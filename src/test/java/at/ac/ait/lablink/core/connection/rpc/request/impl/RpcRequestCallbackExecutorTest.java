//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.request.impl;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;
import at.ac.ait.lablink.core.connection.encoding.encodables.Packet;
import at.ac.ait.lablink.core.connection.rpc.RpcHeader;
import at.ac.ait.lablink.core.connection.rpc.request.IRpcRequestCallback;
import at.ac.ait.lablink.core.payloads.ErrorMessage;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * Unit tests for class RpcRequestCallbackExecutor.
 */
public class RpcRequestCallbackExecutorTest {

  RpcRequestCallbackExecutor cut;
  IRpcRequestCallback cb = mock(IRpcRequestCallback.class);
  RpcReplyPublisher publisher = mock(RpcReplyPublisher.class);

  @Test(expected = NullPointerException.class)
  @SuppressWarnings("unchecked")
  public void create_nullCallback_ShouldThrowException_test() throws Exception {
    cut = new RpcRequestCallbackExecutor(mock(Packet.class), mock(List.class), null, publisher);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void executeHandleCallback_callHandleMethod_test() throws Exception {
    cut = new RpcRequestCallbackExecutor(mock(IEncodable.class), mock(List.class), cb, publisher);
    cut.executeHandleCallback(mock(List.class));

    //verify(cb, times(1)).handleRequest(any(RpcHeader.class), anyList());
    verify(cb, times(1)).handleRequest(eq(null), anyList());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void handleCallback_callReplyPublish_test() throws Exception {
    cut =
        new RpcRequestCallbackExecutor(mock(IEncodable.class),
            Collections.<ErrorMessage>emptyList(), cb, publisher);
    cut.sendResponse();

    //verify(publisher, times(1)).publishResponse(any(RpcHeader.class), anyList());
    verify(publisher, times(1)).publishResponse(eq(null), anyList());
  }

}