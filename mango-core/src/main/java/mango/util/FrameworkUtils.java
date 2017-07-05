package mango.util;

import mango.common.URL;
import mango.common.URLParam;
import mango.config.ProtocolConfig;
import mango.config.RegistryConfig;
import mango.core.Request;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class FrameworkUtils {

    /**
     * 默认本地注册中心
     * @return local registry
     */
    public static RegistryConfig getDefaultRegistryConfig(){
        RegistryConfig local = new RegistryConfig();
        local.setProtocol(Constants.REGISTRY_PROTOCOL_LOCAL);
        return local;
    }

    public static ProtocolConfig getDefaultProtocolConfig() {
        ProtocolConfig pc = new ProtocolConfig();
        pc.setId(Constants.FRAMEWORK_NAME);
        pc.setName(Constants.FRAMEWORK_NAME);
        pc.setPort(Constants.DEFAULT_PORT);
        return pc;
    }

    /**
     * protocol key: protocol://host:port/group/interface/version
     *
     * @param url
     * @return
     */
    public static String getProtocolKey(URL url) {
        return url.getProtocol() + Constants.PROTOCOL_SEPARATOR + url.getServerAndPort() + Constants.PATH_SEPARATOR
                + url.getGroup() + Constants.PATH_SEPARATOR + url.getPath() + Constants.PATH_SEPARATOR + url.getVersion();
    }

    public static String getServiceKey(URL url) {
        return getServiceKey(url.getGroup(), url.getPath(), url.getVersion());
    }

    public static String getServiceKey(Request request) {
        String version = getValueFromRequest(request, URLParam.version.name(), URLParam.version.getValue());
        String group = getValueFromRequest(request, URLParam.group.name(), URLParam.group.getValue());

        return getServiceKey(group, request.getInterfaceName(), version);
    }

    public static String getValueFromRequest(Request request, String key, String defaultValue) {
        String value = defaultValue;
        if (request.getAttachments() != null && request.getAttachments().containsKey(key)) {
            value = request.getAttachments().get(key);
        }
        return value;
    }

    /**
     * serviceKey: group/interface/version
     *
     * @param group
     * @param interfaceName
     * @param version
     * @return
     */
    private static String getServiceKey(String group, String interfaceName, String version) {
        return group + Constants.PATH_SEPARATOR + interfaceName + Constants.PATH_SEPARATOR + version;
    }
}
