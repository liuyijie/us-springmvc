package us.framework.web.servlet.annotation;

import java.lang.annotation.*;


@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface USReqParam {
    String value() default "";
}

