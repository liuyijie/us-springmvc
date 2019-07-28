package us.framework.web.servlet.annotation;

import java.lang.annotation.*;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface USAutowired {

    String value() default "";
}

