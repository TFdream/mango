package mango.codec;

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

    byte[] encode(Channel channel, Object message) throws IOException;

    Object decode(Channel channel, byte messageType, byte[] data) throws IOException;
}
