//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.ex;

import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

/**
 * Any exception that occurs while using the Lablink LowLevelCommunication interface.
 */
public class LowLevelCommRuntimeException extends LlCoreRuntimeException {

  /**
   * Constructs a new {@code LowLevelCommRuntimeException} without specified
   * detail message.
   */
  public LowLevelCommRuntimeException() {
    super();
  }

  /**
   * Constructs a new {@code LowLevelCommRuntimeException} with specified
   * detail message.
   *
   * @param message the error message
   */
  public LowLevelCommRuntimeException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@code LowLevelCommRuntimeException} with specified
   * nested {@code Throwable}.
   *
   * @param cause the exception or error that caused this exception to be thrown
   */
  public LowLevelCommRuntimeException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new {@code LowLevelCommRuntimeException} with specified
   * detail message and nested {@code Throwable}.
   *
   * @param message the error message
   * @param cause   the exception or error that caused this exception to be thrown
   */
  public LowLevelCommRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}
