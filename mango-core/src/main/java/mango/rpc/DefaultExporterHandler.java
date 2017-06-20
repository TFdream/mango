package mango.rpc;

import mango.common.URL;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class DefaultExporterHandler implements ExporterHandler {

    @Override
    public <T> Exporter<T> export(Class<T> interfaceClass, T ref, URL serviceUrl, List<URL> registryUrls) {
        return null;
    }
}
