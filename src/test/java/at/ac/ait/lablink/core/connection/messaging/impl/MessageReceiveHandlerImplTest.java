//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.messaging.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import at.ac.ait.lablink.core.connection.ClientIdentifier;
import at.ac.ait.lablink.core.connection.dispatching.IRootDispatcher;
import at.ac.ait.lablink.core.connection.dispatching.impl.DispatchingTreeNode;
import at.ac.ait.lablink.core.connection.dispatching.impl.RootDispatchingTreeNode;
import at.ac.ait.lablink.core.connection.encoding.DecoderBase;
import at.ac.ait.lablink.core.connection.encoding.impl.DecoderFactory;
import at.ac.ait.lablink.core.connection.messaging.IMessageCallback;
import at.ac.ait.lablink.core.connection.mqtt.IMqttSubscriber;
import at.ac.ait.lablink.core.connection.topic.MsgSubscription;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * Unit tests for class MessageReceiveHandlerImpl.
 */
public class MessageReceiveHandlerImplTest {

  ClientIdentifier clientId;
  MessageReceiveHandlerImpl msgReceiver;
  IRootDispatcher rootDispatcher;
  DecoderBase decoder = mock(DecoderBase.class);

  MsgSubscription subscription;
  IMessageCallback testCallback = mock(IMessageCallback.class);

  @Before
  public void setUp() throws Exception {

    rootDispatcher = new RootDispatchingTreeNode("top");
    rootDispatcher.setMqttSubscriber(mock(IMqttSubscriber.class));

    clientId =
        new ClientIdentifier(Collections.singletonList("top"), "TestApp", "group1", "client1");

    DecoderFactory decoderFactory = mock(DecoderFactory.class);
    when(decoderFactory.getDecoderObject(any(DecoderFactory.EDecoderType.class)))
        .thenReturn(decoder);
    when(decoderFactory.getDefaultDecoderObject()).thenReturn(decoder);

    msgReceiver = new MessageReceiveHandlerImpl("msg", clientId);
    msgReceiver.setDecoderFactory(decoderFactory);
    msgReceiver.setRootDispatcher(rootDispatcher);

    subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectElement("Voltage").build();
  }

  @Test
  public void registerMessageHandler_NoDispatcherAvailable_AddDispatcher_test() throws Exception {

    msgReceiver.registerMessageHandler(subscription, testCallback);

    boolean
        hasDispatcher =
        rootDispatcher.hasDispatcher(Arrays.asList("top", "TestApp", "msg", "+", "+", "Voltage"));
    assertTrue("Registration doesn't add dispatcher", hasDispatcher);
  }

  @Test
  public void registerMessageHandler_DispatcherAvailable_OnlyAddCallback_test() throws Exception {
    msgReceiver.registerMessageHandler(subscription, testCallback);
    msgReceiver.registerMessageHandler(subscription, mock(IMessageCallback.class));

    DispatchingTreeNode
        dispatcher =
        (DispatchingTreeNode) rootDispatcher
            .getDispatcher(Arrays.asList("top", "TestApp", "msg", "+", "+", "Voltage"));
    int callbacksSize = dispatcher.getCallbackHandlers().size();
    assertEquals("There should be two callbacks registered", 2, callbacksSize);
  }

  @Test
  public void registerMessageHandler_DispatcherAvailableAddSameCallbackTwice_DoNothing_test()
      throws Exception {

    msgReceiver.registerMessageHandler(subscription, testCallback);
    msgReceiver.registerMessageHandler(subscription, testCallback);

    DispatchingTreeNode
        dispatcher =
        (DispatchingTreeNode) rootDispatcher
            .getDispatcher(Arrays.asList("top", "TestApp", "msg", "+", "+", "Voltage"));
    int callbacksSize = dispatcher.getCallbackHandlers().size();
    assertEquals("There should only be one callback registered", 1, callbacksSize);
  }

  @Test
  public void unregisterMessageHandler_NoDispatcherAvailable_DoNothing_test() throws Exception {
    msgReceiver.registerMessageHandler(subscription, testCallback);

    MsgSubscription
        subscription2 =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectElement("Voltage").addSubjectElement("L1").build();

    msgReceiver.unregisterMessageHandler(subscription2, testCallback);
  }

  @Test
  public void unregisterMessageHandler_MoreCallbacksRegistered_OnlyRemoveCallback_test()
      throws Exception {

    msgReceiver.registerMessageHandler(subscription, testCallback);
    msgReceiver.registerMessageHandler(subscription, mock(IMessageCallback.class));

    DispatchingTreeNode
        dispatcher =
        (DispatchingTreeNode) rootDispatcher
            .getDispatcher(Arrays.asList("top", "TestApp", "msg", "+", "+", "Voltage"));
    int callbacksSize = dispatcher.getCallbackHandlers().size();
    assertEquals("There should be two callbacks registered", 2, callbacksSize);

    msgReceiver.unregisterMessageHandler(subscription, testCallback);

    boolean
        hasDispatcher =
        rootDispatcher.hasDispatcher(Arrays.asList("top", "TestApp", "msg", "+", "+", "Voltage"));
    assertTrue("Dispatcher should be still available after deregistration.", hasDispatcher);

    callbacksSize = dispatcher.getCallbackHandlers().size();
    assertEquals("There should only be one callback registered", 1, callbacksSize);
  }

  @Test
  public void unregisterMessageHandler_OnlyOneCallbacksRegistered_RemoveDispatcher_test()
      throws Exception {

    msgReceiver.registerMessageHandler(subscription, testCallback);

    DispatchingTreeNode
        dispatcher =
        (DispatchingTreeNode) rootDispatcher
            .getDispatcher(Arrays.asList("top", "TestApp", "msg", "+", "+", "Voltage"));
    int callbacksSize = dispatcher.getCallbackHandlers().size();
    assertEquals("There should be two callbacks registered", 1, callbacksSize);

    msgReceiver.unregisterMessageHandler(subscription, testCallback);

    boolean
        hasDispatcher =
        rootDispatcher.hasDispatcher(Arrays.asList("top", "TestApp", "msg", "+", "+", "Voltage"));
    assertFalse("Dispatcher should be removed from DispatchingTree.", hasDispatcher);
  }
}