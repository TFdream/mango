package mango.cluster.loadbalance;

import mango.common.URL;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class RandomLoadBalance implements LoadBalance {

    @Override
    public URL select(List<URL> urls) {
        int idx = (int) (ThreadLocalRandom.current().nextDouble() * urls.size());
        return urls.get(idx);
    }
}
