//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.spi;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Access to Lablink host implementation.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ALlHostImplementation {

  /**
   * Access name. The name that will be use to access this implementation.
   *
   * @return the string
   */
  String accessName();

  /**
   * Description. A short description of the implementation.
   *
   * @return the string
   */
  String description();

  /**
   * Checks if is a pseudo host. The default is false.
   *
   * @return true, if is pseudo host
   */
  boolean isPseudoHost() default false;
}
