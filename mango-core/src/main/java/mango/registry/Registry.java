package mango.registry;

import mango.common.URL;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface Registry extends RegistryService, DiscoveryService {

    URL getUrl();

    void close();
}
