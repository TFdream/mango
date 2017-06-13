package com.mindflow.framework.rpc.core;

import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface Response {

    Long getRequestId();

    Throwable getException();

    Object getResult();

    Map<String, String> getAttachments();

    String getAttachment(String key);

    String getAttachment(String key, String defaultValue);
}
