package mango.codec;

import mango.common.URL;
import mango.core.extension.SPI;
import mango.util.Constants;
import io.netty.channel.Channel;

import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@SPI(Constants.FRAMEWORK_NAME)
public interface Codec {

    byte[] encode(URL url, Object message) throws IOException;

    Object decode(URL url, byte messageType, byte[] data) throws IOException;
}
