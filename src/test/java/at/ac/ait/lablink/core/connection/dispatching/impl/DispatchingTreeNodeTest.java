//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.dispatching.impl;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import at.ac.ait.lablink.core.connection.dispatching.IDispatcherCallback;
import at.ac.ait.lablink.core.connection.dispatching.IDispatcherInterface;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Unit Test for DispatchingTreeNode.
 */
@RunWith(JUnitParamsRunner.class)
public class DispatchingTreeNodeTest {

  @Test
  public void init_setParentToNull_BeTreeNode_test() {
    DispatchingTreeNode node1 = new DispatchingTreeNode();
    node1.init(null, "Node1");

    assertNull("Parent before initialisation should be empty", node1.getParent());
  }

  @Test
  public void init_setParent_CheckSetParent_test() {
    DispatchingTreeNode node1 = new DispatchingTreeNode();
    DispatchingTreeNode node2 = new DispatchingTreeNode();
    node1.init(null, "Node1");
    node2.init(node1, "Node2");

    assertEquals("The parent should be set during init process", node1, node2.getParent());
  }

  @Test
  public void addDispatcher_AddOneDispatcher_CheckCurrentPosition_test() {
    DispatchingTreeNode rootTree = new DispatchingTreeNode();
    rootTree.init(null, "RootTree");

    DispatchingTreeNode nodeToBeAdded = new DispatchingTreeNode();

    Iterator<String> dispatcherName = Collections.singletonList("Node").iterator();
    rootTree.addDispatcher(dispatcherName, nodeToBeAdded);

    assertEquals(Arrays.asList("RootTree", "Node"), nodeToBeAdded.getFullName());
    assertEquals("Parent element should be root node", rootTree, nodeToBeAdded.getParent());
  }

  @Test
  public void addDispatcher_AddTwoDispatcherDifferentLevels_CheckCorrectHierarchicalSet_test() {
    DispatchingTreeNode rootTree = new DispatchingTreeNode();
    rootTree.init(null, "RootTree");

    DispatchingTreeNode nodeToBeAdded = new DispatchingTreeNode();

    Iterator<String> dispatcherName = Arrays.asList("MiddleNode", "Node").iterator();
    rootTree.addDispatcher(dispatcherName, nodeToBeAdded);
    /* A dispatcher element in the middle should be set. */

    assertEquals(Arrays.asList("RootTree", "MiddleNode", "Node"), nodeToBeAdded.getFullName());
    assertNotEquals("Parent element should not be root node", rootTree, nodeToBeAdded.getParent());
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void addDispatcher_AddSameDispatcherTwice_ShouldThrowException_test() {
    DispatchingTreeNode rootTree = new DispatchingTreeNode();
    rootTree.init(null, "RootTree");

    DispatchingTreeNode nodeToBeAdded = new DispatchingTreeNode();

    Iterator<String> dispatcherName = Collections.singletonList("Node").iterator();
    rootTree.addDispatcher(dispatcherName, nodeToBeAdded);

    Iterator<String> dispatcherName2 = Collections.singletonList("Node").iterator();
    rootTree.addDispatcher(dispatcherName2, nodeToBeAdded);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void addDispatcher_AddDifferentDispatcherSameName_ShouldThrowException_test() {
    DispatchingTreeNode rootTree = new DispatchingTreeNode();
    rootTree.init(null, "RootTree");

    DispatchingTreeNode nodeToBeAdded = new DispatchingTreeNode();
    Iterator<String> dispatcherName = Arrays.asList("MiddleNode", "Node").iterator();
    rootTree.addDispatcher(dispatcherName, nodeToBeAdded);

    DispatchingTreeNode nodeToBeAdded2 = new DispatchingTreeNode();
    Iterator<String> dispatcherName2 = Arrays.asList("MiddleNode", "Node").iterator();
    rootTree.addDispatcher(dispatcherName2, nodeToBeAdded2);
  }

  @Test
  public void addDispatcher_addTwoDifferentDispatcherSameLevel_ShouldBeAdded_test() {
    DispatchingTreeNode rootTree = new DispatchingTreeNode();
    rootTree.init(null, "RootTree");

    DispatchingTreeNode nodeToBeAdded = new DispatchingTreeNode();
    Iterator<String>
        dispatcherName =
        Arrays.asList("MiddleNode", "MiddleNode2", "MiddleNode3", "Node").iterator();
    rootTree.addDispatcher(dispatcherName, nodeToBeAdded);

    DispatchingTreeNode nodeToBeAdded2 = new DispatchingTreeNode();
    Iterator<String>
        dispatcherName2 =
        Arrays.asList("MiddleNode", "MiddleNode2", "MiddleNode3", "Node2").iterator();
    rootTree.addDispatcher(dispatcherName2, nodeToBeAdded2);

    DispatchingTreeNode nodeToBeAdded3 = new DispatchingTreeNode();
    Iterator<String>
        dispatcherName3 =
        Arrays.asList("MiddleNode", "MiddleNode2", "Node3").iterator();
    rootTree.addDispatcher(dispatcherName3, nodeToBeAdded3);

    assertEquals(Arrays.asList("RootTree", "MiddleNode", "MiddleNode2", "MiddleNode3", "Node"),
        nodeToBeAdded.getFullName());
    assertEquals(Arrays.asList("RootTree", "MiddleNode", "MiddleNode2", "MiddleNode3", "Node2"),
        nodeToBeAdded2.getFullName());
    assertEquals(Arrays.asList("RootTree", "MiddleNode", "MiddleNode2", "Node3"),
        nodeToBeAdded3.getFullName());
  }

  @Test
  public void removeDispatcher_removeLeafDispatcher_ShouldBeRemoved_test() {
    DispatchingTreeNode rootTree = generateTestSampleTree();

    Iterator<String>
        removeName =
        Arrays.asList("MiddleNode", "MiddleNode2", "MiddleNode3", "Node2").iterator();
    rootTree.removeDispatcher(removeName);

    Iterator<String>
        checkMiddleElementName =
        Arrays.asList("MiddleNode", "MiddleNode2", "MiddleNode3").iterator();
    assertNotNull(rootTree.getDispatcher(checkMiddleElementName));
  }

  @Test
  public void removeDispatcher_removeLeafDispatcher_AlsoMiddleDispatcherShouldBeRemoved_test() {
    DispatchingTreeNode rootTree = generateTestSampleTree();

    Iterator<String>
        removeName =
        Arrays.asList("MiddleNode", "RemoveNode1", "LeafNode1").iterator();
    rootTree.removeDispatcher(removeName);

    Iterator<String> checkMiddleElementName = Collections.singletonList("MiddleNode").iterator();
    assertNotNull(rootTree.getDispatcher(checkMiddleElementName));

    Iterator<String>
        checkMiddle3ElementName =
        Arrays.asList("MiddleNode", "RemoveNode1").iterator();
    assertNull(rootTree.getDispatcher(checkMiddle3ElementName));

  }

  @Test
  public void removeDispatcher_middleNodeNotAvailable_shouldThrowException_test() {
    DispatchingTreeNode rootTree = generateTestSampleTree();

    Iterator<String>
        removeName =
        Arrays.asList("MiddleNode", "MiddleNode2", "MiddleNode4", "Node3").iterator();
    rootTree.removeDispatcher(removeName);
  }

  @Test
  public void removeDispatcher_removeNonEmptyMiddleElement_shouldClearCallbacks_test() {
    DispatchingTreeNode rootTree = generateTestSampleTree();

    List<String> removeName = Arrays.asList("MiddleNode", "MiddleNode2", "MiddleNode3");
    DispatchingTreeNode node = ((DispatchingTreeNode)rootTree.getDispatcher(removeName.iterator()));
    assertTrue(node.getCallbackHandlers().size() > 0);
    rootTree.removeDispatcher(removeName.iterator());
    assertEquals(0,node.getCallbackHandlers().size());
  }

  @Test
  public void getDispatcher_getAvailableLeaf_shouldReturnADispatcher_test() {
    DispatchingTreeNode rootTree = generateTestSampleTree();

    //rootTree.printTree("", true);
    List<String>
        checkMiddle3ElementName =
        Arrays.asList("MiddleNode", "MiddleNode2", "MiddleNode3");
    assertNotNull(rootTree.getDispatcher(checkMiddle3ElementName.iterator()));
    assertEquals(Arrays.asList("RootTree", "MiddleNode", "MiddleNode2", "MiddleNode3"),
        rootTree.getDispatcher(checkMiddle3ElementName.iterator()).getFullName());

    List<String>
        checkMiddle5ElementName =
        Arrays.asList("MiddleNode", "MiddleNode2", "MiddleNode3", "Node2");
    assertNotNull(rootTree.getDispatcher(checkMiddle5ElementName.iterator()));
    assertEquals(Arrays.asList("RootTree", "MiddleNode", "MiddleNode2", "MiddleNode3", "Node2"),
        rootTree.getDispatcher(checkMiddle5ElementName.iterator()).getFullName());
  }

  @Test
  public void getDispatcher_NonAvailableDispatcher_ShouldReturnNull_test() {
    DispatchingTreeNode rootTree = generateTestSampleTree();

    Iterator<String>
        checkMiddleElementName =
        Arrays.asList("MiddleNode", "MiddleNode10").iterator();
    assertNull(rootTree.getDispatcher(checkMiddleElementName));

    Iterator<String>
        checkMiddle5ElementName =
        Arrays.asList("MiddleNode", "MiddleNode2", "Node3", "Node5", "Node7").iterator();
    assertNull(rootTree.getDispatcher(checkMiddle5ElementName));
  }

  @Test
  public void getDispatcher_getAvailableMiddleNode_ShouldReturnNode_test() {

    DispatchingTreeNode rootTree = generateTestSampleTree();
    Iterator<String>
        checkMiddle3ElementName =
        Arrays.asList("MiddleNode", "MiddleNode2", "MiddleNode3").iterator();
    assertNotNull(rootTree.getDispatcher(checkMiddle3ElementName));

    Iterator<String>
        checkMiddle5ElementName =
        Arrays.asList("MiddleNode", "MiddleNode2").iterator();
    assertNotNull(rootTree.getDispatcher(checkMiddle5ElementName));
  }

  @Test
  @Parameters(method = "executions")
  @TestCaseName("execute ('{0}') should call handler {1} time(s)")
  public void execute_invokeDifferentHandlers_shouldCallHandlerXTimes(String[] executionName,
                                                                      int handlerCalls) {
    IDispatcherCallback callback = mock(IDispatcherCallback.class);

    DispatchingTreeNode rootNode = generateTestSampleTreeWithCallbackAndWildcards(callback);
    //rootNode.printTree("",true);
    rootNode.execute(Arrays.asList(executionName), 1, "Hallo".getBytes());
    verify(callback, times(handlerCalls)).handleMessage(any(byte[].class));
  }

  @SuppressWarnings("unused")
  private Object[] executions() {
    return new Object[]{
        new Object[]{new String[]{"RootTree", "Node_L1_1", "Node_L2_1", "Node_L3_1"}, 1},
        new Object[]{new String[]{"RootTree", "Node_L1_1", "Node_L2_1", "Node_L3_1"}, 1},
        new Object[]{new String[]{"RootTree", "Node_L1_1", "test", "Node_L3_2"}, 1},
        new Object[]{new String[]{"RootTree", "Node_L1_2", "Node_L2_1"}, 2},
        new Object[]{new String[]{"RootTree", "Node_L1_2", "test"}, 1},
        new Object[]{new String[]{"RootTree", "Node_L1_2"}, 1},
        new Object[]{new String[]{"RootTree", "Node_L1_3"}, 1},
        new Object[]{new String[]{"RootTree", "Node_L1_3", "Node_L2_2"}, 0},
        new Object[]{new String[]{"RootTree", "Node_L1_3", "Node_L2_2", "Node_L3_3"}, 1},
        new Object[]{new String[]{"TestNode", "Node_L1_3", "Node_L2_2","TestNode","TestNode"}, 0}};
  }

  @Test
  public void getAllSubscriptions_fromRoot_shouldReturnDispatcherWithRegisteredCallbacks_test() {
    DispatchingTreeNode rootTree = generateTestSampleTree();

    List<List<String>> expected = new ArrayList<List<String>>();
    expected.add(Arrays.asList("RootTree", "MiddleNode", "MiddleNode2", "MiddleNode3"));
    expected.add(Arrays.asList("RootTree", "MiddleNode", "MiddleNode2", "MiddleNode3", "Node2"));
    expected.add(Arrays.asList("RootTree", "MiddleNode", "MiddleNode2", "Node3"));
    expected.add(Arrays.asList("RootTree", "MiddleNode", "RemoveNode1", "LeafNode1"));
    List<List<String>> readSubscription = rootTree.getAllSubscriptions();

    /* Check two Lists order is not relevant */
    assertTrue(
        "Not all subscriptions equals\nexpected: " + expected + " \nget: " + readSubscription,
        expected.containsAll(readSubscription) && readSubscription.containsAll(expected));
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void getSubscriptions_FromNonAvailableDispatcher_ShouldThrowException_test() {
    DispatchingTreeNode rootTree = generateTestSampleTree();

    Iterator<String>
        checkMiddle5ElementName =
        Arrays.asList("MiddleNode", "MiddleNode5").iterator();

    rootTree.getSubscriptions(checkMiddle5ElementName);
  }

  @Test
  public void getSubscriptions_FromAvailableLeafDispatcher_ShouldReturnSubscriptionList_test() {
    DispatchingTreeNode rootTree = generateTestSampleTree();

    Iterator<String>
        checkMiddle5ElementName =
        Arrays.asList("MiddleNode", "MiddleNode2", "MiddleNode3", "Node2").iterator();

    List<List<String>> expected = new ArrayList<List<String>>();
    expected.add(Arrays.asList("RootTree", "MiddleNode", "MiddleNode2", "MiddleNode3", "Node2"));

    assertEquals(expected, rootTree.getSubscriptions(checkMiddle5ElementName));
  }

  @Test
  public void getSubscriptions_FromAvailableMiddleDispatcher_ShouldReturnSubscriptionList_test() {
    DispatchingTreeNode rootTree = generateTestSampleTree();

    Iterator<String>
        checkMiddle5ElementName =
        Arrays.asList("MiddleNode", "MiddleNode2", "MiddleNode3").iterator();

    List<List<String>> expected = new ArrayList<List<String>>();
    expected.add(Arrays.asList("RootTree", "MiddleNode", "MiddleNode2", "MiddleNode3"));

    assertEquals(expected, rootTree.getSubscriptions(checkMiddle5ElementName));
  }

  @Test
  public void 
      getSubscriptions_FromDispatcherWithNonRegisteredCallback_ShouldReturnEmptyList_test() {
    DispatchingTreeNode rootTree = generateTestSampleTree();

    Iterator<String>
        checkMiddle5ElementName =
        Arrays.asList("MiddleNode", "MiddleNode2").iterator();

    List<List<String>> expected = new ArrayList<List<String>>();
    assertEquals(expected, rootTree.getSubscriptions(checkMiddle5ElementName));
  }

  @Test
  public void addCallback_addTwoDifferentCallbacks_TwoShouldBeRegistered_test() {
    DispatchingTreeNode classUnderTest = new DispatchingTreeNode();
    classUnderTest.init(null, "ClassUnderTest");
    IDispatcherCallback callback1 = mock(IDispatcherCallback.class);
    IDispatcherCallback callback2 = mock(IDispatcherCallback.class);

    classUnderTest.addCallback(callback1);
    classUnderTest.addCallback(callback2);

    assertEquals(2, classUnderTest.getCallbackHandlers().size());
  }

  @Test
  public void addCallback_addSameCallbackTwice_ShouldBeOnlyRegisteredOnce_test() {
    DispatchingTreeNode classUnderTest = new DispatchingTreeNode();
    classUnderTest.init(null, "ClassUnderTest");
    IDispatcherCallback callback1 = mock(IDispatcherCallback.class);

    classUnderTest.addCallback(callback1);
    classUnderTest.addCallback(callback1);

    assertEquals("Callback should only be registered once", 1,
        classUnderTest.getCallbackHandlers().size());
  }

  @Test
  public void getFullName_insertTwoNodes_ShouldReturnWholePath_test() {
    DispatchingTreeNode node1 = new DispatchingTreeNode();
    node1.init(null, "Node1");
    DispatchingTreeNode node2 = new DispatchingTreeNode();
    node2.init(node1, "Node2");
    DispatchingTreeNode classUnderTest = new DispatchingTreeNode();
    classUnderTest.init(node2, "ClassUnderTest");

    List<String> expected = Arrays.asList("Node1", "Node2", "ClassUnderTest");

    assertEquals(expected, classUnderTest.getFullName());
  }


  @Test
  public void canBeRemoved_CheckEmptyDispatcher_ShouldBeRemoveable_test() {
    DispatchingTreeNode classUnderTest = new DispatchingTreeNode();
    classUnderTest.init(null, "ClassUnderTest");
    assertTrue(classUnderTest.canBeRemoved());
  }

  @Test
  public void canBeRemoved_dispatcherHasCallbackRegistered_ShouldNotBeRemoveable_test() {
    DispatchingTreeNode classUnderTest = new DispatchingTreeNode();
    classUnderTest.init(null, "ClassUnderTest");
    IDispatcherCallback callback1 = mock(IDispatcherCallback.class);
    classUnderTest.addCallback(callback1);

    assertFalse(classUnderTest.canBeRemoved());
  }

  @Test
  public void canBeRemoved_dispatcherHasChildRegistered_ShouldNotBeRemoveable_test() {
    DispatchingTreeNode classUnderTest = new DispatchingTreeNode();
    classUnderTest.init(null, "ClassUnderTest");
    DispatchingTreeNode node1 = new DispatchingTreeNode();
    node1.init(classUnderTest, "Node1");
    classUnderTest.getChildren().put("Node1", node1);

    assertFalse(classUnderTest.canBeRemoved());
  }

  @Test
  public void canBeRemoved_dispatcherHasChildAndCallbackRegistered_ShouldNotBeRemoveable_test() {
    DispatchingTreeNode classUnderTest = new DispatchingTreeNode();
    classUnderTest.init(null, "ClassUnderTest");
    DispatchingTreeNode node1 = new DispatchingTreeNode();
    node1.init(classUnderTest, "Node1");
    classUnderTest.getChildren().put("Node1", node1);
    IDispatcherCallback callback1 = mock(IDispatcherCallback.class);
    classUnderTest.addCallback(callback1);

    assertFalse(classUnderTest.canBeRemoved());
  }

  @Test
  public void canBeRemoved_dispatcherIsLeafElement_shouldBeRemoveable_test() {
    DispatchingTreeNode classUnderTest = new DispatchingTreeNode();
    classUnderTest.init(null, "ClassUnderTest");
    DispatchingTreeNode node1 = new DispatchingTreeNode();
    node1.init(null, "Node1");
    DispatchingTreeNode node2 = new DispatchingTreeNode();
    node2.init(node1, "Node2");
    classUnderTest.init(node2, "ClassUnderTest");

    assertTrue(classUnderTest.canBeRemoved());
  }

  @Test
  public void removeCallback_aCallbackIsAvailable_CanBeRemoved_test() {
    DispatchingTreeNode classUnderTest = new DispatchingTreeNode();
    classUnderTest.init(null, "ClassUnderTest");
    IDispatcherCallback callback1 = mock(IDispatcherCallback.class);
    IDispatcherCallback callback2 = mock(IDispatcherCallback.class);

    classUnderTest.addCallback(callback1);
    classUnderTest.addCallback(callback2);
    assertEquals("Handlers can't be added", 2, classUnderTest.getCallbackHandlers().size());

    classUnderTest.removeCallback(callback2);
    assertEquals("Handler can't be removed", 1, classUnderTest.getCallbackHandlers().size());
  }

  @Test
  public void removeCallback_CallbackIsNotAvailable_ShouldDoNothing_test() {
    DispatchingTreeNode classUnderTest = new DispatchingTreeNode();
    classUnderTest.init(null, "ClassUnderTest");
    IDispatcherCallback callback1 = mock(IDispatcherCallback.class);
    IDispatcherCallback callback2 = mock(IDispatcherCallback.class);

    classUnderTest.addCallback(callback1);
    assertEquals("Handler can't be added", 1, classUnderTest.getCallbackHandlers().size());

    classUnderTest.removeCallback(callback2);
    assertEquals("Handler can't be removed", 1, classUnderTest.getCallbackHandlers().size());
  }

  @Test
  public void hasCallbacksRegistered_NoCallbackIsRegistered_test() {
    DispatchingTreeNode classUnderTest = new DispatchingTreeNode();
    classUnderTest.init(null, "ClassUnderTest");
    IDispatcherCallback callback1 = mock(IDispatcherCallback.class);
    classUnderTest.addCallback(callback1);

    assertEquals("One Callback should be registered", 1,
        classUnderTest.getCallbackHandlers().size());
    assertTrue(classUnderTest.hasCallbacksRegistered());
  }

  @Test
  public void hasCallbacksRegistered_CallbackIsRegistered_test() {
    DispatchingTreeNode classUnderTest = new DispatchingTreeNode();
    classUnderTest.init(null, "ClassUnderTest");

    assertEquals("No Callback should be registered", 0,
        classUnderTest.getCallbackHandlers().size());
    assertFalse(classUnderTest.hasCallbacksRegistered());
  }

  @Test
  public void isRootTree_CheckRootElement_ShouldBeTrue_test() {

    DispatchingTreeNode node1 = new DispatchingTreeNode();
    node1.init(null, "Node1");

    assertTrue(node1.isTreeRoot());
  }


  @Test
  public void isRootTree_CheckLeafElement_ShouldBeFalse_test() {

    DispatchingTreeNode node1 = new DispatchingTreeNode();
    DispatchingTreeNode node2 = new DispatchingTreeNode();
    node1.init(null, "Node1");
    node2.init(node1, "Node2");

    assertFalse(node2.isTreeRoot());
  }

  @Test
  public void printTree_test() {

    IDispatcherCallback callback = mock(IDispatcherCallback.class);
    DispatchingTreeNode rootNode = generateTestSampleTreeWithCallbackAndWildcards(callback);
    rootNode.printTree("   ", true);
  }

  /* Example Trees as test data */

  private DispatchingTreeNode generateTestSampleTree() {

    DispatchingTreeNode rootTree = new DispatchingTreeNode();
    rootTree.init(null, "RootTree");

    addTestNode(rootTree, Arrays.asList("MiddleNode", "MiddleNode2", "MiddleNode3"),
        mock(IDispatcherCallback.class));
    addTestNode(rootTree, Arrays.asList("MiddleNode", "MiddleNode2", "MiddleNode3", "Node"), null);
    addTestNode(rootTree, Arrays.asList("MiddleNode", "MiddleNode2", "MiddleNode3", "Node2"),
        mock(IDispatcherCallback.class));
    addTestNode(rootTree, Arrays.asList("MiddleNode", "MiddleNode2", "Node3"),
        mock(IDispatcherCallback.class));
    addTestNode(rootTree, Arrays.asList("MiddleNode", "RemoveNode1", "LeafNode1"),
        mock(IDispatcherCallback.class));

    return rootTree;
  }

  private DispatchingTreeNode generateTestSampleTreeWithCallbackAndWildcards(
      IDispatcherCallback callback) {

    DispatchingTreeNode rootTree = new DispatchingTreeNode();
    rootTree.init(null, "RootTree");

    addTestNode(rootTree, Collections.singletonList("Node_L1_1"), null);
    addTestNode(rootTree, Arrays.asList("Node_L1_1", "Node_L2_1"), null);
    addTestNode(rootTree, Arrays.asList("Node_L1_1", "+"), null);
    addTestNode(rootTree, Arrays.asList("Node_L1_1", "Node_L2_1", "Node_L3_1"), callback);
    addTestNode(rootTree, Arrays.asList("Node_L1_1", "+", "Node_L3_2"), callback);

    addTestNode(rootTree, Collections.singletonList("Node_L1_2"), null);
    addTestNode(rootTree, Arrays.asList("Node_L1_2", "Node_L2_1"), callback);
    addTestNode(rootTree, Arrays.asList("Node_L1_2", "#"), callback);

    addTestNode(rootTree, Collections.singletonList("Node_L1_3"), callback);
    addTestNode(rootTree, Arrays.asList("Node_L1_3", "Node_L2_2"), null);
    addTestNode(rootTree, Arrays.asList("Node_L1_3", "Node_L2_2", "Node_L3_3"), callback);

    return rootTree;
  }

  private void addTestNode(IDispatcherInterface rootNode, List<String> names,
                           IDispatcherCallback callback) {

    DispatchingTreeNode nodeToBeAdded = new DispatchingTreeNode();
    rootNode.addDispatcher(names.iterator(), nodeToBeAdded);
    if (callback != null) {
      nodeToBeAdded.addCallback(callback);
    }
  }
}