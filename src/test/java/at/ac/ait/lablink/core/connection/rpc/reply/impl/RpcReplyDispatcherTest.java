//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.reply.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import at.ac.ait.lablink.core.connection.dispatching.IDispatcherInterface;
import at.ac.ait.lablink.core.connection.dispatching.impl.DispatchingTreeNode;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Unit tests for class RpcReplyDispatcher.
 */
public class RpcReplyDispatcherTest {

  DispatchingTreeNode classUnderTest;

  @Before
  public void setUp() throws Exception {
    classUnderTest = new RpcReplyDispatcher("group1", "client1");
  }

  @Test
  public void addDispatcher_NoClientGroupInName_ShouldBeAdded_test() throws Exception {
    List<String> name = Arrays.asList("Test", "Second");
    classUnderTest.addDispatcher(name.iterator(), mock(IDispatcherInterface.class));

    Iterator<String> expectedPlace = Arrays.asList("group1","client1","Test", "Second").iterator();
    assertNotNull("Rpc Dispatcher hasn't added new dispatcher.",
        classUnderTest.getDispatcher(expectedPlace));
  }

  @Test
  public void addDispatcher_ClientGroupInName_ShouldBeAdded_test() throws Exception {
    List<String> name = Arrays.asList("group1","client1","Test", "Second");
    classUnderTest.addDispatcher(name.iterator(), mock(IDispatcherInterface.class));

    Iterator<String> expectedPlace = Arrays.asList("group1","client1","Test", "Second").iterator();
    assertNotNull("Rpc Dispatcher hasn't added new dispatcher.",
        classUnderTest.getDispatcher(expectedPlace));
  }


  @Test
  public void addDispatcher_NoTopicGiven_ShouldntBeAdded_test() throws Exception {
    List<String> name = Arrays.asList("group1","client1");
    classUnderTest.addDispatcher(name.iterator(), mock(IDispatcherInterface.class));

    Iterator<String> expectedPlace = Arrays.asList("group1","client1").iterator();
    assertNull("Rpc Dispatcher has added new dispatcher but it shouldn'T.",
        classUnderTest.getDispatcher(expectedPlace));
  }


}