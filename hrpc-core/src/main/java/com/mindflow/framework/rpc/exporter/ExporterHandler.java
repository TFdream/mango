package com.mindflow.framework.rpc.exporter;

import com.mindflow.framework.rpc.common.URL;
import com.mindflow.framework.rpc.core.extension.SPI;
import com.mindflow.framework.rpc.util.Constants;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@SPI(Constants.DEFAULT_VALUE)
public interface ExporterHandler {

    <T> Exporter<T> export(Class<T> interfaceClass, T ref, URL serviceUrl, List<URL> registryUrls);

}
