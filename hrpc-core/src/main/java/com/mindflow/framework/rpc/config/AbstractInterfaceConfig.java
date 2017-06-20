package com.mindflow.framework.rpc.config;

import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.common.URLParamName;
import com.mindflow.framework.rpc.registry.Registry;
import com.mindflow.framework.rpc.util.Constants;
import com.mindflow.framework.rpc.util.NetUtils;
import com.mindflow.framework.rpc.util.StringUtils;

import java.util.*;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class AbstractInterfaceConfig {
    private String id;
    protected String interfaceName;
    protected String group;
    protected String version;
    protected Integer timeout;
    protected Integer retries;

    //server暴露服务使用的协议，暴露可以使用多种协议，但client只能用一种协议进行访问，原因是便于client的管理
    protected List<ProtocolConfig> protocols;
    // 注册中心的配置列表
    protected List<RegistryConfig> registries;

    protected List<URL> loadRegistryUrls() {
        List<URL> registryList = new ArrayList<URL>();
        if (registries != null && !registries.isEmpty()) {
            for (RegistryConfig config : registries) {
                String address = config.getAddress();
                String protocol = config.getProtocol();
                if (StringUtils.isBlank(address)) {
                    address = NetUtils.LOCALHOST + Constants.HOST_PORT_SEPARATOR + Constants.DEFAULT_INT_VALUE;
                    protocol = Constants.REGISTRY_PROTOCOL_LOCAL;
                }
                Map<String, String> map = new HashMap<>();

                map.put(URLParamName.path.getName(), Registry.class.getName());
                map.put(URLParamName.timestamp.getName(), String.valueOf(System.currentTimeMillis()));
                map.put(URLParamName.protocol.getName(), protocol);
                map.put(URLParamName.registryConnectTimeout.getName(), String.valueOf(config.getConnectTimeout()));
                map.put(URLParamName.registrySessionTimeout.getName(), String.valueOf(config.getSessionTimeout()));

                String[] arr = address.split(Constants.HOST_PORT_SEPARATOR);
                URL url = new URL(protocol, arr[0], Integer.parseInt(arr[1]), Registry.class.getName(), map);
                registryList.add(url);
            }
        }
        return registryList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public List<ProtocolConfig> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<ProtocolConfig> protocols) {
        this.protocols = protocols;
    }

    public void setProtocol(ProtocolConfig protocol) {
        this.protocols = Collections.singletonList(protocol);
    }
    public List<RegistryConfig> getRegistries() {
        return registries;
    }

    public void setRegistries(List<RegistryConfig> registries) {
        this.registries = registries;
    }

    public void setRegistry(RegistryConfig registry) {
        this.registries = Collections.singletonList(registry);
    }

}
