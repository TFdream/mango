package com.mindflow.framework.registry;

import com.mindflow.framework.rpc.URL;
import java.util.List;

/**
 * @author Ricky Fung
 */
public interface NotifyListener {

    void notify(URL registryUrl, List<URL> urls);
}
