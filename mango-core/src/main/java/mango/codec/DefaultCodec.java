package mango.codec;

import mango.core.DefaultRequest;
import mango.core.DefaultResponse;
import mango.core.extension.ExtensionLoader;
import mango.serializer.Serializer;
import mango.util.Constants;
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
