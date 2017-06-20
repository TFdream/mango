package mango.registry;

import mango.common.URL;

import java.util.List;

/**
 * @author Ricky Fung
 */
public interface DiscoveryService {

    void subscribe(URL url, NotifyListener listener);

    void unsubscribe(URL url, NotifyListener listener);

    /**
     * 查询符合条件的已注册数据，与订阅的推模式相对应，这里为拉模式，只返回一次结果。
     * @param url
     * @return
     */
    List<URL> discover(URL url) throws Exception;
}
