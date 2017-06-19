package com.mindflow.framework.rpc.cluster.loadbalance;

import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.core.extension.SPI;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@SPI("random")
public interface LoadBalance {

    URL select(List<URL> urls);
}
