package mango.cluster.loadbalance;

import mango.core.Request;
import mango.core.extension.SPI;
import mango.rpc.Reference;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@SPI("random")
public interface LoadBalance<T> {

    void setReferences(List<Reference<T>> references);

    Reference<T> select(Request request);
}
