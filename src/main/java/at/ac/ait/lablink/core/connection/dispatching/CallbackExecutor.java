//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.dispatching;

import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.encodables.Header;
import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;
import at.ac.ait.lablink.core.connection.encoding.encodables.Packet;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;
import at.ac.ait.lablink.core.payloads.ErrorMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for Callback Executor
 *
 * <p>This base class implements the common methods for handling incoming packets. It will
 * execute available callbacks and handles the errors.
 */
public abstract class CallbackExecutor {

  /**
   * Default logger of the class.
   */
  protected static final Logger logger = LoggerFactory.getLogger(CallbackExecutor.class);

  /* Callback method that is used to transmit available error messages to the library user. */
  private final ICallbackBase callbackBase;

  /* Temporary storage for occurring errors. */
  protected List<ErrorMessage> errors;

  /* Temporary storage for read payloads objects. */
  private IEncodable decodedPacket;

  protected List<IPayload> payloads = new ArrayList<IPayload>();
  protected Header header;


  /**
   * Constructor.
   *
   * @param decodedPacket Decoded object of incoming packet.
   * @param errors        Occurred errors during dispatching and decoding.
   * @param callback      User's callback method that is used for error handling.
   */
  public CallbackExecutor(IEncodable decodedPacket, List<ErrorMessage> errors,
                          ICallbackBase callback) {

    if (errors != null) {
      this.errors = errors;
    } else {
      this.errors = new ArrayList<ErrorMessage>();
    }
    this.decodedPacket = decodedPacket;

    if (callback == null) {
      throw new NullPointerException("No ICallbackBase is set.");
    }
    this.callbackBase = callback;
  }

  /**
   * Method for handling the callback.
   *
   * <p>It implements the common method for parsing and error handling and callback execution.
   */
  public void handleCallback() {

    try {

      if (decodedPacket == null) {
        throw new LlCoreRuntimeException("Decoded Packet is null.");
      }

      Packet packet = (Packet) decodedPacket;
      this.payloads = packet.getPayloads();
      this.header = packet.getHeader();

      List<ErrorMessage> incomingErrors = extractIncomingErrorPayloads();
      executeErrors(header, incomingErrors);

      if (payloads != null && payloads.size() > 0) {
        executeHandleCallback(Collections.unmodifiableList(payloads));
      }

    } catch (Exception ex) {
      logger.warn("Exception during callback handling: ", ex);

      ErrorMessage
          errorMessage =
          new ErrorMessage(ErrorMessage.EErrorCode.PROCESSING_ERROR,
              "Error during callback handling: " + ex.getMessage());
      errors.add(errorMessage);
    }

    executeErrors(header, this.errors);
  }

  /**
   * Execute errors if they are available. Every error will call the error callback routine
   * separately.
   * @param header header
   * @param errors error messages
   */
  private void executeErrors(Header header, List<ErrorMessage> errors) {

    if (errors == null || errors.isEmpty()) {
      return;
    }

    try {
      this.callbackBase.handleError(header, Collections.unmodifiableList(errors));
    } catch (Exception ex) {
      logger.info("Exception during error callback handling", ex);
    }
  }

  /**
   * Extract error messages from the incoming packet. These error messages will be extracted in an
   * own data structure. Afterwards they will be removed from the payloads fields.
   *
   * @return The extracted error payloads.
   */
  private List<ErrorMessage> extractIncomingErrorPayloads() {

    List<ErrorMessage> extractedErrorMsg = new ArrayList<ErrorMessage>();

    for (IPayload pl : payloads) {
      if (pl instanceof ErrorMessage) {
        extractedErrorMsg.add((ErrorMessage) pl);
      }
    }

    if (extractedErrorMsg.size() > 0) {
      payloads.removeAll(extractedErrorMsg);
    }

    return extractedErrorMsg;
  }

  /**
   * Execute the specific callback method for the special transmission system.
   *
   * @param payloads That should be transmitted to the library user.
   * @throws java.lang.Exception general exception
   */
  protected abstract void executeHandleCallback(List<IPayload> payloads) throws Exception;

  List<ErrorMessage> getErrors() {
    return errors;
  }

  public List<IPayload> getPayloads() {
    return payloads;
  }
}
