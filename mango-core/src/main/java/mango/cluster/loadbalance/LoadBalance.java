package mango.cluster.loadbalance;

import mango.common.URL;
import mango.core.extension.SPI;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@SPI("random")
public interface LoadBalance {

    URL select(List<URL> urls);
}
