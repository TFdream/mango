package com.mindflow.framework.rpc.serialize;

import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface Serializer {

    byte[] encode(Object msg) throws IOException;

    <T> T decode(byte[] buf, Class<T> type) throws IOException;
}
