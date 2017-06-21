package mango.codec;

import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public abstract class AbstractCodec implements Codec {

    protected byte[] serialize(Object message, Serializer serializer) throws IOException {
        if (message == null) {
            return null;
        }
        return serializer.serialize(message);
    }

    protected Object deserialize(byte[] data, Class<?> type, Serializer serializer) throws IOException {
        if (data == null) {
            return null;
        }
        return serializer.deserialize(data, type);
    }
}
