
package com.rcs.newsletter.core.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation class to be used on fields you'd like excluded on Gson serialization
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface GsonExclude {

}