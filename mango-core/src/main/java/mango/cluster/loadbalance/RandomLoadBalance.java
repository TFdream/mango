package mango.cluster.loadbalance;

import mango.cluster.LoadBalance;
import mango.core.Request;
import mango.rpc.Reference;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class RandomLoadBalance<T> implements LoadBalance<T> {
    private volatile List<Reference<T>> references;

    @Override
    public void setReferences(List<Reference<T>> references) {
        this.references = references;
    }

    @Override
    public Reference select(Request request) {
        int idx = (int) (ThreadLocalRandom.current().nextDouble() * references.size());
        return references.get(idx);
    }
}
