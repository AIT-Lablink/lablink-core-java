//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.messaging.impl;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import at.ac.ait.lablink.core.connection.dispatching.CallbackExecutor;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.messaging.IMessageCallback;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests for class MessageCallbackExecutorFactory.
 */
public class MessageCallbackExecutorFactoryTest {

  MessageCallbackExecutorFactory cut;
  IMessageCallback cbMock;

  @Before
  public void setUp() throws Exception {

    cbMock = mock(IMessageCallback.class);

  }


  @Test(expected = NullPointerException.class)
  public void construct_nullCallback_throwException_test() throws Exception {
    cut = new MessageCallbackExecutorFactory(null);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void createExecutor_returnNewElement_test() throws Exception {
    cut = new MessageCallbackExecutorFactory(cbMock);
    CallbackExecutor exe = cut.createCallbackExecutor(mock(IEncodable.class),mock(List.class));
    assertThat(exe, instanceOf(MessageCallbackExecutor.class));
  }

  @Test
  public void checkEquals_TwoEqualCallbacks_ShouldBeEquals_test() throws Exception {
    cut = new MessageCallbackExecutorFactory(cbMock);
    MessageCallbackExecutorFactory cut2 = new MessageCallbackExecutorFactory(cbMock);

    assertTrue("Factories should be equals if callbacks are equals.", cut.equals(cut2));
  }
}