//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc;

import static org.junit.Assert.assertEquals;

import at.ac.ait.lablink.core.connection.encoding.encodables.EncodableBaseTest;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * Unit tests for class RpcHeader.
 */
public class RpcHeaderTest extends EncodableBaseTest {

  RpcHeader rpcHeader;

  @Before
  public void setUp() throws Exception {
    rpcHeader = new RpcHeader();
    classUnderTest = rpcHeader;
    expectedName = "rpc-header";
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_DstGroupIdIsNull_shouldThrow_test() throws Exception {
    RpcHeader
        cut =
        new RpcHeader("App", "group1", "client1", Arrays.asList("Sub1", "Sub2"), 12, null,
            "Destclient1", "PacketId");
    cut.validate();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_DstGroupIsEmpty_shouldThrow_test() throws Exception {
    RpcHeader
        cut =
        new RpcHeader("App", "group1", "client1", Arrays.asList("Sub1", "Sub2"), 12, "",
            "Destclient1", "PacketId");
    cut.validate();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_DstClientIdIsNull_shouldThrow_test() throws Exception {
    RpcHeader
        cut =
        new RpcHeader("App", "group1", "client1", Arrays.asList("Sub1", "Sub2"), 12, "Destgroup1",
            null, "PacketId");
    cut.validate();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_DstClientIsEmpty_shouldThrow_test() throws Exception {
    RpcHeader
        cut =
        new RpcHeader("App", "group1", "client1", Arrays.asList("Sub1", "Sub2"), 12, "Destgroup1",
            "", "PacketId");
    cut.validate();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_PacketIdIsNull_shouldThrow_test() throws Exception {
    RpcHeader
        cut =
        new RpcHeader("App", "group1", "client1", Arrays.asList("Sub1", "Sub2"), 12, "Destgroup1",
            "Destclient1", null);
    cut.validate();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_PacketIsEmpty_shouldThrow_test() throws Exception {
    RpcHeader
        cut =
        new RpcHeader("App", "group1", "client1", Arrays.asList("Sub1", "Sub2"), 12, "Destgroup1",
            "Destclient1", "");
    cut.validate();
  }

  @Test
  public void getDestinationGroup_test() throws Exception {
    RpcHeader
        cut =
        new RpcHeader("App", "group1", "client1", Arrays.asList("Sub1", "Sub2"), 12, "Dstgroup1",
            "Destclient1", "PacketId");
    assertEquals("Dstgroup1", cut.getDestinationGroupId());
  }

  @Test
  public void getDestinationClient_test() throws Exception {
    RpcHeader
        cut =
        new RpcHeader("App", "group1", "client1", Arrays.asList("Sub1", "Sub2"), 12, "Dstgroup1",
            "Destclient1", "PacketId");
    assertEquals("Destclient1", cut.getDestinationClientId());
  }

  @Test
  public void getPacketId_test() throws Exception {
    RpcHeader
        cut =
        new RpcHeader("App", "group1", "client1", Arrays.asList("Sub1", "Sub2"), 12, "Dstgroup1",
            "Destclient1", "PacketId");
    assertEquals("PacketId", cut.getPacketId());
  }
}