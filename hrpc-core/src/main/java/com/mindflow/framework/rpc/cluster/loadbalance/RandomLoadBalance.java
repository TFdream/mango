package com.mindflow.framework.rpc.cluster.loadbalance;

import com.mindflow.framework.rpc.common.URL;
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
