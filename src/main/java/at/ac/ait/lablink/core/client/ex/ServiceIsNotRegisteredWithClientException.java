//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ex;

/**
 * This exception is raised whenever a Lablink client provides a
 * service instance that is not registered.
 */
public class ServiceIsNotRegisteredWithClientException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2606859216544705469L;

  /**
   * Default constructor.
   */
  public ServiceIsNotRegisteredWithClientException() {
    super("The provided instance of service is not registered.");
  }

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param  message the detail message (which is saved for later retrieval
   *         by the {@link java.lang.Exception#getMessage()} method).
   */
  public ServiceIsNotRegisteredWithClientException(String message) {
    super(message);
  }

  /**
   * Constructs a new exception with the specified cause.
   *
   * @param  cause the cause (which is saved for later retrieval by the
   *         {@link java.lang.Exception#getCause()} method).
   */
  public ServiceIsNotRegisteredWithClientException(Throwable cause) {
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
  public ServiceIsNotRegisteredWithClientException(String message, Throwable cause) {
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
  public ServiceIsNotRegisteredWithClientException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
