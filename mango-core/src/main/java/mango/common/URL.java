package mango.common;

import mango.exception.RpcFrameworkException;
import mango.util.Constants;
import mango.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * /{mango-framework}/interface/consumers/
 * /{mango-framework}/interface/providers/
 * 服务提供者: [protocol]://10.141.5.49:21903/mango.example.UserService?application=mango-provider&mango=2.8.3&interface=mango.example.UserService&pid=9011&retries=0&side=provider&timestamp=1487902335567&version=1.0.0
 * 服务消费者: consumer://10.141.5.49/mango.example.UserService?application=mango-consumer&category=consumers&check=false&mango=2.8.3&interface=mango.example.UserService&pid=29465&side=consumer&timeout=120000&timestamp=1480648755499&version=1.0.0
 * @author Ricky Fung
 */
public class URL {

    private String protocol;

    private String host;

    private int port;

    // interfaceName
    private String path;

    private Map<String, String> parameters;

    public URL(String protocol, String host, int port, String path) {
        this(protocol, host, port, path, new HashMap<String, String>());
    }

    public URL(String protocol, String host, int port, String path, Map<String, String> parameters) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.path = path;
        this.parameters = parameters;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getVersion() {
        return getParameter(URLParam.version.getName(), URLParam.version.getValue());
    }

    public String getGroup() {
        return getParameter(URLParam.group.getName(), URLParam.group.getValue());
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public String getParameter(String name, String defaultValue) {
        String value = getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public Integer getIntParameter(String name, int defaultValue) {
        String value = parameters.get(name);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    public void addParameter(String name, String value) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(value)) {
            return;
        }
        parameters.put(name, value);
    }

    public void removeParameter(String name) {
        if (name != null) {
            parameters.remove(name);
        }
    }

    public static URL parse(String url) {
        if (StringUtils.isBlank(url)) {
            throw new RpcFrameworkException("url is empty");
        }
        String protocol = null;
        String host = null;
        int port = 0;
        String path = null;
        Map<String, String> parameters = new HashMap<String, String>();;
        int i = url.indexOf("?"); // seperator between body and parameters
        if (i >= 0) {
            String[] parts = url.substring(i + 1).split("\\&");

            for (String part : parts) {
                part = part.trim();
                if (part.length() > 0) {
                    int j = part.indexOf('=');
                    if (j >= 0) {
                        parameters.put(part.substring(0, j), part.substring(j + 1));
                    } else {
                        parameters.put(part, part);
                    }
                }
            }
            url = url.substring(0, i);
        }
        i = url.indexOf("://");
        if (i >= 0) {
            if (i == 0) throw new IllegalStateException("url missing protocol: \"" + url + "\"");
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        } else {
            i = url.indexOf(":/");
            if (i >= 0) {
                if (i == 0) throw new IllegalStateException("url missing protocol: \"" + url + "\"");
                protocol = url.substring(0, i);
                url = url.substring(i + 1);
            }
        }

        i = url.indexOf("/");
        if (i >= 0) {
            path = url.substring(i + 1);
            url = url.substring(0, i);
        }

        i = url.indexOf(":");
        if (i >= 0 && i < url.length() - 1) {
            port = Integer.parseInt(url.substring(i + 1));
            url = url.substring(0, i);
        }
        if (url.length() > 0) host = url;
        return new URL(protocol, host, port, path, parameters);
    }

    public String getUri() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(protocol).append(Constants.PROTOCOL_SEPARATOR).append(host)
                .append(":").append(port).append(Constants.PATH_SEPARATOR)
                .append(path);
        return sb.toString();
    }

    public String toFullUri() {
        StringBuilder builder = new StringBuilder(1024);
        builder.append(getUri()).append("?");

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();

            builder.append(name).append("=").append(value).append("&");
        }

        return builder.toString();
    }

    public URL clone0() {
        Map<String, String> params = new HashMap<String, String>();
        if (this.parameters != null) {
            params.putAll(this.parameters);
        }
        return new URL(protocol, host, port, path, params);
    }

    public String getServerAndPort() {
        return buildHostPortStr(host, port);
    }

    private static String buildHostPortStr(String host, int defaultPort) {
        if (defaultPort <= 0) {
            return host;
        }

        int idx = host.indexOf(":");
        if (idx < 0) {
            return host + ":" + defaultPort;
        }

        int port = Integer.parseInt(host.substring(idx + 1));
        if (port <= 0) {
            return host.substring(0, idx + 1) + defaultPort;
        }
        return host;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(120);
        sb.append(getUri()).append("?group=").append(getGroup());
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        URL url = (URL) o;

        if (port != url.port) return false;
        if (!protocol.equals(url.protocol)) return false;
        if (!host.equals(url.host)) return false;
        if (!path.equals(url.path)) return false;
        return !(parameters != null ? !parameters.equals(url.parameters) : url.parameters != null);

    }

    @Override
    public int hashCode() {
        int result = protocol.hashCode();
        result = 31 * result + host.hashCode();
        result = 31 * result + port;
        result = 31 * result + path.hashCode();
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }
}
