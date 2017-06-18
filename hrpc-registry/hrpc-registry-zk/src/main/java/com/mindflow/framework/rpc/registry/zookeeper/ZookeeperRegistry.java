package com.mindflow.framework.rpc.registry.zookeeper;

import com.mindflow.framework.rpc.common.Closeable;
import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.exception.RpcFrameworkException;
import com.mindflow.framework.rpc.registry.AbstractRegistry;
import com.mindflow.framework.rpc.registry.NotifyListener;
import com.mindflow.framework.rpc.util.Constants;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ricky Fung
 */
public class ZookeeperRegistry extends AbstractRegistry implements Closeable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ReentrantLock clientLock = new ReentrantLock();
    private final ReentrantLock serverLock = new ReentrantLock();

    private ZkClient zkClient;

    public ZookeeperRegistry(URL url, ZkClient zkClient) {
        super(url);
        this.zkClient = zkClient;
        IZkStateListener zkStateListener = new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {
                // do nothing
            }

            @Override
            public void handleNewSession() throws Exception {
                logger.info("zkRegistry get new session notify.");

            }

            @Override
            public void handleSessionEstablishmentError(Throwable throwable) throws Exception {

            }
        };
        this.zkClient.subscribeStateChanges(zkStateListener);
    }

    @Override
    protected void doRegister(URL url) {
        try {
            serverLock.lock();
            // 防止旧节点未正常注销
            removeNode(url, ZkNodeType.SERVER);
            createNode(url, ZkNodeType.SERVER);
        } catch (Throwable e) {
            throw new RpcFrameworkException(String.format("Failed to register %s to zookeeper(%s), cause: %s", url, getUrl(), e.getMessage()), e);
        } finally {
            serverLock.unlock();
        }
    }

    @Override
    protected void doUnregister(URL url) {
        try {
            serverLock.lock();
            removeNode(url, ZkNodeType.SERVER);
        } catch (Throwable e) {
            throw new RpcFrameworkException(String.format("Failed to unregister %s to zookeeper(%s), cause: %s", url, getUrl(), e.getMessage()), e);
        } finally {
            serverLock.unlock();
        }
    }

    @Override
    protected void doSubscribe(URL url, NotifyListener listener) {

    }

    @Override
    protected void doUnsubscribe(URL url, NotifyListener listener) {

    }

    @Override
    protected List<URL> doDiscover(URL url) {
        return discoverService(url);
    }

    private void createNode(URL url, ZkNodeType nodeType) {
        String nodeTypePath = ZkUtils.toNodeTypePath(url, nodeType);
        if (!zkClient.exists(nodeTypePath)) {
            zkClient.createPersistent(nodeTypePath, true);
        }
        zkClient.createEphemeral(ZkUtils.toNodePath(url, nodeType), url.toFullUri());
    }

    private void removeNode(URL url, ZkNodeType nodeType) {
        String nodePath = ZkUtils.toNodePath(url, nodeType);
        if (zkClient.exists(nodePath)) {
            zkClient.delete(nodePath);
        }
    }

    private List<URL> discoverService(URL url) {
        try {
            String parentPath = ZkUtils.toNodeTypePath(url, ZkNodeType.SERVER);
            List<String> children = new ArrayList<String>();
            if (zkClient.exists(parentPath)) {
                children = zkClient.getChildren(parentPath);
            }
            return childrenNodeToUrls(parentPath, children);
        } catch (Throwable e) {
            throw new RpcFrameworkException(String.format("Failed to discover service %s from zookeeper(%s), cause: %s", url, getUrl(), e.getMessage()), e);
        }
    }

    private List<URL> childrenNodeToUrls(String parentPath, List<String> children) {
        List<URL> urls = new ArrayList<URL>();
        if (children != null) {
            for (String node : children) {
                String nodePath = parentPath + Constants.PATH_SEPARATOR + node;
                String data = zkClient.readData(nodePath, true);
                try {
                    URL url = URL.parse(data);
                    urls.add(url);
                } catch (Exception e) {
                    logger.warn(String.format("Found malformed urls from ZookeeperRegistry, path=%s", nodePath), e);
                }
            }
        }
        return urls;
    }

    @Override
    public void close() throws Exception {
        this.zkClient.close();
    }
}
