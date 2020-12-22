//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.topic;

import static org.junit.Assert.assertEquals;

import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.junit.Test;

/**
 * Unit tests for class RpcDestination.
 */
public class RpcDestinationTest {

  @Test
  public void createDestination_sendToAllNoSet_ShouldWork_test() throws Exception {
    RpcDestination
        actual =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_ALL).build();

    assertEquals("Client ID should be set to -ANY-", "-ANY-", actual.getClientId());
    assertEquals("Group ID should be set to -ANY-", "-ANY-", actual.getGroupId());
  }

  @Test
  public void createDestination_sendToAllOnlySetClient_ShouldWork_test() throws Exception {
    RpcDestination
        actual =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_ALL)
            .setClientId("client1").build();

    assertEquals("Client ID should be set to -ANY-", "-ANY-", actual.getClientId());
    assertEquals("Group ID should be set to -ANY-", "-ANY-", actual.getGroupId());
  }

  @Test
  public void createDestination_sendToAllOnlySetGroup_ShouldWork_test() throws Exception {
    RpcDestination
        actual =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_ALL)
            .setGroupId("group1").build();

    assertEquals("Client ID should be set to -ANY-", "-ANY-", actual.getClientId());
    assertEquals("Group ID should be set to -ANY-", "-ANY-", actual.getGroupId());
  }

  @Test
  public void createDestination_sendToAllSetBoth_ShouldWork_test() throws Exception {
    RpcDestination
        actual =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_ALL)
            .setGroupId("group1").setClientId("client1").build();

    assertEquals("Client ID should be set to -ANY-", "-ANY-", actual.getClientId());
    assertEquals("Group ID should be set to -ANY-", "-ANY-", actual.getGroupId());
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createDestination_sendToGroupNoSet_ShouldThrow_test() throws Exception {
    RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_GROUP).build();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createDestination_sendToGroupOnlySetClient_ShouldThrow_test() throws Exception {
    RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_GROUP)
        .setClientId("client1").build();

  }

  @Test
  public void createDestination_sendToGroupOnlySetGroup_ShouldWork_test() throws Exception {
    RpcDestination
        actual =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_GROUP)
            .setGroupId("group1").build();

    assertEquals("Client ID should be set to -ANY-", "-ANY-", actual.getClientId());
    assertEquals("Group ID should be specific set.", "group1", actual.getGroupId());
  }

  @Test
  public void createDestination_sendToGroupSetBoth_ShouldWork_test() throws Exception {
    RpcDestination
        actual =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_GROUP)
            .setGroupId("group1").setClientId("client1").build();

    assertEquals("Client ID should be set to -ANY-", "-ANY-", actual.getClientId());
    assertEquals("Group ID should be specific set.", "group1", actual.getGroupId());
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createDestination_sendToClientNoSet_ShouldThrow_test() throws Exception {
    RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_CLIENT).build();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createDestination_sendToClientOnlySetClient_ShouldThrow_test() throws Exception {
    RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_CLIENT)
        .setClientId("client1").build();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createDestination_sendToClientOnlySetGroup_ShouldThrow_test() throws Exception {
    RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_CLIENT)
        .setGroupId("group1").build();
  }

  @Test
  public void createDestination_sendToClientSetBoth_ShouldWork_test() throws Exception {
    RpcDestination
        actual =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_CLIENT)
            .setGroupId("group1").setClientId("client1").build();

    assertEquals("Client ID should be specific set.", "client1", actual.getClientId());
    assertEquals("Group ID should be specific set.", "group1", actual.getGroupId());
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createDestination_sendToClientSetEmptyGroup_ShouldThrow_test() throws Exception {
    RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_CLIENT).setGroupId("")
        .setClientId("client1").build();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createDestination_sendToClientSetEmptyClient_ShouldThrow_test() throws Exception {
    RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_CLIENT)
        .setGroupId("group1").setClientId("").build();
  }
}