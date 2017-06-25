package mango.rpc;

import com.google.common.collect.ArrayListMultimap;
import mango.cluster.Cluster;
import mango.common.URL;
import mango.core.extension.SPI;
import mango.util.Constants;

import java.util.Collection;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@SPI(Constants.DEFAULT_VALUE)
public interface ConfigHandler {

    <T> Cluster<T> buildCluster(Class<T> interfaceClass, URL refUrl, List<URL> registryUrls);

    /**
     * 引用服务
     * @param interfaceClass
     * @param cluster
     * @param <T>
     * @return
     */
    <T> T refer(Class<T> interfaceClass, List<Cluster<T>> cluster, String proxyType);

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

    <T> void unexport(List<Exporter<T>> exporters, ArrayListMultimap<URL, URL> registryUrls);

}
