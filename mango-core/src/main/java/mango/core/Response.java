package mango.core;

import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public interface Response {

    Long getRequestId();

    Exception getException();

    Object getResult();

    Map<String, String> getAttachments();

    String getAttachment(String key);

    String getAttachment(String key, String defaultValue);

    void setAttachment(String key, String value);
}
