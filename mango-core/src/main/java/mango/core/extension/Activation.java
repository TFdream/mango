package mango.core.extension;

import java.lang.annotation.*;

/**
 * @author Ricky Fung
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Activation {

    /** priority值越小，在返回的list中的位置越靠前，尽量使用 0-100以内的数字 */
    int order() default 20;

    String[] key() default {};
}
