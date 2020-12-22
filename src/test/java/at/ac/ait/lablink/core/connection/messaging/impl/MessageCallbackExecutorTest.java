//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.messaging.impl;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.encodables.Packet;
import at.ac.ait.lablink.core.connection.messaging.IMessageCallback;
import at.ac.ait.lablink.core.connection.messaging.MsgHeader;

import org.junit.Test;

import java.util.List;

/**
 * Unit tests for class MessageCallbackExecutor.
 */
public class MessageCallbackExecutorTest {

  MessageCallbackExecutor cut;
  IMessageCallback cb = mock(IMessageCallback.class);


  @Test(expected = NullPointerException.class)
  @SuppressWarnings("unchecked")
  public void create_nullCallback_ShouldThrowException_test() throws Exception {
    cut = new MessageCallbackExecutor(mock(Packet.class), mock(List.class), null);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void executeHandleCallback_callHandleMethod() throws Exception {
    cut = new MessageCallbackExecutor(mock(IEncodable.class), mock(List.class), cb);
    cut.executeHandleCallback(mock(List.class));

    //verify(cb, times(1)).handleMessage(any(MsgHeader.class), anyList());
    verify(cb, times(1)).handleMessage(eq(null), anyList());
  }

}