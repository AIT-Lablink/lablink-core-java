//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.dispatching.impl;

import at.ac.ait.lablink.core.connection.dispatching.CallbackExecutor;
import at.ac.ait.lablink.core.connection.dispatching.CallbackExecutorManager;
import at.ac.ait.lablink.core.connection.dispatching.ICallbackExecutorFactory;
import at.ac.ait.lablink.core.connection.dispatching.IDispatcherCallback;
import at.ac.ait.lablink.core.connection.encoding.DecoderBase;
import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.connection.ex.LlCoreDecoderRuntimeException;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;
import at.ac.ait.lablink.core.payloads.ErrorMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a Dispatcher callback.
 *
 * <p>It will be used for dispatching callbacks. An incoming message will be decoded and the
 * execution will be added to a CallbackExecutionManager that will be used to execute the callback.
 */
public class DispatcherCallbackImpl implements IDispatcherCallback {

  /**
   * Default logger of the class.
   */
  protected static final Logger logger = LoggerFactory.getLogger(DispatcherCallbackImpl.class);

  /* decoder that will be used to decode the packet */
  private final DecoderBase decoder;

  private final ICallbackExecutorFactory callbackExecutorFactory;

  /* Temporary storage for occurring errors. */
  private List<ErrorMessage> errors = new ArrayList<ErrorMessage>();


  private CallbackExecutorManager callbackExecutorManager;


  /**
   * Default constructor.
   *
   * @param decoder                 IDecoder object that is used to convert an incoming message
   *                                into the
   *                                packet structure.
   * @param callbackExecutorFactory Callback method for handling error messages.
   */
  public DispatcherCallbackImpl(DecoderBase decoder,
                                ICallbackExecutorFactory callbackExecutorFactory) {

    if (decoder == null) {
      throw new NullPointerException("No IDecoder is set.");
    }
    this.decoder = decoder;

    if (callbackExecutorFactory == null) {
      throw new NullPointerException("No ICallbackExecutorFactory is set.");
    }
    this.callbackExecutorFactory = callbackExecutorFactory;
  }

  /**
   * Set the Executor manager for callback methods.
   *
   * @param callbackExecutorManager Manager that is used to execute callback methods for incoming
   *                                messages.
   */
  public void setCallbackExecutorManager(CallbackExecutorManager callbackExecutorManager) {
    this.callbackExecutorManager = callbackExecutorManager;
  }


  @Override
  public void handleMessage(byte[] payload) {

    clearElements();

    IEncodeable decoded = decodeIncomingPacket(payload);

    try {
      CallbackExecutor
          callbackExecutor =
          callbackExecutorFactory.createCallbackExecutor(decoded, errors);
      callbackExecutorManager.addNewCallbackExecution(callbackExecutor);
    } catch (Exception ex) {
      logger.warn("Exception occurs during incoming message dispatching and handling.", ex);
    }
  }

  /**
   * Decode an incoming payloads using the available decoder.
   *
   * @param payload byte array with payloads stream to be decoded.
   * @return The encoded and validated packet
   */
  private IEncodeable decodeIncomingPacket(byte[] payload) {

    IEncodeable decoded = null;
    try {
      decoded = decoder.processDecoding(payload);
      decoded.validate();
    } catch (LlCoreDecoderRuntimeException ex) {
      ErrorMessage
          error =
          new ErrorMessage(ErrorMessage.EErrorCode.DECODING_ERROR,
              "Errors during decoding: " + ex.getMessage());
      errors.add(error);
    } catch (LlCoreRuntimeException ex) {
      ErrorMessage
          error =
          new ErrorMessage(ErrorMessage.EErrorCode.VALIDATION_ERROR,
              "Errors during validation:" + ex.getMessage());
      errors.add(error);
    } catch (NullPointerException ex) {
      ErrorMessage
          error =
          new ErrorMessage(ErrorMessage.EErrorCode.VALIDATION_ERROR,
              "Errors before validation:" + ex.getMessage());
      errors.add(error);
    }

    return decoded;
  }

  /**
   * Clear all temporary elements of the callback adapter.
   */
  protected void clearElements() {
    errors = new ArrayList<ErrorMessage>();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    DispatcherCallbackImpl that = (DispatcherCallbackImpl) obj;

    return callbackExecutorFactory != null ? callbackExecutorFactory
        .equals(that.callbackExecutorFactory) : that.callbackExecutorFactory == null;

  }

  @Override
  public int hashCode() {
    return callbackExecutorFactory != null ? callbackExecutorFactory.hashCode() : 0;
  }

  List<ErrorMessage> getErrors() {
    return errors;
  }
}
