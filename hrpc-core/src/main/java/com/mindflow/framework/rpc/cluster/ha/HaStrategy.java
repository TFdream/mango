package com.mindflow.framework.rpc.cluster.ha;

import com.mindflow.framework.rpc.cluster.LoadBalanceStrategy;
import com.mindflow.framework.rpc.core.Request;
import com.mindflow.framework.rpc.core.Response;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface HaStrategy {

    Response call(Request request, LoadBalanceStrategy loadBalance);
}
