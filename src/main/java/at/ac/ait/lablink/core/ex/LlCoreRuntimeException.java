//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.ex;

/**
 * A LablinkCore related runtime exception.
 */
public class LlCoreRuntimeException extends RuntimeException {

  /**
   * Constructs a new {@code LlCoreRuntimeException} without
   * specified detail message.
   */
  public LlCoreRuntimeException() {
    super();
  }

  /**
   * Constructs a new {@code LlCoreRuntimeException} with
   * specified detail message.
   *
   * @param message the error message
   */
  public LlCoreRuntimeException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@code LlCoreRuntimeException} with
   * specified nested {@code Throwable}.
   *
   * @param cause the exception or error that caused this exception to be thrown
   */
  public LlCoreRuntimeException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new {@code LlCoreRuntimeException} with
   * specified detail message and nested {@code Throwable}.
   *
   * @param message the error message
   * @param cause   the exception or error that caused this exception to be thrown
   */
  public LlCoreRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}
