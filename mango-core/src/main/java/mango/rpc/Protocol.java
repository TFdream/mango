package mango.rpc;

import mango.common.URL;
import mango.core.extension.SPI;
import mango.util.Constants;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@SPI(value = Constants.FRAMEWORK_NAME)
public interface Protocol {

    <T> Reference<T> refer(Class<T> clz, URL url, URL serviceUrl);

    <T> Exporter<T> export(Provider<T> provider, URL url);

    void destroy();
}
