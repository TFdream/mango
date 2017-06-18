package com.mindflow.framework.rpc.codec;

import com.mindflow.framework.rpc.core.DefaultRequest;
import com.mindflow.framework.rpc.core.DefaultResponse;
import com.mindflow.framework.rpc.core.extension.ExtensionLoader;
import com.mindflow.framework.rpc.serializer.Serializer;
import com.mindflow.framework.rpc.util.Constants;
import io.netty.channel.Channel;
import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class DefaultCodec extends AbstractCodec {

    @Override
    public byte[] encode(Channel channel, Object message) throws IOException {

        return serialize(message, ExtensionLoader.getExtensionLoader(Serializer.class).getDefaultExtension());
    }

    @Override
    public Object decode(Channel channel, byte messageType, byte[] data) throws IOException {

        if(messageType == Constants.FLAG_REQUEST) {
            return deserialize(data, DefaultRequest.class, ExtensionLoader.getExtensionLoader(Serializer.class).getDefaultExtension());
        }
        return deserialize(data, DefaultResponse.class, ExtensionLoader.getExtensionLoader(Serializer.class).getDefaultExtension());
    }
}
