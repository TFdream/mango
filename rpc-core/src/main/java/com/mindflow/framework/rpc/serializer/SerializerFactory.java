package com.mindflow.framework.rpc.serializer;

import com.mindflow.framework.rpc.core.extension.ExtensionLoader;
import com.mindflow.framework.rpc.util.StringUtils;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class SerializerFactory {

    public static Serializer getSerializer(String protocol) {
        if(StringUtils.isBlank(protocol)) {
            return ExtensionLoader.getExtensionLoader(Serializer.class).getDefaultExtension();
        }
        return ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(protocol);
    }
}
