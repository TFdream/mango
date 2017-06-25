package mango.cluster;

import mango.core.Request;
import mango.core.Response;
import mango.core.extension.SPI;
import mango.core.extension.Scope;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@SPI(scope = Scope.PROTOTYPE)
public interface HaStrategy<T> {

    Response call(Request request, LoadBalance loadBalance);
}
