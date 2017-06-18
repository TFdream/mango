package com.mindflow.framework.rpc.registry;

import com.mindflow.framework.rpc.common.URL;
import java.util.List;

/**
 * @author Ricky Fung
 */
public interface NotifyListener {

    void notify(URL registryUrl, List<URL> urls);
}
