package mango.cluster.ha;

import mango.cluster.loadbalance.LoadBalance;
import mango.core.Request;
import mango.core.Response;
import mango.core.extension.SPI;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@SPI
public interface HaStrategy<T> {

    Response call(Request request, LoadBalance loadBalance);
}
