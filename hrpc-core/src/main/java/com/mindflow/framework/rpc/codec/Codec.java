package com.mindflow.framework.rpc.codec;

import com.mindflow.framework.rpc.core.extension.SPI;
import com.mindflow.framework.rpc.util.Constants;
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
