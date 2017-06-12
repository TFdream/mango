package com.mindflow.framework.rpc;

import java.util.HashMap;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class DefaultResponse implements Response {

    private Long requestId;
    private Throwable exc;
    private Object result;
    private Map<String, String> attachments = new HashMap<>();

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    @Override
    public Long getRequestId() {
        return requestId;
    }

    @Override
    public Throwable getException() {
        return exc;
    }

    public void setException(Throwable exc) {
        this.exc = exc;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public Object getResult() {
        return null;
    }

    @Override
    public Map<String, String> getAttachments() {
        return null;
    }

    @Override
    public String getAttachment(String key) {
        return null;
    }

    @Override
    public String getAttachment(String key, String defaultValue) {
        return null;
    }
}
