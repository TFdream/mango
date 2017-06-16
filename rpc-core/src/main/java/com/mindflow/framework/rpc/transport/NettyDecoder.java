package com.mindflow.framework.rpc.transport;

import com.mindflow.framework.rpc.codec.Codec;
import com.mindflow.framework.rpc.core.DefaultResponse;
import com.mindflow.framework.rpc.core.Response;
import com.mindflow.framework.rpc.exception.RpcFrameworkException;
import com.mindflow.framework.rpc.util.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private int maxFrameLength;
    private Codec codec;

    public NettyDecoder(Codec codec, int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        this.codec = codec;
        this.maxFrameLength = maxFrameLength;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (in == null) {
            return null;
        }

        if (in.readableBytes() <= Constants.HEADER_SIZE) {
            return null;
        }

        in.markReaderIndex();

        short magicType = in.readShort();
        if (magicType != Constants.NETTY_MAGIC_TYPE) {
            in.resetReaderIndex();
            throw new RpcFrameworkException("RpcDecoder transport header not support, type: " + magicType);
        }

        byte messageType = in.readByte();
        long requestId = in.readLong();
        int dataLength = in.readInt();

        // FIXME 如果dataLength过大，可能导致问题
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return null;
        }

        if (maxFrameLength > 0 && dataLength > maxFrameLength) {
            logger.warn(
                    "NettyDecoder transport data content length over of limit, size: {}  > {}. remote={} local={}",
                    dataLength, maxFrameLength, ctx.channel().remoteAddress(), ctx.channel()
                            .localAddress());
            Exception e = new RpcFrameworkException("NettyDecoder transport data content length over of limit, size: "
                    + dataLength + " > " + maxFrameLength);

            if (messageType == Constants.FLAG_REQUEST) {
                Response response = buildExceptionResponse(requestId, e);
                ctx.write(response);
                throw e;
            } else {
                throw e;
            }
        }

        byte[] data = new byte[dataLength];
        in.readBytes(data);

        try {
            return codec.decode(ctx.channel(), messageType, data);
        } catch (Exception e) {
            if (messageType == Constants.FLAG_REQUEST) {
                Response response = buildExceptionResponse(requestId, e);
                ctx.write(response);
                return null;
            } else {
                Response response = buildExceptionResponse(requestId, e);
                return response;
            }
        }
    }

    private Response buildExceptionResponse(long requestId, Exception e) {
        DefaultResponse response = new DefaultResponse();
        response.setRequestId(requestId);
        response.setException(e);
        return response;
    }
}
