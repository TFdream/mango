package com.mindflow.framework.registry.zookeeper;

import com.mindflow.framework.registry.DiscoveryService;
import com.mindflow.framework.registry.NotifyListener;
import com.mindflow.framework.rpc.URL;
import java.util.List;

/**
 * @author Ricky Fung
 */
public class ZookeeperDiscoveryService implements DiscoveryService {
    private String address;
    private int timeout;

    public ZookeeperDiscoveryService(String address, int timeout) {
        this.address = address;
        this.timeout = timeout;
    }

    @Override
    public void subscribe(URL url, NotifyListener listener) {

    }

    @Override
    public void unsubscribe(URL url, NotifyListener listener) {

    }

    @Override
    public List<URL> discover(URL url) throws Exception {
        return null;
    }
}
