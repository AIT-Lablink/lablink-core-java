//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.dispatching.impl;

import at.ac.ait.lablink.core.connection.dispatching.IDispatcherCallback;
import at.ac.ait.lablink.core.connection.dispatching.IDispatcherInterface;
import at.ac.ait.lablink.core.connection.mqtt.impl.MqttUtils;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A dispatcher for incoming messages.
 *
 * <p>The dispatcher is build of tree nodes. Each tree node can contain child nodes. The node also
 * stores callback methods. If an incoming message fits to this dispatcher the callback methods
 * will be executed.
 *
 * <p>Each node in the dispatcher tree represents an element of an incoming MQTT topic. The topic
 * will be split into single string elements. Each element matches with one registered dispatcher.
 */
public class DispatchingTreeNode implements IDispatcherInterface {

  private static final Logger logger = LoggerFactory.getLogger(DispatchingTreeNode.class);

  /* Parent node where the this node is registered in the tree */
  private IDispatcherInterface parent = null;

  /* Children nodes that are registered in this node */
  private final Map<String, IDispatcherInterface>
      children =
      new ConcurrentHashMap<String, IDispatcherInterface>();

  /* Name element of the node (part of the mqtt topic subscription */ String name;

  /* Registered callback handlers at the node */
  private final List<IDispatcherCallback>
      callbackHandlers =
      Collections.synchronizedList(new ArrayList<IDispatcherCallback>());

  protected final Object syncMonitor = new Object();

  @Override
  public void init(IDispatcherInterface parent, String name) {
    this.parent = parent;
    this.name = name;
  }

  @Override
  public void addDispatcher(Iterator<String> dispatcherNameIter, IDispatcherInterface node) {

    if (!dispatcherNameIter.hasNext()) {
      return;
    }
    String directChildName = dispatcherNameIter.next();
    addDispatcherChildName(directChildName, dispatcherNameIter, node);
  }

  /**
   * Helper method for add dispatcher.
   *
   * <p>This method splits the name pick up from the iterator and the search for children.
   *
   * @param directChildName direct child name
   * @param dispatcherNameIter iterator over dispatcher names
   * @param node dispatcher interface node
   */
  protected void addDispatcherChildName(String directChildName, Iterator<String> dispatcherNameIter,
                                        IDispatcherInterface node) {
    IDispatcherInterface childNode;
    MqttUtils.validateSubscriptionTopicElement(directChildName);

    synchronized (syncMonitor) {
      if (!dispatcherNameIter.hasNext()) {

        if (children.containsKey(directChildName)) {
          List<String> fullName = this.getFullName();
          fullName.add(directChildName);
          throw new LlCoreRuntimeException(
              "IDispatcherInterface " + node.toString() + " can't be added. " 
                  + "Element with name '" + fullName + "' already registered."
          );
        }
        children.put(directChildName, node);
        node.init(this, directChildName);

      } else {

        childNode = children.get(directChildName);

        if (childNode == null) {
          childNode = generateChild(directChildName);
        }

        childNode.addDispatcher(dispatcherNameIter, node);
      }
    }
  }

  /**
   * Generate a new child node and register it at the node.
   *
   * @param name Single topic element to be registered as new child
   * @return the generated and registered child
   */
  private DispatchingTreeNode generateChild(String name) {

    DispatchingTreeNode child = new DispatchingTreeNode();
    child.init(this, name);
    this.children.put(name, child);
    return child;
  }

  @Override
  public void removeDispatcher(Iterator<String> dispatcherNameIter) {

    synchronized (syncMonitor) {
      if (!dispatcherNameIter.hasNext()) {
        clearCallbackHandlers();
        return;
      }

      String childName = dispatcherNameIter.next();
      removeDispatcherChildName(childName, dispatcherNameIter);
    }
  }

  /**
   * Helper method for remove Dispatcher.
   *
   * <p>This method splits the name pick up from the iterator and the search for children.
   *
   * @param childName child name
   * @param dispatcherNameIter iterator of dispatcher names
   */
  protected void removeDispatcherChildName(String childName, Iterator<String> dispatcherNameIter) {

    IDispatcherInterface childDispatcher = children.get(childName);

    if (childDispatcher == null) {
      return;
    }

    childDispatcher.removeDispatcher(dispatcherNameIter);

    if (childDispatcher.canBeRemoved()) {
      children.remove(childName);
    }
  }

  protected void clearCallbackHandlers() {

    if (this.hasDispatcherRegistered()) {
      logger.info("IDispatcherInterface {} can't be removed. Element hold other child dispatchers.",
          this.toString());
    }

    this.callbackHandlers.clear();
  }

  /**
   * Check if the node has children dispatchers registered.
   *
   * @return True if there are registered children nodes, otherwise false.
   */
  protected boolean hasDispatcherRegistered() {
    return children.size() > 0;
  }

  @Override
  public IDispatcherInterface getDispatcher(Iterator<String> dispatcherNameIter) {

    if (!dispatcherNameIter.hasNext()) {
      return null;
    }

    String searchName = dispatcherNameIter.next();
    return getDispatcherInterfaceChildName(searchName, dispatcherNameIter);
  }

  /**
   * Helper Method.
   *
   * @param searchName         Name of the child
   * @param dispatcherNameIter dispatcher iterator the get the last element
   * @return the found dispatcher interface or null
   */
  protected IDispatcherInterface getDispatcherInterfaceChildName(String searchName,
                                                                Iterator<String>
                                                                    dispatcherNameIter) {

    IDispatcherInterface foundOne = children.get(searchName);

    if (foundOne == null) {
      return null;
    }

    if (dispatcherNameIter.hasNext()) {
      return foundOne.getDispatcher(dispatcherNameIter);
    } else {
      return foundOne;
    }
  }

  @Override
  public void execute(List<String> names, int listPosition, byte[] mqttPayload) {

    if (this.name.equals(MqttUtils.TOPIC_WILDCARD_ALL)) {
      executeCallbacks(mqttPayload);
      return;
    }

    final int names_size = names.size();

    if (listPosition <= names_size) {
      IDispatcherInterface dispatcherChild = children.get(MqttUtils.TOPIC_WILDCARD_ALL);
      if (dispatcherChild != null) {
        dispatcherChild.execute(names, listPosition, mqttPayload);
      }
    }

    if (listPosition < names_size) {
      String childName = names.get(listPosition);
      listPosition++;

      IDispatcherInterface dispatcherChild = children.get(childName);
      if (dispatcherChild != null) {
        dispatcherChild.execute(names, listPosition, mqttPayload);
      }

      dispatcherChild = children.get(MqttUtils.TOPIC_WILDCARD_ANY);
      if (dispatcherChild != null) {
        dispatcherChild.execute(names, listPosition, mqttPayload);
      }

    } else {  /* current node is a leaf */
      executeCallbacks(mqttPayload);
    }
  }

  /**
   * Execute all registered callbacks of this node.
   *
   * @param mqttPayload for handling callbacks
   */
  private void executeCallbacks(byte[] mqttPayload) {

    synchronized (callbackHandlers) {
      for (IDispatcherCallback callback : callbackHandlers) {
        callback.handleMessage(mqttPayload);
      }
    }
  }

  @Override
  public List<List<String>> getAllSubscriptions() {
    List<List<String>> subscriptions = new ArrayList<List<String>>();

    subscriptions.addAll(this.getSubscriptions());

    for (IDispatcherInterface child : children.values()) {
      subscriptions.addAll(child.getAllSubscriptions());
    }

    return subscriptions;
  }

  @Override
  public List<List<String>> getSubscriptions(Iterator<String> dispatcherNameIter) {

    List<List<String>> subscriptions;

    if (dispatcherNameIter.hasNext()) {
      String childName = dispatcherNameIter.next();

      IDispatcherInterface dispatcherChild = children.get(childName);

      if (dispatcherChild == null) {
        throw new LlCoreRuntimeException("Child with '" + childName + "' not available");
      }
      subscriptions = dispatcherChild.getSubscriptions(dispatcherNameIter);

    } else {  /* node is leaf */
      subscriptions = this.getSubscriptions();
    }

    if (subscriptions == null) {
      subscriptions = new ArrayList<List<String>>();
    }

    return subscriptions;
  }

  /**
   * Get all subscriptions of this node. Usually the full name of the node
   *
   * @return a list of subscriptions for this node.
   */
  private List<List<String>> getSubscriptions() {
    List<List<String>> subscriptions = new ArrayList<List<String>>();

    if (this.hasCallbacksRegistered()) {
      subscriptions.add(this.getFullName());
    }

    return subscriptions;
  }

  /**
   * Check if this node has any registered callback.
   *
   * @return True if minimum one callback is registered.
   */
  boolean hasCallbacksRegistered() {
    return callbackHandlers.size() > 0;
  }

  @Override
  public List<String> getFullName() {
    List<String> fullName = new ArrayList<String>();

    if (!isTreeRoot()) {
      fullName.addAll(parent.getFullName());
    }
    fullName.add(this.name);

    return fullName;
  }

  @Override
  public boolean canBeRemoved() {
    return !hasDispatcherRegistered() && !hasCallbacksRegistered();
  }

  @Override
  public void addCallback(IDispatcherCallback callback) {

    if (callback == null || this.callbackHandlers.contains(callback)) {
      return;
    }

    callbackHandlers.add(callback);
  }

  @Override
  public void removeCallback(IDispatcherCallback callback) {
    callbackHandlers.remove(callback);
  }

  /**
   * Check if this tree node is the root node.
   *
   * @return true if the node is the root node.
   */
  boolean isTreeRoot() {
    return parent == null;
  }

  /**
   * Get the parent of the dispatcher node. (For testing purposes)
   *
   * @return the parent dispatcher
   */
  IDispatcherInterface getParent() {
    return parent;
  }

  /**
   * Return all registered children dispatcher. (For testing purposes)
   *
   * @return all registered dispatchers at the node.
   */
  Map<String, IDispatcherInterface> getChildren() {
    return children;
  }

  /**
   * Get the registered name of the node. (For testing purposes).
   *
   * @return name of the tree node.
   */
  String getName() {
    return name;
  }

  /**
   * Return a list of all registered callback handlers. (For testing purposes)
   *
   * @return all registered callback handlers
   */
  public List<IDispatcherCallback> getCallbackHandlers() {
    return callbackHandlers;
  }

  @Override
  public void printTree(String indent, boolean last) {
    System.out.print(indent);
    if (last) {
      System.out.print("|--");
      indent += "  ";
    } else {
      System.out.print("|--");
      indent += "| ";
    }
    System.out.println(
        " '" + this.name + "' (" + callbackHandlers.size() + " callbacks) " + this.getClass()
            .getSimpleName());

    List<IDispatcherInterface> childrenList = 
        new ArrayList<IDispatcherInterface>(children.values());

    for (int i = 0; i < childrenList.size(); i++) {
      childrenList.get(i).printTree(indent, i == childrenList.size() - 1);
    }
  }
}
