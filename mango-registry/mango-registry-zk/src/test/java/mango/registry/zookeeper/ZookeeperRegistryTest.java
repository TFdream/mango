package mango.registry.zookeeper;

import mango.common.URL;
import mango.common.URLParam;
import mango.util.Constants;
import org.I0Itec.zkclient.ZkClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class ZookeeperRegistryTest {

    private ZookeeperRegistry zookeeperRegistry;
    private URL url;
    private String zkServers = "localhost:2181";
    private String interfaceName = "com.mindflow.rpc.demo.service.DemoService";

    //@Before
    public void init() {
        url = new URL(URLParam.codec.getName(), "192.168.1.100", Constants.DEFAULT_PORT, interfaceName);

        ZkClient zkClient = new ZkClient(zkServers, 60*1000, 2000);
        zookeeperRegistry = new ZookeeperRegistry(url, zkClient);
    }

    @Test
    @Ignore
    public void testRegister() throws Exception {
        zookeeperRegistry.register(url);

        sleep(5000);
    }

    @Test
    @Ignore
    public void testUnregister() throws Exception {
        zookeeperRegistry.unregister(url);

        sleep(5000);
    }

    @Test
    @Ignore
    public void testDiscover() throws Exception {
        List<URL> urls = zookeeperRegistry.discover(url);
        System.out.println(urls);
    }

    //@After
    public void destroy() throws Exception {
        zookeeperRegistry.close();
    }

    private void sleep(long timeInMillis) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeInMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
