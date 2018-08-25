package eu.hywse.lib.config._class;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface ConfigVariable {

    String path() default "{name}";
    String comment() default "";
    boolean serialize() default false;

}
