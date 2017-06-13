package com.mindflow.framework.rpc.core;

import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface Request {

    Long getRequestId();

    String getClassName();

    String getMethodName();

    Object[] getArguments();

    Class<?>[] getParameterTypes();

    Map<String, String> getAttachments();

    String getAttachment(String key);

    String getAttachment(String key, String defaultValue);

    void setAttachment(String key, String value);
}
