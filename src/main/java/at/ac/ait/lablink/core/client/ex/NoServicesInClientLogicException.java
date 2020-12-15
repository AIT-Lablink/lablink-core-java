//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ex;

/**
 * This exception is raised whenever a Lablink client has not defined
 * any services in its logic.
 */
public class NoServicesInClientLogicException extends Exception {

  /**
   * Default constructor.
   */
  public NoServicesInClientLogicException() {
    super("There are no services defined in the client logic.");
  }

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param  message the detail message (which is saved for later retrieval
   *         by the {@link java.lang.Exception#getMessage()} method).
   */
  public NoServicesInClientLogicException(String message) {
    super(message);
  }

  /**
   * Constructs a new exception with the specified cause.
   *
   * @param  cause the cause (which is saved for later retrieval by the
   *         {@link java.lang.Exception#getCause()} method).
   */
  public NoServicesInClientLogicException(Throwable cause) {
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
  public NoServicesInClientLogicException(String message, Throwable cause) {
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
  public NoServicesInClientLogicException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
