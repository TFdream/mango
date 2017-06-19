package com.mindflow.framework.rpc.registry.zookeeper;

import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.common.URLParamName;
import com.mindflow.framework.rpc.registry.AbstractRegistryFactory;
import com.mindflow.framework.rpc.registry.Registry;
import com.mindflow.framework.rpc.util.Constants;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ZookeeperRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistry(URL registryUrl) {
        try {
            int timeout = registryUrl.getIntParameter(URLParamName.registryConnectTimeout.getName(), URLParamName.registryConnectTimeout.getIntValue());
            int sessionTimeout =
                    registryUrl.getIntParameter(URLParamName.registrySessionTimeout.getName(),
                            URLParamName.registrySessionTimeout.getIntValue());
            ZkClient zkClient = new ZkClient(registryUrl.getParameter(Constants.REGISTRY_ADDRESS), sessionTimeout, timeout);
            return new ZookeeperRegistry(registryUrl, zkClient);
        } catch (ZkException e) {
            throw e;
        }
    }
}
