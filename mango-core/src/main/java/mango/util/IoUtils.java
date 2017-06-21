package mango.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Ricky Fung
 */
public class IoUtils {

    public static void closeQuietly(InputStream input){
        closeQuietly((Closeable)input);
    }

    public static void closeQuietly(OutputStream output){
        closeQuietly((Closeable)output);
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if(closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(Closeable... closeables){
        for (Closeable closeable : closeables){
            closeQuietly(closeable);
        }
    }

    public static long copy(InputStream in, OutputStream out, int bufferSize) throws IOException {
        byte[] buff = new byte[bufferSize];
        return copy(in, out, buff);
    }

    public static long copy(InputStream in, OutputStream out) throws IOException {
        byte[] buff = new byte[1024];
        return copy(in, out, buff);
    }

    public static long copy(InputStream in, OutputStream out, byte[] buff) throws IOException {
        long count = 0;
        int len = -1;
        while((len=in.read(buff, 0, buff.length))!=-1){
            out.write(buff, 0, len);
            count += len;
        }
        return count;
    }
}
