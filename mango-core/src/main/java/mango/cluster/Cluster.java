package mango.cluster;

import mango.core.extension.SPI;
import mango.core.extension.Scope;
import mango.rpc.Caller;
import mango.rpc.Reference;

import java.util.List;

/**
 * @author Ricky Fung
 */
@SPI(scope = Scope.PROTOTYPE)
public interface Cluster<T> extends Caller<T> {

    void setLoadBalance(LoadBalance<T> loadBalance);

    void setHaStrategy(HaStrategy<T> haStrategy);

    List<Reference<T>> getReferences();

    LoadBalance<T> getLoadBalance();
}
