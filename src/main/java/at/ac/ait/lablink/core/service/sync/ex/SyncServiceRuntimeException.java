//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync.ex;

import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

/**
 * A Sync service related runtime exception.
 */
public class SyncServiceRuntimeException extends LlCoreRuntimeException {

  /**
   * Constructs a new {@code SyncServiceRuntimeException} without
   * specified detail message.
   */
  public SyncServiceRuntimeException() {
    super();
  }

  /**
   * Constructs a new {@code SyncServiceRuntimeException} with
   * specified detail message.
   *
   * @param message the error message
   */
  public SyncServiceRuntimeException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@code SyncServiceRuntimeException} with
   * specified nested {@code Throwable}.
   *
   * @param cause the exception or error that caused this exception to be thrown
   */
  public SyncServiceRuntimeException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new {@code SyncServiceRuntimeException} with
   * specified detail message and nested {@code Throwable}.
   *
   * @param message the error message
   * @param cause   the exception or error that caused this exception to be thrown
   */
  public SyncServiceRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}
