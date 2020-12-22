//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.dispatching.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import at.ac.ait.lablink.core.connection.dispatching.CallbackExecutor;
import at.ac.ait.lablink.core.connection.dispatching.CallbackExecutorManager;
import at.ac.ait.lablink.core.connection.dispatching.ICallbackExecutorFactory;
import at.ac.ait.lablink.core.connection.encoding.DecoderBase;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.encodables.Packet;
import at.ac.ait.lablink.core.connection.ex.LlCoreDecoderRuntimeException;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit Tests for DispatcherCallbackImpl.
 */
public class DispatcherCallbackImplTest {

  private DispatcherCallbackImpl cut;
  DecoderBase decoder = mock(DecoderBase.class);
  ICallbackExecutorFactory callbackExecutorFactory = mock(ICallbackExecutorFactory.class);
  CallbackExecutorManager manager = mock(CallbackExecutorManager.class);
  private Packet packetMock;

  CallbackExecutor cbExecutor  = mock(CallbackExecutor.class);

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() throws Exception {

    cut = new DispatcherCallbackImpl(decoder, callbackExecutorFactory);
    cut.setCallbackExecutorManager(manager);

    packetMock = mock(Packet.class);
    when(decoder.processDecoding(any(byte[].class))).thenReturn(packetMock);
    when(callbackExecutorFactory.createCallbackExecutor(any(IEncodable.class), anyList()))
        .thenReturn(cbExecutor);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void decodeIncomingPacket_decoderThrowsDecodingException_addErrorMessage_test()
      throws Exception {

    when(decoder.processDecoding(any(byte[].class)))
        .thenThrow(LlCoreDecoderRuntimeException.class);

    cut.handleMessage(new byte[10]);

    assertEquals("There should be an Error Message added.", 1, cut.getErrors().size());
  }

  @Test
  public void decodeIncomingPacket_throwsValidationError_addErrorMessage_test() throws Exception {
    doThrow(new LlCoreRuntimeException()).when(packetMock).validate();

    cut.handleMessage(new byte[10]);
    assertEquals("There should be an Error Message added.", 1, cut.getErrors().size());
  }

  @Test
  public void handleMessage_addIncomingMessageToExecutor_test() throws Exception {

    cut.handleMessage(new byte[10]);

    verify(manager,times(1)).addNewCallbackExecution(cbExecutor);
  }
}