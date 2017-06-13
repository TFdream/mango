package com.mindflow.framework.rpc.registry.zookeeper;

import com.mindflow.framework.rpc.registry.RegistryService;
import com.mindflow.framework.rpc.URL;

/**
 * @author Ricky Fung
 */
public class ZookeeperRegistryService implements RegistryService {
    private String address;
    private int timeout;

    public ZookeeperRegistryService(String address, int timeout) {
        this.address = address;
        this.timeout = timeout;
    }

    @Override
    public void register(URL url) throws Exception {

    }

    @Override
    public void unregister(URL url) throws Exception {

    }
}
