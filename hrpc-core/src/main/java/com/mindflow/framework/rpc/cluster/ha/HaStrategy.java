package com.mindflow.framework.rpc.cluster.ha;

import com.mindflow.framework.rpc.cluster.loadbalance.LoadBalance;
import com.mindflow.framework.rpc.core.Request;
import com.mindflow.framework.rpc.core.Response;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface HaStrategy {

    Response call(Request request, LoadBalance loadBalance);
}
