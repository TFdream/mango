package com.mindflow.framework.rpc.registry.zookeeper;

import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.util.Constants;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ZkUtils {

    public static String toGroupPath(URL url) {
        return Constants.ZOOKEEPER_REGISTRY_NAMESPACE + Constants.PATH_SEPARATOR + url.getGroup();
    }

    public static String toServicePath(URL url) {
        return toGroupPath(url) + Constants.PATH_SEPARATOR + url.getPath();
    }

    public static String toNodeTypePath(URL url, ZkNodeType nodeType) {
        return toServicePath(url) + Constants.PATH_SEPARATOR + nodeType.getValue();
    }

    public static String toNodePath(URL url, ZkNodeType nodeType) {
        return toNodeTypePath(url, nodeType) + Constants.PATH_SEPARATOR + url.getServerAndPort();
    }
}
