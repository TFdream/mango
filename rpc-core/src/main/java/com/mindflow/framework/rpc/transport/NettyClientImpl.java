package com.mindflow.framework.rpc.transport;

import com.mindflow.framework.rpc.core.*;
import com.mindflow.framework.rpc.config.NettyClientConfig;
import com.mindflow.framework.rpc.exception.TransportException;
import com.mindflow.framework.rpc.serializer.Serializer;
import com.mindflow.framework.rpc.serializer.SerializerFactory;
import com.mindflow.framework.rpc.transport.codec.NettyDecoder;
import com.mindflow.framework.rpc.transport.codec.NettyEncoder;
import com.mindflow.framework.rpc.util.Constants;
import com.mindflow.framework.rpc.util.NetUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

    private final ConcurrentHashMap<String, ChannelWrapper> channelTable =
            new ConcurrentHashMap<>();

    private Serializer serializer;

    private ScheduledExecutorService scheduledExecutorService;

    private NettyClientConfig config;

    public NettyClientImpl(NettyClientConfig config) {
        this.config = config;
        serializer = SerializerFactory.getSerializer("");
    }

    @Override
    public void start() {
        b.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_RCVBUF, config.getReceivedBufferSize())
                .option(ChannelOption.SO_SNDBUF, config.getSendBufferSize())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        ch.pipeline().addLast(new NettyDecoder(serializer, Constants.MAX_FRAME_LENGTH, Constants.HEADER_SIZE, 4), //
                                new NettyEncoder(serializer), //
                                new NettyClientHandler());
                    }
                });

        this.scheduledExecutorService = Executors.newScheduledThreadPool(5,
                new DefaultThreadFactory(String.format("%s-%s", Constants.FRAMEWORK_NAME, "future")));

        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                scanRpcFutureTable();
            }
        }, 0, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public Response invokeSync(String address, final Request request, long timeoutInMillis) throws InterruptedException, TransportException {
        Channel channel = getChannel(address);

        if (channel != null && channel.isActive()) {

            final ResponseFuture<Response> rpcFuture = new DefaultResponseFuture<>(timeoutInMillis);
            this.responseFutureMap.put(request.getRequestId(), rpcFuture);
            //写数据
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()) {
                        logger.info("send success, request id:{}", request.getRequestId());

                    } else {
                        logger.info("send failure, request id:{}", request.getRequestId());
                        responseFutureMap.remove(request.getRequestId());
                        rpcFuture.setFailure(future.cause());
                    }
                }
            });

            return rpcFuture.get();
        } else {
            throw new TransportException("channel not active. request id:"+request.getRequestId());
        }
    }

    @Override
    public ResponseFuture invokeAsync(String address, final Request request, long timeoutInMillis) throws InterruptedException, TransportException {
        return null;
    }

    @Override
    public void invokeOneway(String address, final Request request, long timeoutInMillis) throws InterruptedException, TransportException {

    }

    @Override
    public void shutdown() {
        this.scheduledExecutorService.shutdown();
        this.group.shutdownGracefully();
    }

    private class NettyClientHandler extends ChannelInboundHandlerAdapter {
        private Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
                throws Exception {

            logger.info("client read msg:{}, ", msg);
            if(msg instanceof Response) {
                DefaultResponse response = (DefaultResponse) msg;

                ResponseFuture<Response> rpcFuture =responseFutureMap.get(response.getRequestId());
                if(rpcFuture!=null) {
                    responseFutureMap.remove(response.getRequestId());
                    rpcFuture.setResult(response);
                }

            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            logger.error("client caught exception", cause);
            ctx.close();
        }
    }

    private Channel getChannel(String address) throws InterruptedException {

        ChannelWrapper cw = this.channelTable.get(address);
        if (cw != null && cw.isActive()) {
            return cw.getChannel();
        }

        synchronized (this){
            // 发起异步连接操作
            ChannelFuture channelFuture = b.connect(NetUtils.parseSocketAddress(address)).sync();
            cw = new ChannelWrapper(channelFuture);
            this.channelTable.put(address, cw);
        }

        ChannelFuture channelFuture = cw.getChannelFuture();
        long timeout = 2000;
        if (channelFuture.awaitUninterruptibly(timeout)) {
            if (cw.isActive()) {
                logger.info("createChannel: connect remote host[{}] success, {}", address, channelFuture.toString());
            } else {
                logger.warn("createChannel: connect remote host[" + address + "] failed, " + channelFuture.toString(), channelFuture.cause());
            }
        } else {
            logger.warn("createChannel: connect remote host[{}] timeout {}ms, {}", address, timeout, channelFuture);
        }
        return cw.getChannel();
    }

    /**定时清理超时Future**/
    private void scanRpcFutureTable() {

        long currentTime = System.currentTimeMillis();
        logger.info("scan timeout RpcFuture, currentTime:{}", currentTime);

        final List<ResponseFuture> timeoutFutureList = new ArrayList<>();
        Iterator<Map.Entry<Long, ResponseFuture>> it = this.responseFutureMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, ResponseFuture> next = it.next();
            ResponseFuture future = next.getValue();

            if (future.isTimeout()) {  //超时
                it.remove();
                timeoutFutureList.add(future);
            }
        }

        for (ResponseFuture future : timeoutFutureList) {
            //释放资源
        }
    }
}
