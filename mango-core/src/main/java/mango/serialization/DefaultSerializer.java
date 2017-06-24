package mango.serialization;

import mango.codec.Serializer;
import java.io.IOException;

/**
 * @author Ricky Fung
 */
public class DefaultSerializer implements Serializer {

    @Override
    public byte[] serialize(Object msg) throws IOException {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> type) throws IOException {
        return null;
    }
}
