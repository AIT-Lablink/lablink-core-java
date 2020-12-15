//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.dispatching;

import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.payloads.ErrorMessage;

import java.util.List;

/**
 * Factory interface for creating callback executors.
 *
 * <p>The interface will be used to create a new callback handler for an incoming message.
 * Every message uses its own callback handler that handles this incoming message. The provided
 * user's callback method will be used by this handler.
 */
public interface ICallbackExecutorFactory {

  /**
   * Create a new callback handler that is used to handle an incoming message.
   * @param decoded Already decoded packet that was received and sucessfully dispatched
   * @param errors Generated errors by the dispatcher. This will be in most cases decoding errors,
   * @return The new created CallbackExecutor that can be handled and executed by another thread.
   */
  CallbackExecutor createCallbackExecutor(IEncodeable decoded, List<ErrorMessage> errors);
}
