//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.reply.impl;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.encodables.Packet;
import at.ac.ait.lablink.core.connection.rpc.RpcHeader;
import at.ac.ait.lablink.core.connection.rpc.reply.IRpcReplyCallback;
import at.ac.ait.lablink.core.connection.rpc.request.IRpcRequestCallback;

import org.junit.Test;

import java.util.List;

/**
 * Unit tests for class RpcReplyCallbackExecutor.
 */
public class RpcReplyCallbackExecutorTest {

  RpcReplyCallbackExecutor cut;
  IRpcReplyCallback cb = mock(IRpcReplyCallback.class);


  @Test(expected = NullPointerException.class)
  @SuppressWarnings("unchecked")
  public void create_nullCallback_ShouldThrowException_test() throws Exception {
    cut = new RpcReplyCallbackExecutor(mock(Packet.class),mock(List.class),null);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void executeHandleCallback_callHandleMethod() throws Exception {
    cut = new RpcReplyCallbackExecutor(mock(Packet.class),mock(List.class),cb);
    cut.executeHandleCallback(mock(List.class));

    //verify(cb,times(1)).handleReply(any(RpcHeader.class),anyList());
    verify(cb,times(1)).handleReply(eq(null),anyList());
  }

}