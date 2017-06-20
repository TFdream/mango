package mango.cluster.ha;

import mango.cluster.loadbalance.LoadBalance;
import mango.core.Request;
import mango.core.Response;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface HaStrategy {

    Response call(Request request, LoadBalance loadBalance);
}
