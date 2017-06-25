package mango.cluster.ha;

import mango.cluster.HaStrategy;
import mango.cluster.LoadBalance;
import mango.core.Request;
import mango.core.Response;
import mango.rpc.Reference;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class FailfastHaStrategy<T> implements HaStrategy<T> {

    @Override
    public Response call(Request request, LoadBalance loadBalance) {
        Reference<T> reference = loadBalance.select(request);
        return reference.call(request);
    }
}
