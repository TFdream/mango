package mango.cluster;

import mango.core.Request;
import mango.core.extension.SPI;
import mango.core.extension.Scope;
import mango.rpc.Reference;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@SPI(scope = Scope.PROTOTYPE)
public interface LoadBalance<T> {

    void setReferences(List<Reference<T>> references);

    Reference<T> select(Request request);
}
