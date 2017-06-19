package com.mindflow.framework.rpc.util;

import com.mindflow.framework.rpc.config.ProtocolConfig;
import com.mindflow.framework.rpc.config.RegistryConfig;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class HRpcUtils {

    /**
     * 默认本地注册中心
     * @return local registry
     */
    public static RegistryConfig getDefaultRegistryConfig(){
        RegistryConfig local = new RegistryConfig();
        local.setProtocol("local");
        return local;
    }

    public static ProtocolConfig getDefaultProtocolConfig() {
        ProtocolConfig pc = new ProtocolConfig();
        pc.setId(Constants.FRAMEWORK_NAME);
        pc.setName(Constants.FRAMEWORK_NAME);
        return pc;
    }

}
