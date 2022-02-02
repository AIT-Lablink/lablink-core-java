//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.request.impl;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import at.ac.ait.lablink.core.connection.dispatching.CallbackExecutor;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.rpc.request.IRpcRequestCallback;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests for class RpcRequestCallbackExecutorFactory.
 */
public class RpcRequestCallbackExecutorFactoryTest {

  RpcRequestCallbackExecutorFactory cut;
  IRpcRequestCallback cbMock;
  RpcReplyPublisher publisher;

  @Before
  public void setUp() throws Exception {

    cbMock = mock(IRpcRequestCallback.class);
    publisher = mock(RpcReplyPublisher.class);
  }


  @Test(expected = NullPointerException.class)
  public void construct_nullCallback_throwException_test() throws Exception {
    cut = new RpcRequestCallbackExecutorFactory(null,publisher);
  }

  @Test(expected = NullPointerException.class)
  @SuppressWarnings("unchecked")
  public void construct_nullPublisher_throwException_test() throws Exception {
    cut = new RpcRequestCallbackExecutorFactory(cbMock,null);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void createExecutor_returnNewElement_test() throws Exception {
    cut = new RpcRequestCallbackExecutorFactory(cbMock, publisher);
    CallbackExecutor exe = cut.createCallbackExecutor(mock(IEncodable.class), mock(List.class));
    assertEquals(exe.getClass(), RpcRequestCallbackExecutor.class);
  }

  @Test
  public void checkEquals_TwoEqualCallbacks_ShouldBeEquals_test() throws Exception {
    cut = new RpcRequestCallbackExecutorFactory(cbMock, publisher);
    RpcRequestCallbackExecutorFactory
        cut2 =
        new RpcRequestCallbackExecutorFactory(cbMock, publisher);

    assertTrue("Factories should be equals if callbacks are equals.", cut.equals(cut2));
  }
}