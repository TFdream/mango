package mango.rpc;

import mango.common.URL;
import mango.core.Request;
import mango.core.Response;
import java.lang.reflect.Method;

/**
 * @author Ricky Fung
 */
public abstract class AbstractProvider<T> implements Provider<T> {
    protected Class<T> clz;
    protected URL url;

    public AbstractProvider(URL url, Class<T> clz) {
        this.url = url;
        this.clz = clz;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public Class<T> getInterface() {
        return clz;
    }

    @Override
    public Response call(Request request) {
        Response response = invoke(request);

        return response;
    }

    protected abstract Response invoke(Request request);

    protected Method lookup(Request request) {
        try {
            return clz.getMethod(request.getMethodName(), request.getParameterTypes());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

}
