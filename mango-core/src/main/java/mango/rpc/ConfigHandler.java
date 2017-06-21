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

    <T> Exporter<T> export(Class<T> interfaceClass, T ref, URL serviceUrl, List<URL> registryUrls);

}
