package com.mindflow.framework.rpc.transport;

import com.mindflow.framework.rpc.core.DefaultRequest;
import com.mindflow.framework.rpc.core.DefaultResponse;
import com.mindflow.framework.rpc.config.NettyServerConfig;
import com.mindflow.framework.rpc.exception.RpcServiceException;
import com.mindflow.framework.rpc.serializer.Serializer;
import com.mindflow.framework.rpc.serializer.SerializerFactory;
import com.mindflow.framework.rpc.server.MessageHandler;
import com.mindflow.framework.rpc.transport.codec.NettyDecoder;
import com.mindflow.framework.rpc.transport.codec.NettyEncoder;
import com.mindflow.framework.rpc.util.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
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

    private ThreadPoolExecutor pool;    //业务处理线程池
    private Serializer serializer;

    private NettyServerConfig config;

    public NettyServerImpl(NettyServerConfig config){
        this.config = config;
        serializer = SerializerFactory.getSerializer("");
    }

    @Override
    public void bind() throws InterruptedException {
        this.serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, config.getBacklogSize())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_RCVBUF, config.getReceivedBufferSize())
                .childOption(ChannelOption.SO_SNDBUF, config.getSendBufferSize())
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws IOException {

                        ch.pipeline().addLast(new NettyDecoder(serializer, Constants.MAX_FRAME_LENGTH, Constants.HEADER_SIZE, 4), //
                                new NettyEncoder(serializer), //
                                new NettyServerHandler());
                    }
                });

        pool = new ThreadPoolExecutor(config.getCorePoolSize(), config.getMaximumPoolSize(),
                config.getKeepAliveTimeSeconds(), TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                new DefaultThreadFactory(String.format("%s-%s", Constants.FRAMEWORK_NAME, "biz")));


        ChannelFuture channelFuture = this.serverBootstrap.bind(new InetSocketAddress(this.config.getPort())).sync();

        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture f) throws Exception {

                if(f.isSuccess()){
                    logger.info("Rpc Server bind port:{} success", config.getPort());
                } else {
                    logger.error("Rpc Server bind port:{} failure", config.getPort());
                }
            }
        });
        logger.info("Rpc Server bind port:{}", config.getPort());
    }

    @Override
    public void shutdown() {
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
            response.setException(new RpcServiceException("process thread pool is full, reject"));
            response.setProcessTime(System.currentTimeMillis() - processStartTime);
            context.channel().write(response);
        }

    }

    private void processRpcRequest(ChannelHandlerContext context, DefaultRequest request, long processStartTime) {

        DefaultResponse response = MessageHandler.getInstance().invoke(request, processStartTime);

        if(request.getType()!=Constants.REQUEST_ONEWAY){    //非单向调用
            context.writeAndFlush(response);
        }
        logger.info("Rpc server process request:{} end...", request.getRequestId());
    }
}
