package mango.serialization.jdk;

import mango.codec.Serializer;
import mango.util.IoUtils;

import java.io.*;

/**
 * @author Ricky Fung
 */
public class JdkSerializer implements Serializer {

    @Override
    public byte[] serialize(Object msg) throws IOException {
        ObjectOutputStream output = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            output = new ObjectOutputStream(baos);
            output.writeObject(msg);
            output.flush();

            return baos.toByteArray();
        } finally {
            IoUtils.closeQuietly(baos);
            output.close();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> type) throws IOException {
        // Read Obj from File
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(new ByteArrayInputStream(data));
            return (T) input.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("class not found", e);
        } finally {
            IoUtils.closeQuietly(input);
        }
    }
}
