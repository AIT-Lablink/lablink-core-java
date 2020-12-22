//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.dispatching;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.connection.encoding.encodables.Header;
import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;
import at.ac.ait.lablink.core.connection.encoding.encodables.Packet;
import at.ac.ait.lablink.core.connection.encoding.encodables.PayloadBase;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;
import at.ac.ait.lablink.core.payloads.ErrorMessage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for callback executor.
 */
public class CallbackExecutorTest {

  private CallbackExecutorTestImpl classUnderTest;
  private ICallbackBase callbackMock;
  private Packet packetMock;
  private List<ErrorMessage> errorsMock;
  private List<IPayload> payloadsMock;

  @Before
  public void setUp() throws Exception {

    callbackMock = mock(ICallbackBase.class);
    errorsMock = new ArrayList<ErrorMessage>();

    payloadsMock = new ArrayList<IPayload>();
    Header headerMock = mock(Header.class);
    packetMock = mock(Packet.class);
    when(packetMock.getHeader()).thenReturn(headerMock);
    when(packetMock.getPayloads()).thenReturn(payloadsMock);

    classUnderTest = new CallbackExecutorTestImpl(packetMock, errorsMock, callbackMock);

  }

  @Test
  @SuppressWarnings("unchecked")
  public void executeErrors_listOfErrors_separateExecution_test() throws Exception {
    ErrorMessage msgMock = new ErrorMessage(ErrorMessage.EErrorCode.PROCESSING_ERROR, "Test");
    payloadsMock.add(msgMock);
    payloadsMock.add(msgMock);

    classUnderTest.handleCallback();

    ArgumentCaptor<List> errorArgument = ArgumentCaptor.forClass(List.class);
    verify(callbackMock, times(1)).handleError(any(Header.class), errorArgument.capture());
    assertEquals("Errors should only contain two element.", 2, errorArgument.getValue().size());
  }

  @Test
  public void executeErrors_NoErrors_noExecution_test() throws Exception {

    classUnderTest.handleCallback();
    verify(callbackMock, times(0)).handleError(any(Header.class), anyList());
  }

  @Test
  public void extractIncomingErrorPayloads_NoErrorMessages_extractNothing_test() throws Exception {
    IPayload payload = new PayloadTestImpl();
    payloadsMock.add(payload);
    payloadsMock.add(payload);

    classUnderTest.handleCallback();

    assertEquals(2, classUnderTest.getPayloads().size());
    assertEquals(0, classUnderTest.getErrors().size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void extractIncomingErrorPayloads_AllErrorMessages_extractAll_test() throws Exception {
    ErrorMessage msgMock = new ErrorMessage(ErrorMessage.EErrorCode.PROCESSING_ERROR, "Test");
    payloadsMock.add(msgMock);
    payloadsMock.add(msgMock);

    classUnderTest.handleCallback();

    assertEquals(0, classUnderTest.getPayloads().size());
    ArgumentCaptor<List> errorArgument = ArgumentCaptor.forClass(List.class);
    verify(callbackMock, times(1)).handleError(any(Header.class), errorArgument.capture());
    assertEquals("Errors should only contain two element.", 2, errorArgument.getValue().size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void extractIncomingErrorPayloads_ExtractErrorMsg_test() throws Exception {
    ErrorMessage msgMock = new ErrorMessage(ErrorMessage.EErrorCode.PROCESSING_ERROR, "Test");
    payloadsMock.add(msgMock);
    payloadsMock.add(msgMock);
    IPayload payload = new PayloadTestImpl();
    payloadsMock.add(payload);
    payloadsMock.add(payload);
    classUnderTest.handleCallback();

    assertEquals(2, classUnderTest.getPayloads().size());
    ArgumentCaptor<List> errorArgument = ArgumentCaptor.forClass(List.class);
    verify(callbackMock, times(1)).handleError(any(Header.class), errorArgument.capture());
    assertEquals("Errors should only contain two element.", 2, errorArgument.getValue().size());
  }

  @Test
  public void castIncomingPacket_NoPacketType_ErrorMsgAdded_test() throws Exception {
    classUnderTest = new CallbackExecutorTestImpl(mock(Header.class), errorsMock, callbackMock);
    classUnderTest.handleCallback();
    assertEquals(1, classUnderTest.getErrors().size());
  }

  @Test
  public void castIncomingPacket_PacketIsNull_AddErrorMessage_test() throws Exception {
    classUnderTest = new CallbackExecutorTestImpl(null, errorsMock, callbackMock);
    classUnderTest.handleCallback();
    assertEquals(1, classUnderTest.getErrors().size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void handleMessage_PacketContainsErrors_testOrderOfExecution_test() throws Exception {

    ErrorMessage msgMock = new ErrorMessage(ErrorMessage.EErrorCode.PROCESSING_ERROR, "Test");
    payloadsMock.add(msgMock);
    IPayload payload = new PayloadTestImpl();
    payloadsMock.add(payload);
    payloadsMock.add(msgMock);
    payloadsMock.add(payload);

    CallbackExecutorTestImpl spyCuT = spy(classUnderTest);
    InOrder inOrder = inOrder(callbackMock, spyCuT);

    spyCuT.handleCallback();

    inOrder.verify(callbackMock, times(1))
        .handleError(any(Header.class), anyList());
    inOrder.verify(spyCuT).executeHandleCallback(anyList());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void handleMessage_CorrectPacket_testOrderOfExecution_test() throws Exception {

    IPayload payload = new PayloadTestImpl();
    payloadsMock.add(payload);
    payloadsMock.add(payload);

    CallbackExecutorTestImpl spyCuT = spy(classUnderTest);
    InOrder inOrder = inOrder(callbackMock, spyCuT);

    spyCuT.handleCallback();

    inOrder.verify(spyCuT).executeHandleCallback(anyList());
  }

  @Test
  public void handleMessage_executionThrowsException_test() throws Exception {

    IPayload payload = new PayloadTestImpl();
    payloadsMock.add(payload);
    payloadsMock.add(payload);

    classUnderTest.setThrowDuringExecution(true);

    classUnderTest.handleCallback();

    assertEquals("An error should be added", 1, classUnderTest.getErrors().size());

  }


  private class CallbackExecutorTestImpl extends CallbackExecutor {

    private boolean throwDuringExecution = false;

    public CallbackExecutorTestImpl(IEncodable decodedPacket, List<ErrorMessage> errors,
                                    ICallbackBase callback) {
      super(decodedPacket, errors, callback);
    }

    @Override
    protected void executeHandleCallback(List<IPayload> payloads) throws Exception {
      if (throwDuringExecution) {
        throw new LlCoreRuntimeException("TestException");
      }
    }

    public void setThrowDuringExecution(boolean throwDuringExecution) {
      this.throwDuringExecution = throwDuringExecution;
    }
  }



  class PayloadTestImpl extends PayloadBase {

    @Override
    public void encode(IEncoder encoder) {
    }

    @Override
    public void decode(IDecoder decoder) {
    }

    @Override
    public String getType() {
      return "TestPayload";
    }

    @Override
    public void decodingCompleted() {
    }

    @Override
    public void validate() {
    }
  }
}

