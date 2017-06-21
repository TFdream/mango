package mango.protocol;

import mango.common.URL;
import mango.core.extension.SPI;
import mango.rpc.Exporter;
import mango.rpc.Provider;
import mango.util.Constants;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@SPI(Constants.FRAMEWORK_NAME)
public interface Protocol {

    <T> Exporter<T> export(Provider<T> provider, URL url);
}
