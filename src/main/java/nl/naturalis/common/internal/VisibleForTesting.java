package nl.naturalis.common.internal;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@Target({FIELD, METHOD, CONSTRUCTOR})
public @interface VisibleForTesting {}