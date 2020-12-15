//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.dispatching;

import java.util.Iterator;
import java.util.List;

/**
 * Interface for a dispatcher unit.
 *
 * <p>This interface is used for dispatching incoming messages and must be implemented to register
 * a dispatcher to the IRootDispatcher.
 */
public interface IDispatcherInterface {

  /**
   * Initializes the dispatching node. Potential tasks are: - initializing a set of - creating a
   * list of IDispatcherInterface child elements if applicable - saving a reference to the parent
   * node
   *
   * @param parent the parent {@link IDispatcherInterface} implementing object
   * @param name   the name of the Dispatcher node
   */
  void init(IDispatcherInterface parent, String name);

  /**
   * Adds a dispatcher to the dispatching tree. This involves creation of the dispatching tree by
   * optional creation and linking of new dispatching tree nodes in case they do not exist. Also the
   * element might be initialized by it's init() method.
   *
   * @param dispatcherNameIter Name iterator of the dispatcher element to be added
   * @param node               The dispatcher element to be added
   */
  void addDispatcher(Iterator<String> dispatcherNameIter, IDispatcherInterface node);

  /**
   * Removes the given dispatcher from the dispatching tree.
   *
   * @param dispatcherNameIter Name iterator of the dispatcher to remove
   */
  void removeDispatcher(Iterator<String> dispatcherNameIter);

  /**
   * Get an already registered dispatcher.
   *
   * <p>If no dispatcher with the given name is registered in the IRootDispatcher the method will
   * return null.
   *
   * @param dispatcherNameIter Iterator with topic elements that points to the requested dispatcher.
   * @return The method will return a registered dispatcher, if it exists.
   */
  IDispatcherInterface getDispatcher(Iterator<String> dispatcherNameIter);

  /**
   * Executes a newly incoming mqttPayload.
   *
   * <p>The payloads is either forwarded to the specific child's exception method or directly
   * executed in case the message is destined for this concrete dispatcher. The latter is the
   * case when the name Iterator has only one remaining element which is equal to this
   * dispatcher's name.
   *
   * @param names        Name Iterator of destined dispatcher
   * @param listPosition Position of the iterator in the list.
   * @param mqttPayload  IPayload to be executed by the specific dispatcher
   */
  void execute(List<String> names, int listPosition, byte[] mqttPayload);

  /**
   * Get a collection of all subscriptions registered to the dispatcher.
   *
   * <p>If other dispatcher are registered to the dispatcher the method should also return these
   * subscriptions. E.g., if the dispatcher is a node in a dispatching tree this method should
   * return a collection of all subscriptions (from all registered child nodes) of the subtree.
   *
   * @return A list of Mqtt subscription topics. The elements of a topic are also stored in a list.
   */
  List<List<String>> getAllSubscriptions();

  /**
   * Get the subscriptions of a specific dispatcher.
   *
   * <p>The method should be used to get the subscription of a registered dispatcher. The
   * dispatcher can register more than one subscription for its element.
   *
   * @param dispatcherNameIter Iterator with topic elements that points to the requested dispatcher.
   * @return A list of Mqtt subscription topics. The elements of a topic are also stored in a list.
   */
  List<List<String>> getSubscriptions(Iterator<String> dispatcherNameIter);

  /**
   * Check if the dispatcher can be removed from the root dispatcher.
   *
   * <p>A dispatcher should only be registered if it has callback methods registered. Therefore the
   * dispatcher will be used to dispatch incoming messages. If a dispatcher tree is used and the
   * last element of a path will be removed all nodes before this removed one can also be removed
   * to minimize the tree.
   *
   * @return True if the dispatcher node can be removed, otherwise False
   */
  boolean canBeRemoved();

  /**
   * Returns dispatcher's full name, which typically represents all fields of the whole topic.
   *
   * @return dispatcher's name as a list of topic elements.
   */
  List<String> getFullName();

  /**
   * Add a dispatcher callback to the dispatcher.
   *
   * @param callback The callback object to be added to the dispatcher.
   */
  void addCallback(IDispatcherCallback callback);

  /**
   * Remove a registered callback from the dispatcher.
   *
   * @param callback The callback object to be removed from the dispatcher.
   */
  void removeCallback(IDispatcherCallback callback);

  /**
   * Print the dispatcher element in a tree structure on the console
   *
   * @param indent Print an indention in front of the tree
   * @param last   Set if the element to be printed is the last child of a tree node.
   */
  void printTree(String indent, boolean last);
}
