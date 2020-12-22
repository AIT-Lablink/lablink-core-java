//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import at.ac.ait.lablink.core.connection.ClientIdentifier;
import at.ac.ait.lablink.core.connection.dispatching.ICallbackBase;
import at.ac.ait.lablink.core.connection.dispatching.IDispatcherCallback;
import at.ac.ait.lablink.core.connection.dispatching.IDispatcherInterface;
import at.ac.ait.lablink.core.connection.encoding.encodables.Header;
import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;
import at.ac.ait.lablink.core.connection.encoding.encodables.Packet;
import at.ac.ait.lablink.core.connection.publishing.PublishingManager;
import at.ac.ait.lablink.core.connection.rpc.reply.impl.RpcReplyDispatcher;
import at.ac.ait.lablink.core.connection.topic.RpcDestination;
import at.ac.ait.lablink.core.connection.topic.RpcSubject;
import at.ac.ait.lablink.core.payloads.ErrorMessage;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Unit tests for class RpcRequesterImpl.
 */
public class RpcRequesterImplTest {


  RpcRequesterImpl rpcRequester;
  RpcSubject subject;
  private PublishingManager publishingManager;
  RpcReplyDispatcher rpcRootReplyDispatcher = mock(RpcReplyDispatcher.class);

  IDispatcherCallback callback = mock(IDispatcherCallback.class);
  ICallbackBase errorCallback = mock(ICallbackBase.class);

  List<IPayload> payloads;
  RpcDestination rpcDestination;

  @Before
  public void setUp() throws Exception {

    ClientIdentifier
        clientId =
        new ClientIdentifier(Collections.singletonList("top"), "TestApp", "Dstgroup1",
            "Dstclient1");
    subject = RpcSubject.getBuilder().addSubjectElement("Test").build();

    rpcRequester = new RpcRequesterImpl("req", clientId, subject, callback, errorCallback, null);

    publishingManager = mock(PublishingManager.class);
    rpcRequester.setPublishingManager(publishingManager);
    rpcRequester.setRootReplyDispatcher(rpcRootReplyDispatcher);

    rpcDestination =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_ALL).build();
    payloads = new ArrayList<IPayload>();
  }


  @Test
  @SuppressWarnings("unchecked")
  public void sendRequest_unlimitedReplies_removeAfterTimeout_test() throws Exception {

    rpcRequester.sendRequest(rpcDestination, payloads, -1, 500);
    Thread.sleep(700);
    verify(rpcRootReplyDispatcher, times(1))
        .addDispatcher(any(Iterator.class), any(IDispatcherInterface.class));
    verify(publishingManager).publishPacket(anyList(), any(Packet.class));
    Thread.sleep(400);
    verify(rpcRootReplyDispatcher, times(1)).removeDispatcher(any(Iterator.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sendRequest_NotExpectedNoOfReplies_removeAfterTimeoutCallErrorHandler_test()
      throws Exception {

    rpcRequester.sendRequest(rpcDestination, payloads, 5, 500);
    Thread.sleep(700);
    verify(rpcRootReplyDispatcher, times(1))
        .addDispatcher(any(Iterator.class), any(IDispatcherInterface.class));

    Thread.sleep(600);
    verify(rpcRootReplyDispatcher, times(1)).removeDispatcher(any(Iterator.class));
    verify(errorCallback, times(1)).handleError(any(Header.class), anyList());
  }

}