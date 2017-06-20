package mango.registry;

import mango.common.URL;

import java.util.List;

/**
 * @author Ricky Fung
 */
public interface NotifyListener {

    void notify(URL registryUrl, List<URL> urls);
}
