//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.topic;

import static org.junit.Assert.assertEquals;

import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.junit.Test;

import java.util.Collections;

/**
 * Unit tests for class MsgSubscription.
 */
public class MsgSubscriptionTest {

  @Test
  public void createMsgSubscription_recvFromAllNoSet_ShouldWork_test() throws Exception {
    MsgSubscription
        actual =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectAllChildren().build();

    assertEquals("Client ID should be set to All", "+", actual.getSubscriptionClientId());
    assertEquals("Group ID should be set to All", "+", actual.getSubscriptionGroupId());
    assertEquals("Subscription topic should be for all", Collections.singletonList("#"),
        actual.getSubscriptionSubject());
  }

  @Test
  public void createMsgSubscription_recvFromAllOnlySetClient_ShouldWork_test() throws Exception {
    MsgSubscription
        actual =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .setSrcClientId("client1").addSubjectAllChildren().build();

    assertEquals("Client ID should be set to All", "+", actual.getSubscriptionClientId());
    assertEquals("Group ID should be set to All", "+", actual.getSubscriptionGroupId());
    assertEquals("Subscription topic should be for all", Collections.singletonList("#"),
        actual.getSubscriptionSubject());
  }

  @Test
  public void createMsgSubscription_recvFromAllOnlySetGroup_ShouldWork_test() throws Exception {
    MsgSubscription
        actual =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .setSrcGroupId("group1").addSubjectAllChildren().build();

    assertEquals("Client ID should be set to All", "+", actual.getSubscriptionClientId());
    assertEquals("Group ID should be set to All", "+", actual.getSubscriptionGroupId());
    assertEquals("Subscription topic should be for all", Collections.singletonList("#"),
        actual.getSubscriptionSubject());
  }

  @Test
  public void createMsgSubscription_recvFromAllSetBoth_ShouldWork_test() throws Exception {
    MsgSubscription
        actual =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .setSrcGroupId("group1").setSrcClientId("client1").addSubjectAllChildren().build();

    assertEquals("Client ID should be set to All", "+", actual.getSubscriptionClientId());
    assertEquals("Group ID should be set to All", "+", actual.getSubscriptionGroupId());
    assertEquals("Subscription topic should be for all", Collections.singletonList("#"),
        actual.getSubscriptionSubject());
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createMsgSubscription_recvFromGroupNoSet_ShouldThrow_test() throws Exception {
    MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_GROUP)
        .addSubjectAllChildren().build();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createMsgSubscription_recvFromGroupOnlySetClient_ShouldThrow_test() throws Exception {
    MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_GROUP)
        .setSrcClientId("client1").addSubjectAllChildren().build();
  }

  @Test
  public void createMsgSubscription_recvFromGroupOnlySetGroup_ShouldWork_test() throws Exception {
    MsgSubscription
        actual =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_GROUP)
            .setSrcGroupId("group1").addSubjectAllChildren().build();

    assertEquals("Client ID should be set to All", "+", actual.getSubscriptionClientId());
    assertEquals("Group ID should be specific set.", "group1", actual.getSubscriptionGroupId());
    assertEquals("Subscription topic should be for all", Collections.singletonList("#"),
        actual.getSubscriptionSubject());
  }

  @Test
  public void createMsgSubscription_recvFromGroupSetBoth_ShouldWork_test() throws Exception {
    MsgSubscription
        actual =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_GROUP)
            .setSrcGroupId("group1").setSrcClientId("client1").addSubjectAllChildren().build();

    assertEquals("Client ID should be set to All", "+", actual.getSubscriptionClientId());
    assertEquals("Group ID should be specific set.", "group1", actual.getSubscriptionGroupId());
    assertEquals("Subscription topic should be for all", Collections.singletonList("#"),
        actual.getSubscriptionSubject());
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createMsgSubscription_recvFromClientNoSet_ShouldThrow_test() throws Exception {
    MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_CLIENT)
        .addSubjectAllChildren().build();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createMsgSubscription_recvFromClientOnlySetClient_ShouldThrow_test()
      throws Exception {
    MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_CLIENT)
        .setSrcClientId("client1").addSubjectAllChildren().build();

  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createMsgSubscription_recvFromClientOnlySetGroup_ShouldThrow_test() throws Exception {
    MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_CLIENT)
        .setSrcGroupId("group1").addSubjectAllChildren().build();
  }

  @Test
  public void createMsgSubscription_recvFromClientSetBoth_ShouldWork_test() throws Exception {
    MsgSubscription
        actual =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_CLIENT)
            .setSrcGroupId("group1").setSrcClientId("client1").addSubjectAllChildren().build();

    assertEquals("Client ID should be specific set.", "client1", actual.getSubscriptionClientId());
    assertEquals("Group ID should be specific set.", "group1", actual.getSubscriptionGroupId());
    assertEquals("Subscription topic should be for all", Collections.singletonList("#"),
        actual.getSubscriptionSubject());
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createMsgSubscription_recvFromClientSetEmptyGroup_ShouldThrow_test()
      throws Exception {
    MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_CLIENT)
        .setSrcGroupId("").setSrcClientId("client1").addSubjectAllChildren().build();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createMsgSubscription_recvFromClientSetEmptyClient_ShouldThrow_test()
      throws Exception {
    MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_CLIENT)
        .setSrcGroupId("group1").setSrcClientId("").addSubjectAllChildren().build();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createMsgSubscription_SetNoSubject_ShouldThrow_test() throws Exception {
    MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_CLIENT)
        .setSrcGroupId("group1").setSrcClientId("client1").build();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createMsgSubscription_SetEmptySubject_ShouldThrow_test() throws Exception {
    MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_CLIENT)
        .setSrcGroupId("group1").setSrcClientId("client1").addSubjectAnyElement()
        .addSubjectElement("").addSubjectAllChildren().build();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createMsgSubscription_SetAllChildNotEnd_ShouldThrow_test() throws Exception {
    MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_CLIENT)
        .setSrcGroupId("group1").setSrcClientId("client1").addSubjectElement("Test1")
        .addSubjectAllChildren().addSubjectAnyElement().build();
  }
}