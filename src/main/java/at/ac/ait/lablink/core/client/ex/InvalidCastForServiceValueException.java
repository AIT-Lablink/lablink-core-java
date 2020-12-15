//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ex;

/**
 * This exception is raised whenever a type cast performed by a 
 * Lablink client' service is invalid (i.e., a value can not be converted 
 * to the requested type).
 */
public class InvalidCastForServiceValueException extends Exception {

  /**
   * Default constructor.
   */
  public InvalidCastForServiceValueException() {
    super("The current value can not be converted to requested type.");
  }

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param  message the detail message (which is saved for later retrieval
   *         by the {@link java.lang.Exception#getMessage()} method).
   */
  public InvalidCastForServiceValueException(String message) {
    super(message);
  }

  /**
   * Constructs a new exception with the specified cause.
   *
   * @param  cause the cause (which is saved for later retrieval by the
   *         {@link java.lang.Exception#getCause()} method).
   */
  public InvalidCastForServiceValueException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new exception with the specified detail message and
   * cause.
   *
   * @param  message the detail message (which is saved for later retrieval
   *         by the {@link java.lang.Exception#getMessage()} method).
   * @param  cause the cause (which is saved for later retrieval by the
   *         {@link java.lang.Exception#getCause()} method).
   */
  public InvalidCastForServiceValueException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new exception with the specified detail message and
   * cause.
   *
   * @param  message the detail message (which is saved for later retrieval
   *         by the {@link java.lang.Exception#getMessage()} method).
   * @param  cause the cause (which is saved for later retrieval by the
   *         {@link java.lang.Exception#getCause()} method).
   * @param  enableSuppression this flag indicates if suppression of this exception is enabled
   * @param  writableStackTrace this flag indicates if the stack is writable
   */
  public InvalidCastForServiceValueException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
