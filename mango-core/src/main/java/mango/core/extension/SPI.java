package mango.core.extension;

import java.lang.annotation.*;

/**
 *
 * @author Ricky Fung
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {

    /**
     * 缺省扩展点名。
     */
    String value() default "";

    Scope scope() default Scope.SINGLETON;
}
