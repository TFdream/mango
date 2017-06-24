package mango.serialization.msgpack;

import com.fasterxml.jackson.databind.ObjectMapper;
import mango.codec.Serializer;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import java.io.IOException;

/**
 * msgpack-java: https://github.com/msgpack/msgpack-java
 *
 * @author Ricky Fung
 */
public class MessagePackSerializer implements Serializer {

    private final ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());;

    @Override
    public byte[] serialize(Object msg) throws IOException {
        return objectMapper.writeValueAsBytes(msg);
    }

    @Override
    public <T> T deserialize(byte[] buf, Class<T> type) throws IOException {
        return objectMapper.readValue(buf, type);
    }
}
