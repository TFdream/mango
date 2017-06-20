package mango.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class DefaultResponse implements Serializable, Response {

    private static final long serialVersionUID = -7432143972263049268L;

    private Long requestId;
    private Exception exception;
    private Object result;
    private Map<String, String> attachments;

    private long processTime;

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    @Override
    public Long getRequestId() {
        return requestId;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public void setAttachment(String key, String value) {
        if (this.attachments == null) {
            this.attachments = new HashMap<String, String>();
        }

        this.attachments.put(key, value);
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }

    @Override
    public Map<String, String> getAttachments() {
        return attachments != null ? attachments : Collections.EMPTY_MAP;
    }

    @Override
    public String getAttachment(String key) {
        return attachments.get(key);
    }

    @Override
    public String getAttachment(String key, String defaultValue) {
        return attachments.containsKey(key) ? attachments.get(key) : defaultValue;
    }

    public void setProcessTime(long processTime) {
        this.processTime = processTime;
    }

    public long getProcessTime() {
        return processTime;
    }
}
