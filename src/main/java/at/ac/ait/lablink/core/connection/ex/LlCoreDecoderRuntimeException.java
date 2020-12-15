//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.ex;

import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

/**
 * A IDecoder related exception.
 */
public class LlCoreDecoderRuntimeException extends LlCoreRuntimeException {

  /**
   * Constructs a new {@code LlCoreDecoderRuntimeException} without
   * specified detail message.
   */
  public LlCoreDecoderRuntimeException() {
    super();
  }

  /**
   * Constructs a new {@code LlCoreDecoderRuntimeException} with
   * specified detail message.
   *
   * @param message the error message
   */
  public LlCoreDecoderRuntimeException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@code LlCoreDecoderRuntimeException} with
   * specified nested {@code Throwable}.
   *
   * @param cause the exception or error that caused this exception to be thrown
   */
  public LlCoreDecoderRuntimeException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new {@code LlCoreDecoderRuntimeException} with
   * specified detail message and nested {@code Throwable}.
   *
   * @param message the error message
   * @param cause   the exception or error that caused this exception to be thrown
   */
  public LlCoreDecoderRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}
