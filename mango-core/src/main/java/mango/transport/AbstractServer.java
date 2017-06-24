package mango.transport;

import mango.codec.Codec;
import mango.common.URL;
import mango.common.URLParam;
import mango.core.extension.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author Ricky Fung
 */
public abstract class AbstractServer implements NettyServer {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected InetSocketAddress localAddress;
    protected InetSocketAddress remoteAddress;

    protected URL url;
    protected Codec codec;

    protected volatile ChannelState state = ChannelState.NEW;

    public AbstractServer() {}

    public AbstractServer(URL url) {
        this.url = url;
        this.codec =
                ExtensionLoader.getExtensionLoader(Codec.class).getExtension(url.getParameter(URLParam.codec.getName(), URLParam.codec.getValue()));
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

}
