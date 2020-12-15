//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.ex;

import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

/**
 * A Datapoint service related runtime exception.
 */
public class DatapointServiceRuntimeException extends LlCoreRuntimeException {

  /**
   * Constructs a new {@code DatapointServiceRuntimeException} without
   * specified detail message.
   */
  public DatapointServiceRuntimeException() {
    super();
  }

  /**
   * Constructs a new {@code DatapointServiceRuntimeException} with
   * specified detail message.
   *
   * @param message the error message
   */
  public DatapointServiceRuntimeException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@code DatapointServiceRuntimeException} with
   * specified nested {@code Throwable}.
   *
   * @param cause the exception or error that caused this exception to be thrown
   */
  public DatapointServiceRuntimeException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new {@code DatapointServiceRuntimeException} with
   * specified detail message and nested {@code Throwable}.
   *
   * @param message the error message
   * @param cause   the exception or error that caused this exception to be thrown
   */
  public DatapointServiceRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}
