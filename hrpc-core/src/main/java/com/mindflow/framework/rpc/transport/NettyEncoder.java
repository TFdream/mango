package com.mindflow.framework.rpc.transport;

import com.mindflow.framework.rpc.codec.Codec;
import com.mindflow.framework.rpc.core.DefaultResponse;
import com.mindflow.framework.rpc.core.Request;
import com.mindflow.framework.rpc.core.Response;
import com.mindflow.framework.rpc.util.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class NettyEncoder extends MessageToByteEncoder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Codec codec;

    public NettyEncoder(Codec codec) {
        this.codec = codec;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {

        long requestId = getRequestId(msg);
        byte[] data = null;

        if (msg instanceof Response) {
            try {
                data = codec.encode(ctx.channel(), msg);
            } catch (Exception e) {
                logger.error("RpcEncoder encode error, requestId=" + requestId, e);
                Response response = buildExceptionResponse(requestId, e);
                data = codec.encode(ctx.channel(), response);
            }
        } else {
            data = codec.encode(ctx.channel(), msg);
        }

        out.writeShort(Constants.NETTY_MAGIC_TYPE);
        out.writeByte(getType(msg));
        out.writeLong(requestId);
        out.writeInt(data.length);

        out.writeBytes(data);
    }

    private byte getType(Object message) {
        if (message instanceof Request) {
            return Constants.FLAG_REQUEST;
        } else if (message instanceof Response) {
            return Constants.FLAG_RESPONSE;
        } else {
            return Constants.FLAG_OTHER;
        }
    }

    private long getRequestId(Object message) {
        if (message instanceof Request) {
            return ((Request) message).getRequestId();
        } else if (message instanceof Response) {
            return ((Response) message).getRequestId();
        } else {
            return 0;
        }
    }

    private Response buildExceptionResponse(long requestId, Exception e) {
        DefaultResponse response = new DefaultResponse();
        response.setRequestId(requestId);
        response.setException(e);
        return response;
    }
}
