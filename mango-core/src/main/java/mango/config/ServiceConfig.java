package mango.config;

import com.google.common.collect.ArrayListMultimap;
import mango.common.URL;
import mango.common.URLParam;
import mango.core.extension.ExtensionLoader;
import mango.rpc.ConfigHandler;
import mango.rpc.Exporter;
import mango.util.Constants;
import mango.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ServiceConfig<T> extends AbstractInterfaceConfig {

    private static final long serialVersionUID = -6784362174923673740L;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private volatile boolean exported = false;
    private List<Exporter<T>> exporters = new CopyOnWriteArrayList<>();
    private ArrayListMultimap<URL, URL> registeredUrls = ArrayListMultimap.create();
    private Class<T> interfaceClass;
    private T ref;

    protected synchronized void export() {
        if (exported) {
            logger.warn(String.format("%s has already been exported, so ignore the export request!", interfaceName));
            return;
        }

        if (ref == null) {
            throw new IllegalStateException("ref not allow null!");
        }
        try {
            interfaceClass = (Class<T>) Class.forName(interfaceName, true, Thread.currentThread().getContextClassLoader());
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
        exported = true;
    }

    private void doExport(ProtocolConfig protocol, List<URL> registryUrls) {
        String protocolName = protocol.getName();
        if (protocolName == null || protocolName.length() == 0) {
            protocolName = URLParam.protocol.getValue();
        }

        Integer port = getProtocolPort(protocol);
        String hostAddress = getLocalHostAddress(protocol);

        Map<String, String> map = new HashMap<String, String>();
        map.put(URLParam.application.getName(), StringUtils.isNotEmpty(application.getName()) ? application.getName() : URLParam.application.getValue());
        map.put(URLParam.version.getName(), StringUtils.isNotEmpty(version) ? version : URLParam.version.getValue());
        map.put(URLParam.group.getName(), StringUtils.isNotEmpty(group) ? group : URLParam.group.getValue());
        map.put(URLParam.serialization.getName(), StringUtils.isNotEmpty(protocol.getSerialization()) ? protocol.getSerialization(): URLParam.serialization.getValue());
        map.put(URLParam.requestTimeout.getName(), timeout!=null ? timeout.toString() : URLParam.requestTimeout.getValue());
        map.put(URLParam.side.getName(), Constants.PROVIDER);
        map.put(URLParam.timestamp.getName(), String.valueOf(System.currentTimeMillis()));

        URL serviceUrl = new URL(protocolName, hostAddress, port, interfaceClass.getName(), map);

        for(URL ru : registryUrls) {
            registeredUrls.put(serviceUrl, ru);
        }

        ConfigHandler configHandler = ExtensionLoader.getExtensionLoader(ConfigHandler.class).getExtension(Constants.DEFAULT_VALUE);
        exporters.add(configHandler.export(interfaceClass, ref, serviceUrl, registryUrls));
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

        if(!exported) {
            return;
        }

        ConfigHandler configHandler = ExtensionLoader.getExtensionLoader(ConfigHandler.class).getExtension(Constants.DEFAULT_VALUE);
        configHandler.unexport(exporters, registeredUrls);

        exporters.clear();
        registeredUrls.clear();
    }

}
