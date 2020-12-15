//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ex;

/**
 * This exception is raised whenever an unavailable pseudo host 
 * implementation is requested.
 */
public class NoSuchPseudoHostException extends Exception {

  /** The serial version UID. */
  private static final long serialVersionUID = 3420023300495414698L;

  /**
   * Default constructor.
   */
  public NoSuchPseudoHostException() {
    super("No such pseudo host implementation exists.");
  }

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param  message the detail message (which is saved for later retrieval
   *         by the {@link java.lang.Exception#getMessage()} method).
   */
  public NoSuchPseudoHostException(String message) {
    super(message);
  }

  /**
   * Constructs a new exception with the specified cause.
   *
   * @param  cause the cause (which is saved for later retrieval by the
   *         {@link java.lang.Exception#getCause()} method).
   */
  public NoSuchPseudoHostException(Throwable cause) {
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
  public NoSuchPseudoHostException(String message, Throwable cause) {
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
  public NoSuchPseudoHostException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
