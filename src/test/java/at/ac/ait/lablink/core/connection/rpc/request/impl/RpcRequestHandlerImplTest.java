//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.request.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import at.ac.ait.lablink.core.connection.ClientIdentifier;
import at.ac.ait.lablink.core.connection.dispatching.IRootDispatcher;
import at.ac.ait.lablink.core.connection.dispatching.impl.RootDispatchingTreeNode;
import at.ac.ait.lablink.core.connection.encoding.DecoderBase;
import at.ac.ait.lablink.core.connection.encoding.impl.DecoderFactory;
import at.ac.ait.lablink.core.connection.mqtt.IMqttSubscriber;
import at.ac.ait.lablink.core.connection.rpc.request.IRpcRequestCallback;
import at.ac.ait.lablink.core.connection.topic.RpcSubject;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * Unit tests for class RpcRequestHandlerImpl.
 */
public class RpcRequestHandlerImplTest {

  ClientIdentifier clientId;
  RpcRequestHandlerImpl requestReceiver;
  IRootDispatcher rootDispatcher;
  DecoderBase decoder = mock(DecoderBase.class);

  RpcSubject subject;
  IRpcRequestCallback testCallback = mock(IRpcRequestCallback.class);

  @Before
  public void setUp() throws Exception {

    rootDispatcher = new RootDispatchingTreeNode("top");
    rootDispatcher.setMqttSubscriber(mock(IMqttSubscriber.class));

    clientId =
        new ClientIdentifier(Collections.singletonList("top"), "TestApp", "group1", "client1");

    requestReceiver = new RpcRequestHandlerImpl("req", clientId);
    DecoderFactory decoderFactory = mock(DecoderFactory.class);
    when(decoderFactory.getDecoderObject(any(DecoderFactory.EDecoderType.class)))
        .thenReturn(decoder);
    when(decoderFactory.getDefaultDecoderObject()).thenReturn(decoder);
    requestReceiver.setDecoderFactory(decoderFactory);
    requestReceiver.setRootDispatcher(rootDispatcher);
    requestReceiver.setRpcReplyPublisher(mock(RpcReplyPublisher.class));

    subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
  }

  @Test
  public void registerMessageHandler_NoDispatcherAvailable_AddDispatcher_test() throws Exception {

    requestReceiver.registerRequestHandler(subject, testCallback);

    boolean
        hasDispatcher =
        rootDispatcher
            .hasDispatcher(Arrays.asList("top", "TestApp", "req", "group1", "client1", "Test"));
    assertTrue("Registration doesn't add dispatcher", hasDispatcher);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void registerMessageHandler_DispatcherAvailable_ThrowError_test() throws Exception {
    requestReceiver.registerRequestHandler(subject, testCallback);
    requestReceiver.registerRequestHandler(subject, mock(IRpcRequestCallback.class));

  }


  @Test
  public void unregisterMessageHandler_NoDispatcherAvailable_DoNothing_test() throws Exception {
    requestReceiver.registerRequestHandler(subject, testCallback);

    requestReceiver.unregisterRequestHandler(subject, testCallback);

    boolean
        hasDispatcher =
        rootDispatcher
            .hasDispatcher(Arrays.asList("top", "TestApp", "req", "group1", "client1", "Test"));
    assertFalse("Dispatcher should be deleted.", hasDispatcher);
  }

  @Test
  public void reregisterMessageHandler_AfterSuccessfulRemove_AddDispatcher_test() throws Exception {
    requestReceiver.registerRequestHandler(subject, testCallback);
    requestReceiver.unregisterRequestHandler(subject, testCallback);
    requestReceiver.registerRequestHandler(subject, testCallback);

    boolean
        hasDispatcher =
        rootDispatcher
            .hasDispatcher(Arrays.asList("top", "TestApp", "req", "group1", "client1", "Test"));
    assertTrue("Registration doesn't add dispatcher", hasDispatcher);
  }
}