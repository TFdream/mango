package mango.demo.server;

import mango.codec.Serializer;
import mango.core.extension.ExtensionLoader;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class ExtensionLoaderTest {

    @Test
    public void testApp() {

        ExtensionLoader loader = ExtensionLoader.getExtensionLoader(Serializer.class);
        loader.hasExtension("hessian");
    }
}
