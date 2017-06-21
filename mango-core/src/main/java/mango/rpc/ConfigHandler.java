package mango.rpc;

import mango.common.URL;
import mango.core.extension.SPI;
import mango.util.Constants;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@SPI(Constants.DEFAULT_VALUE)
public interface ConfigHandler {

    /**
     * 引用服务
     * @param clz
     * @param url
     * @param serviceUrl
     * @param <T>
     * @return
     */
    <T> Invoker<T> refer(Class<T> clz, URL url, URL serviceUrl);

    /**
     * 暴露服务
     * @param interfaceClass
     * @param ref
     * @param serviceUrl
     * @param registryUrls
     * @param <T>
     * @return
     */
    <T> Exporter<T> export(Class<T> interfaceClass, T ref, URL serviceUrl, List<URL> registryUrls);

}
