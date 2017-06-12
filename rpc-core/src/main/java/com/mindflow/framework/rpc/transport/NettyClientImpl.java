package com.mindflow.framework.rpc.transport;

import com.mindflow.framework.rpc.*;
import com.mindflow.framework.rpc.config.NettyClientConfig;
import com.mindflow.framework.rpc.exception.TransportException;
import com.mindflow.framework.rpc.transport.codec.NettyDecoder;
import com.mindflow.framework.rpc.transport.codec.NettyEncoder;
import com.mindflow.framework.rpc.util.Constants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class NettyClientImpl implements NettyClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap b = new Bootstrap();

    private final ConcurrentHashMap<Long, ResponseFuture> responseFutureMap =
            new ConcurrentHashMap<>(256);

    private ScheduledExecutorService scheduledExecutorService;

    private NettyClientConfig config;

    public NettyClientImpl(NettyClientConfig config) {
        this.config = config;
    }

    @Override
    public void start() {
        b.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_RCVBUF, config.getReceivedBufferSize())
                .option(ChannelOption.SO_SNDBUF, config.getSendBufferSize())
                .option(ChannelOption.SO_BACKLOG, config.getBacklogSize())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        ch.pipeline().addLast(new NettyDecoder(null, Constants.MAX_FRAME_LENGTH, Constants.HEADER_SIZE, 4), //
                                new NettyEncoder(null), //
                                new NettyClientHandler());
                    }
                });

        this.scheduledExecutorService = Executors.newScheduledThreadPool(5,
                new DefaultThreadFactory(String.format("%s-%s", Constants.FRAMEWORK_NAME, "future")));

        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

            }
        }, 0, 2000, TimeUnit.MILLISECONDS);
    }

    @Override
    public Response invokeSync(String address, Request request, long timeoutInMillis) throws InterruptedException, TransportException {
        return null;
    }

    @Override
    public ResponseFuture invokeAsync(String address, Request request, long timeoutInMillis) throws InterruptedException, TransportException {
        return null;
    }

    @Override
    public void invokeOneway(String address, Request request, long timeoutInMillis) throws InterruptedException, TransportException {

    }

    @Override
    public void shutdown() {

    }

    private class NettyClientHandler extends ChannelInboundHandlerAdapter {
        private Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
                throws Exception {

            logger.info("client read msg:{}, ", msg);

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            logger.error("client caught exception", cause);
            ctx.close();
        }
    }

}
