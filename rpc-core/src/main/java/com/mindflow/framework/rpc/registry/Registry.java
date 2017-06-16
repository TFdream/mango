package com.mindflow.framework.rpc.registry;

import com.mindflow.framework.rpc.common.URL;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface Registry extends RegistryService, DiscoveryService {

    URL getUrl();
}
