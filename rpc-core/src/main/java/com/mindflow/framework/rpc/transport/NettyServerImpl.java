package com.mindflow.framework.rpc.transport;

import com.mindflow.framework.rpc.core.DefaultRequest;
import com.mindflow.framework.rpc.core.DefaultResponse;
import com.mindflow.framework.rpc.config.NettyServerConfig;
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

    private NettyServerConfig config;

    public NettyServerImpl(NettyServerConfig config){
        this.config = config;
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

                        ch.pipeline().addLast(new NettyDecoder(null, Constants.MAX_FRAME_LENGTH, Constants.HEADER_SIZE, 4), //
                                new NettyEncoder(null), //
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
        logger.info("Rpc Server bind port:"+config.getPort());
    }

    @Override
    public void shutdown() {

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
            super.exceptionCaught(ctx, cause);
            logger.error("捕获异常", cause);
        }
    }

    private void processRpcRequest(final ChannelHandlerContext context, final DefaultRequest request) {

        this.pool.execute(new Runnable() {
            @Override
            public void run() {

                DefaultResponse response = new DefaultResponse();
                response.setRequestId(request.getRequestId());
                response.setResult(null);

                if(true){    //非单向调用
                    context.writeAndFlush(response);
                }
                logger.info("Rpc server process request:{} end...", request.getRequestId());
            }
        });
    }
}
