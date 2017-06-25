package mango.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import mango.common.URL;
import mango.common.URLParam;
import mango.core.DefaultRequest;
import mango.core.DefaultResponse;
import mango.exception.RpcFrameworkException;
import mango.rpc.MessageRouter;
import mango.rpc.RpcContext;
import mango.util.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class NettyServerImpl extends AbstractServer {

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ServerBootstrap serverBootstrap = new ServerBootstrap();

    private ThreadPoolExecutor pool;    //业务处理线程池
    private MessageRouter router;

    private volatile boolean initializing = false;

    public NettyServerImpl(URL url, MessageRouter router){
        super(url);

        this.localAddress = new InetSocketAddress(url.getPort());
        this.router = router;
        this.pool = new ThreadPoolExecutor(url.getIntParameter(URLParam.minWorkerThread.getName(), URLParam.minWorkerThread.getIntValue()),
                url.getIntParameter(URLParam.maxWorkerThread.getName(), URLParam.maxWorkerThread.getIntValue()),
                120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                new DefaultThreadFactory(String.format("%s-%s", Constants.FRAMEWORK_NAME, "biz")));
    }

    @Override
    public synchronized boolean open() {
        if(initializing) {
            logger.warn("NettyServer ServerChannel is initializing: url=" + url);
            return true;
        }
        initializing = true;

        if (state.isAvailable()) {
            logger.warn("NettyServer ServerChannel has initialized: url=" + url);
            return true;
        }
        // 最大响应包限制
        final int maxContentLength = url.getIntParameter(URLParam.maxContentLength.getName(),
                URLParam.maxContentLength.getIntValue());

        this.serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_RCVBUF, url.getIntParameter(URLParam.bufferSize.getName(), URLParam.bufferSize.getIntValue()))
                .childOption(ChannelOption.SO_SNDBUF, url.getIntParameter(URLParam.bufferSize.getName(), URLParam.bufferSize.getIntValue()))
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws IOException {

                        ch.pipeline().addLast(new NettyDecoder(codec, url, maxContentLength, Constants.HEADER_SIZE, 4), //
                                new NettyEncoder(codec, url), //
                                new NettyServerHandler());
                    }
                });

        try {
            ChannelFuture channelFuture = this.serverBootstrap.bind(this.localAddress).sync();

            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture f) throws Exception {

                    if(f.isSuccess()){
                        logger.info("Rpc Server bind port:{} success", url.getPort());
                    } else {
                        logger.error("Rpc Server bind port:{} failure", url.getPort());
                    }
                }
            });
        } catch (InterruptedException e) {
            logger.error(String.format("NettyServer bind to address:%s failure", this.localAddress), e);
            throw new RpcFrameworkException(String.format("NettyClient connect to address:%s failure", this.localAddress), e);
        }
        state = ChannelState.AVAILABLE;
        return true;
    }

    @Override
    public boolean isAvailable() {
        return state.isAvailable();
    }

    @Override
    public boolean isClosed() {
        return state.isClosed();
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public void close() {
        close(0);
    }

    @Override
    public synchronized void close(int timeout) {

        if (state.isClosed()) {
            logger.info("NettyServer close fail: already close, url={}", url.getUri());
            return;
        }

        try {
            this.bossGroup.shutdownGracefully();
            this.workerGroup.shutdownGracefully();
            this.pool.shutdown();

            state = ChannelState.CLOSED;
        } catch (Exception e) {
            logger.error("NettyServer close Error: url=" + url.getUri(), e);
        }
    }

    class NettyServerHandler extends SimpleChannelInboundHandler<DefaultRequest> {

        @Override
        protected void channelRead0(ChannelHandlerContext context, DefaultRequest request) throws Exception {

            logger.info("Rpc server receive request id:{}", request.getRequestId());
            //处理请求
            processRpcRequest(context, request);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logger.error("NettyServerHandler exceptionCaught: remote=" + ctx.channel().remoteAddress()
                    + " local=" + ctx.channel().localAddress(), cause);
            ctx.channel().close();
        }
    }

    /**处理客户端请求**/
    private void processRpcRequest(final ChannelHandlerContext context, final DefaultRequest request) {
        final long processStartTime = System.currentTimeMillis();
        try {
            this.pool.execute(new Runnable() {
                @Override
                public void run() {

                    try {
                        RpcContext.init(request);
                        processRpcRequest(context, request, processStartTime);
                    } finally {
                        RpcContext.destroy();
                    }

                }
            });
        } catch (RejectedExecutionException e) {
            DefaultResponse response = new DefaultResponse();
            response.setRequestId(request.getRequestId());
            response.setException(new RpcFrameworkException("process thread pool is full, reject"));
            response.setProcessTime(System.currentTimeMillis() - processStartTime);
            context.channel().write(response);
        }

    }

    private void processRpcRequest(ChannelHandlerContext context, DefaultRequest request, long processStartTime) {

        DefaultResponse response = (DefaultResponse) this.router.handle(request);//;
        response.setProcessTime(System.currentTimeMillis() - processStartTime);
        if(request.getType()!=Constants.REQUEST_ONEWAY){    //非单向调用
            context.writeAndFlush(response);
        }
        logger.info("Rpc server process request:{} end...", request.getRequestId());
    }
}
