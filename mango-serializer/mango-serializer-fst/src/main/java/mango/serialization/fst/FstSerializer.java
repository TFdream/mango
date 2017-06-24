package mango.serialization.fst;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import mango.codec.Serializer;
import org.nustaq.serialization.FSTConfiguration;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * https://github.com/RuedigerMoeller/fast-serialization/wiki/Serialization
 *
 * @author Ricky Fung
 */
public class FstSerializer implements Serializer {

    private static final LoadingCache<Class<?>, FSTConfiguration> configurationLoadingCache = CacheBuilder.newBuilder()
            .build(new CacheLoader<Class<?>, FSTConfiguration>() {
                @Override
                public FSTConfiguration load(Class<?> cls) throws Exception {
                    return FSTConfiguration.createDefaultConfiguration();
                }
            });

    @Override
    public byte[] serialize(Object msg) throws IOException {
        return getFSTConfiguration(msg.getClass()).asByteArray(msg);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> type) throws IOException {
        return (T) getFSTConfiguration(type).asObject(data);
    }

    private static FSTConfiguration getFSTConfiguration(Class<?> clz) throws IOException {
        try {
            return configurationLoadingCache.get(clz);
        } catch (ExecutionException e) {
            throw new IOException("create FSTConfiguration error, class:"+clz);
        }
    }
}
