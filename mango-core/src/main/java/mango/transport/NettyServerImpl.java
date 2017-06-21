package mango.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import mango.codec.Codec;
import mango.common.URL;
import mango.common.URLParam;
import mango.core.DefaultRequest;
import mango.core.DefaultResponse;
import mango.core.extension.ExtensionLoader;
import mango.exception.RpcFrameworkException;
import mango.rpc.MessageRouter;
import mango.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class NettyServerImpl implements NettyServer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ServerBootstrap serverBootstrap = new ServerBootstrap();
    private volatile boolean available;

    private volatile boolean initialized;
    private volatile boolean destroyed;

    private ThreadPoolExecutor pool;    //业务处理线程池
    private Codec codec;
    private MessageRouter router;
    private URL url;

    public NettyServerImpl(URL url, MessageRouter router){
        this.url = url;
        this.router = router;
        codec = ExtensionLoader.getExtensionLoader(Codec.class).getExtension(url.getParameter(URLParam.codec.getName(), URLParam.codec.getValue()));
    }

    @Override
    public synchronized boolean open() {

        if(initialized) {
            logger.warn("NettyServer ServerChannel init: url=" + url);
            return true;
        }
        initialized = true;

        if (isAvailable()) {
            logger.warn("NettyServer ServerChannel already Open: url=" + url);
            return true;
        }

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

                        ch.pipeline().addLast(new NettyDecoder(codec, Constants.MAX_FRAME_LENGTH, Constants.HEADER_SIZE, 4), //
                                new NettyEncoder(codec), //
                                new NettyServerHandler());
                    }
                });

        pool = new ThreadPoolExecutor(url.getIntParameter(URLParam.minWorkerThread.getName(), URLParam.minWorkerThread.getIntValue()),
                url.getIntParameter(URLParam.maxWorkerThread.getName(), URLParam.maxWorkerThread.getIntValue()),
                120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                new DefaultThreadFactory(String.format("%s-%s", Constants.FRAMEWORK_NAME, "biz")));

        try {
            final int port = url.getPort();
            ChannelFuture channelFuture = this.serverBootstrap.bind(new InetSocketAddress(port)).sync();

            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture f) throws Exception {

                    if(f.isSuccess()){
                        logger.info("Rpc Server bind port:{} success", port);
                    } else {
                        logger.error("Rpc Server bind port:{} failure", port);
                    }
                }
            });
            logger.info("Rpc Server bind port:{}", port);
            available = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return available;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public synchronized void shutdown() {
        if(destroyed) {
            return;
        }
        destroyed = true;
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
        this.pool.shutdown();
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

                    processRpcRequest(context, request, processStartTime);
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
