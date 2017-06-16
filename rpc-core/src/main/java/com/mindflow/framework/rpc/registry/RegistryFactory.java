package com.mindflow.framework.rpc.registry;

import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.core.extension.SPI;
import com.mindflow.framework.rpc.core.extension.Scope;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@SPI(scope = Scope.SINGLETON)
public interface RegistryFactory {

    Registry getRegistry(URL url);
}
