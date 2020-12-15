//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.dispatching;

import at.ac.ait.lablink.core.connection.encoding.encodeables.Header;
import at.ac.ait.lablink.core.payloads.ErrorMessage;

import java.util.List;

/**
 * Interface to handle received or locally generated error messages.
 */
public interface ICallbackBase {

  /**
   * Callback function for handling errors.
   *
   * <p>If an error occurs during the communication this method will be called. This can be a
   * decoding or validation error of an incoming message but it can also be a received error message
   * from another client.
   *
   * <p>The method can be called from different threads at the same time. Therefore the
   * implementation of this method should be able to handle concurrent access.
   *
   * @param header Header of the message packet that contain thee error message
   * @param errors Error message of the exception
   * @throws Exception if any exception occurs during handling the error message.
   */
  void handleError(Header header, List<ErrorMessage> errors) throws Exception;
}
