//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.dispatching.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import at.ac.ait.lablink.core.connection.dispatching.IDispatcherInterface;
import at.ac.ait.lablink.core.connection.mqtt.IMqttSubscriber;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Unit Tests for RootDispatchingTreeNode.
 */
@SuppressWarnings("unchecked")
public class RootDispatchingTreeNodeTest {

  RootDispatchingTreeNode rdt;
  private IMqttSubscriber mqttSubscriber;

  @Before
  public void setUp() throws Exception {
    rdt = new RootDispatchingTreeNode("ait.ac.at");
    mqttSubscriber = mock(IMqttSubscriber.class);
    rdt.setMqttSubscriber(mqttSubscriber);
  }

  @Test
  public void construction_InitializationAsRoot_ParentShouldBeNull_test() {
    assertNull(rdt.getParent());
    assertTrue(rdt.isTreeRoot());
  }

  @Test
  public void canBeRemoved_Initialization_ShouldAlwaysReturnFalse_test() {
    assertFalse(this.rdt.canBeRemoved());
  }

  @Test
  public void addDispatcher_registeredCallback_shouldSubscribe_test() throws Exception {
    IDispatcherInterface dispatcherMock = new DispatchingTreeNode();
    dispatcherMock.addCallback(mock(DispatcherCallbackImpl.class));

    List<String> topicM1 = Arrays.asList("ait.ac.at", "msg", "meter3", "van");
    rdt.addDispatcher(topicM1, dispatcherMock);

    assertTrue(rdt.hasDispatcher(topicM1));
    assertEquals(topicM1, dispatcherMock.getFullName());
    verify(mqttSubscriber, times(1)).subscribe(anyList());
  }

  @Test
  public void addDispatcher_noRegisteredCallback_shouldNotSubscribe_test() throws Exception {
    IDispatcherInterface dispatcherMock = new DispatchingTreeNode();

    List<String> topicM1 = Arrays.asList("ait.ac.at", "msg", "meter3", "van");
    rdt.addDispatcher(topicM1, dispatcherMock);

    assertTrue(rdt.hasDispatcher(topicM1));
    verify(mqttSubscriber, times(0)).subscribe(anyList());
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void addDispatcher_falseRootName_shouldThrowException_test() throws Exception {
    IDispatcherInterface dispatcherMock = new DispatchingTreeNode();
    dispatcherMock.addCallback(mock(DispatcherCallbackImpl.class));

    List<String> topicM1 = Arrays.asList("falseName", "msg", "meter3", "van");
    rdt.addDispatcher(topicM1, dispatcherMock);
  }

  @Test
  public void removeDispatcher_registeredCallback_shouldUnsubscribe_test() throws Exception {
    IDispatcherInterface dispatcherMock = new DispatchingTreeNode();
    dispatcherMock.addCallback(mock(DispatcherCallbackImpl.class));

    List<String> topicM1 = Arrays.asList("ait.ac.at", "msg", "meter3", "van");
    rdt.addDispatcher(topicM1, dispatcherMock);

    rdt.removeDispatcher(topicM1);
    verify(mqttSubscriber, times(1)).unsubscribe(anyList());
  }

  @Test
  public void removeDispatcher_noRegisteredCallback_shouldNotUnsubscribe_test() throws Exception {
    IDispatcherInterface dispatcherMock = new DispatchingTreeNode();

    List<String> topicM1 = Arrays.asList("ait.ac.at", "msg", "meter3", "van");
    rdt.addDispatcher(topicM1, dispatcherMock);

    rdt.removeDispatcher(topicM1);
    verify(mqttSubscriber, times(0)).unsubscribe(anyList());
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void removeDispatcher_falseRootName_shouldThrowException_test() throws Exception {
    IDispatcherInterface dispatcherMock = new DispatchingTreeNode();
    dispatcherMock.addCallback(mock(DispatcherCallbackImpl.class));

    List<String> topicM1 = Arrays.asList("falseName", "msg", "meter3", "van");
    rdt.removeDispatcher(topicM1);
  }

  @Test
  public void getDispatcher_dispatcherIsAvailable_ShouldReturnDispatcher() throws Exception {
    IDispatcherInterface dispatcherMock = new DispatchingTreeNode();
    dispatcherMock.addCallback(mock(DispatcherCallbackImpl.class));

    List<String> topicM1 = Arrays.asList("ait.ac.at", "msg", "meter3", "vcn");
    rdt.addDispatcher(topicM1, dispatcherMock);

    rdt.printTree("", true);
    assertEquals(topicM1, rdt.getDispatcher(topicM1).getFullName());
    assertEquals(dispatcherMock, rdt.getDispatcher(topicM1));
  }

  @Test
  public void getDispatcher_dispatcherNotAvailable_ShouldReturnNull() throws Exception {
    List<String> topicM1 = Arrays.asList("ait.ac.at", "msg", "meter3", "van");

    assertNull(rdt.getDispatcher(topicM1));
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void getDispatcher_falseRootName_shouldThrowException_test() throws Exception {

    List<String> topicM1 = Arrays.asList("falseName", "msg", "meter3", "van");
    rdt.getDispatcher(topicM1);
  }

  @Test
  public void hasDispatcher_dispatcherIsAvailable_ShouldReturnTrue() throws Exception {
    IDispatcherInterface dispatcherMock = new DispatchingTreeNode();
    dispatcherMock.addCallback(mock(DispatcherCallbackImpl.class));

    List<String> topicM1 = Arrays.asList("ait.ac.at", "msg", "meter3", "vcn");
    rdt.addDispatcher(topicM1, dispatcherMock);

    assertTrue(rdt.hasDispatcher(topicM1));
  }

  @Test
  public void hasDispatcher_dispatcherNotAvailable_ShouldReturnFalse() throws Exception {
    List<String> topicM1 = Arrays.asList("ait.ac.at", "msg", "meter3", "vcn");
    assertFalse(rdt.hasDispatcher(topicM1));
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void hasDispatcher_falseRootName_shouldThrowException_test() throws Exception {

    List<String> topicM1 = Arrays.asList("falseName", "msg", "meter3", "van");
    rdt.hasDispatcher(topicM1);
  }

  @Test
  public void handleRawMqttMessage() throws Exception {

    DispatcherCallbackImpl callback = mock(DispatcherCallbackImpl.class);
    IDispatcherInterface dispatcherMock = new DispatchingTreeNode();
    dispatcherMock.addCallback(callback);

    List<String> topicM1 = Arrays.asList("ait.ac.at", "msg", "meter3", "vcn");
    rdt.addDispatcher(topicM1, dispatcherMock);
    List<String> topicM2 = Arrays.asList("ait.ac.at", "msg", "meter3","#");
    rdt.addDispatcher(topicM2, dispatcherMock);

    rdt.handleRawMqttMessage("ait.ac.at/msg/meter3/vcn",new byte[0]);

    verify(callback,times(2)).handleMessage(any(byte[].class));
  }

  @Test
  public void onEstablishedMqttConnection() throws Exception {
    IDispatcherInterface dispatcherMock = new DispatchingTreeNode();
    dispatcherMock.addCallback(mock(DispatcherCallbackImpl.class));

    List<String> topicM1 = Arrays.asList("ait.ac.at", "msg", "meter3", "vcn");
    rdt.addDispatcher(topicM1, dispatcherMock);
    List<String> topicM2 = Arrays.asList("ait.ac.at", "msg", "meter5");
    rdt.addDispatcher(topicM2, dispatcherMock);

    rdt.onEstablishedMqttConnection();

    verify(mqttSubscriber,times(3)).subscribe(anyList());
  }

  @Test
  public void onDisconnectingMqttConnection_UnsubscribeAllTopics_test() throws Exception {
    IDispatcherInterface dispatcherMock = new DispatchingTreeNode();
    dispatcherMock.addCallback(mock(DispatcherCallbackImpl.class));

    List<String> topicM1 = Arrays.asList("ait.ac.at", "msg", "meter3", "vcn");
    rdt.addDispatcher(topicM1, dispatcherMock);
    List<String> topicM2 = Arrays.asList("ait.ac.at", "msg", "meter5");
    rdt.addDispatcher(topicM2, dispatcherMock);

    rdt.onDisconnectingMqttConnection();

    verify(mqttSubscriber,times(1)).unsubscribe(anyList());
  }
}