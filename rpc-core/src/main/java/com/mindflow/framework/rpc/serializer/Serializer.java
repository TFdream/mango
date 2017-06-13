package com.mindflow.framework.rpc.serializer;

import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface Serializer {

    byte[] encode(Object msg) throws IOException;

    <T> T decode(byte[] data, Class<T> type) throws IOException;
}
