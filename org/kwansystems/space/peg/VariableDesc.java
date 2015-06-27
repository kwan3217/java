package org.kwansystems.space.peg;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface VariableDesc {
  String units() default "";
  String desc();
  boolean Major() default true;
  boolean Minor() default false;
}
