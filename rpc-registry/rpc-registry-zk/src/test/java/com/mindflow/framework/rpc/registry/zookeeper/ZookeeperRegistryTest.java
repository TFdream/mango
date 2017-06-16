package com.mindflow.framework.rpc.registry.zookeeper;

import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.common.URLParamName;
import com.mindflow.framework.rpc.util.Constants;
import org.I0Itec.zkclient.ZkClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class ZookeeperRegistryTest {

    private ZookeeperRegistry zookeeperRegistry;
    private URL url;
    private String zkServers = "localhost:2181";

    @Before
    private void init() {
        url = new URL(URLParamName.codec.getName(), "192.168.1.100", Constants.DEFAULT_PORT, "com.mindflow.rpc.demo.service.DemoService");

        ZkClient zkClient = new ZkClient(zkServers, 60*1000, 2000);
        zookeeperRegistry = new ZookeeperRegistry(url, zkClient);
    }

    @Test
    public void testRegister() throws Exception {
        zookeeperRegistry.register(url);
    }

    @Test
    public void testUnregister() throws Exception {
        zookeeperRegistry.unregister(url);
    }

    @Test
    public void testDiscover() throws Exception {
        List<URL> urls = zookeeperRegistry.discover(url);
        System.out.println(urls);
    }

    @After
    public void destroy() throws Exception {
        zookeeperRegistry.close();
    }
}
