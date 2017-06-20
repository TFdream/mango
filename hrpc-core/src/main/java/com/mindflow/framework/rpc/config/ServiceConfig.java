package com.mindflow.framework.rpc.config;

import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.common.URLParamName;
import com.mindflow.framework.rpc.core.extension.ExtensionLoader;
import com.mindflow.framework.rpc.exporter.Exporter;
import com.mindflow.framework.rpc.exporter.ExporterHandler;
import com.mindflow.framework.rpc.util.Constants;
import com.mindflow.framework.rpc.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ServiceConfig<T> extends AbstractInterfaceConfig {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private volatile boolean exported = false;
    private List<Exporter<T>> exporters = new CopyOnWriteArrayList<Exporter<T>>();
    private Class<T> interfaceClass;
    private T ref;

    protected synchronized void export() {
        if (exported) {
            logger.warn(String.format("%s has already been exported, so ignore the export request!", interfaceClass.getName()));
            return;
        }
        exported = true;

        if (ref == null) {
            throw new IllegalStateException("ref not allow null!");
        }
        Class<?> interfaceClass;
        try {
            interfaceClass = Class.forName(interfaceName, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        if(!interfaceClass.isAssignableFrom(ref.getClass())) {
            throw new IllegalArgumentException(ref.getClass() +" is not "+interfaceClass+" sub class!");
        }

        if (getRegistries() == null || getRegistries().isEmpty()) {
            throw new IllegalStateException("Should set registry config for service:" + interfaceClass.getName());
        }

        List<URL> registryUrls = loadRegistryUrls();
        if (registryUrls == null || registryUrls.size() == 0) {
            throw new IllegalStateException("Should set registry config for service:" + interfaceClass.getName());
        }

        for(ProtocolConfig protocol : protocols) {

            doExport(protocol, registryUrls);
        }
    }

    private void doExport(ProtocolConfig protocol, List<URL> registryUrls) {
        String protocolName = protocol.getName();
        if (protocolName == null || protocolName.length() == 0) {
            protocolName = URLParamName.protocol.getValue();
        }

        URL url = new URL(protocolName, protocol.getHost(), protocol.getPort(), interfaceName);
        url.addParameter(URLParamName.version.getName(), StringUtils.isNotEmpty(version) ? version : URLParamName.version.getValue());
        url.addParameter(URLParamName.group.getName(), StringUtils.isNotEmpty(group) ? group : URLParamName.group.getValue());
        url.addParameter(URLParamName.serialization.getName(), StringUtils.isNotEmpty(protocol.getSerialization()) ? protocol.getSerialization(): URLParamName.serialization.getValue());
        url.addParameter(URLParamName.requestTimeout.getName(), timeout!=null ? timeout.toString() : URLParamName.requestTimeout.getValue());
        url.addParameter(URLParamName.side.getName(), "provider");
        url.addParameter(URLParamName.timestamp.getName(), String.valueOf(System.currentTimeMillis()));

        ExporterHandler exporterHandler = ExtensionLoader.getExtensionLoader(ExporterHandler.class).getExtension(Constants.DEFAULT_VALUE);
        exporters.add(exporterHandler.export(interfaceClass, ref, url, registryUrls));
    }

    public T getRef() {
        return ref;
    }

    public void setRef(T ref) {
        this.ref = ref;
    }

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public boolean isExported() {
        return exported;
    }

    protected void destroy0() throws Exception {

    }
}
