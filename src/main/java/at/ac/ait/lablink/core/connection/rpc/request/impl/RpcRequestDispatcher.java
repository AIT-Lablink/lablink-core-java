//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.request.impl;

import at.ac.ait.lablink.core.connection.dispatching.IDispatcherInterface;
import at.ac.ait.lablink.core.connection.dispatching.impl.DispatchingTreeNode;
import at.ac.ait.lablink.core.connection.mqtt.impl.MqttUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Dispatcher node for handling RPC requests. This special dispatcher node will combine three
 * different combination for -ANY- and specific identifiers into one node.
 */
public class RpcRequestDispatcher extends DispatchingTreeNode {

  private static final Logger logger = LoggerFactory.getLogger(RpcRequestDispatcher.class);

  private final String clientId;
  private final String groupId;

  public static final String RPC_REQUEST_ANY_ELEMENT = "-ANY-";
  private boolean canBeRemoved = false;

  /**
   * Constructor.
   *
   * @param groupId  Group identifier of the client
   * @param clientId client identifier of the client
   */
  public RpcRequestDispatcher(String groupId, String clientId) {

    this.groupId = groupId;
    this.clientId = clientId;
  }

  @Override
  public void addDispatcher(Iterator<String> dispatcherNameIter, IDispatcherInterface node) {

    synchronized (syncMonitor) {
      if (!dispatcherNameIter.hasNext()) {
        canBeRemoved = false;
        return;
      }

      String directChildName = dispatcherNameIter.next();

      //Ignore an available group ID
      if (directChildName.equals(groupId) || directChildName.equals(RPC_REQUEST_ANY_ELEMENT)) {
        if (!dispatcherNameIter.hasNext()) {
          return;
        }
        directChildName = dispatcherNameIter.next();
      }

      //Ignore an available client ID
      if (directChildName.equals(clientId) || directChildName.equals(RPC_REQUEST_ANY_ELEMENT)) {
        if (!dispatcherNameIter.hasNext()) {
          return;
        }
        directChildName = dispatcherNameIter.next();
      }

      addDispatcherChildName(directChildName, dispatcherNameIter, node);
    }
  }

  @Override
  public void removeDispatcher(Iterator<String> dispatcherNameIter) {

    synchronized (syncMonitor) {
      // Remove only if this dispatcher is chosen explicitly and it contains no registered children.
      if (!dispatcherNameIter.hasNext()) {
        clearCallbackHandlers();
        if (!this.hasDispatcherRegistered()) {
          this.canBeRemoved = true;
        }
        return;
      }

      String directChildName = dispatcherNameIter.next();

      //Ignore an available group ID
      if (directChildName.equals(groupId) || directChildName.equals(RPC_REQUEST_ANY_ELEMENT)) {
        if (!dispatcherNameIter.hasNext()) {
          return;
        }
        directChildName = dispatcherNameIter.next();
      }

      //Ignore an available client ID
      if (directChildName.equals(clientId) || directChildName.equals(RPC_REQUEST_ANY_ELEMENT)) {
        if (!dispatcherNameIter.hasNext()) {
          return;
        }
        directChildName = dispatcherNameIter.next();
      }

      removeDispatcherChildName(directChildName, dispatcherNameIter);
    }
  }

  @Override
  public boolean canBeRemoved() {
    return this.canBeRemoved;
  }

  @Override
  public IDispatcherInterface getDispatcher(Iterator<String> dispatcherNameIter) {
    if (!dispatcherNameIter.hasNext()) {
      return null;
    }

    String searchName = dispatcherNameIter.next();

    //Ignore an available group ID
    if (searchName.equals(groupId) || searchName.equals(RPC_REQUEST_ANY_ELEMENT)) {
      if (!dispatcherNameIter.hasNext()) {
        return null;
      }
      searchName = dispatcherNameIter.next();
    }

    //Ignore an available client ID
    if (searchName.equals(clientId) || searchName.equals(RPC_REQUEST_ANY_ELEMENT)) {
      if (!dispatcherNameIter.hasNext()) {
        return null;
      }
      searchName = dispatcherNameIter.next();
    }

    return getDispatcherInterfaceChildName(searchName, dispatcherNameIter);
  }

  @Override
  public void execute(List<String> names, int listPosition, byte[] mqttPayload) {

    if (listPosition < names.size() - 1) {

      String childName = names.get(listPosition);
      if (!(childName.equals(this.groupId) || childName.equals(RPC_REQUEST_ANY_ELEMENT))) {
        logger.debug("False group identifier received during RPC request handling.");
        return;
      }
      listPosition++;

      childName = names.get(listPosition);
      if (!(childName.equals(this.clientId) || childName.equals(RPC_REQUEST_ANY_ELEMENT))) {
        logger.debug("False client identifier received during RPC request handling.");
        return;
      }
      listPosition++;
    }

    super.execute(names, listPosition, mqttPayload);
  }

  @Override
  public List<List<String>> getAllSubscriptions() {

    /* add req, groupId, clientId */
    List<String> anySubscription = super.getFullName();
    anySubscription.add(this.groupId);
    anySubscription.add(this.clientId);
    anySubscription.add(MqttUtils.TOPIC_WILDCARD_ALL);

    List<List<String>> subscriptions = new ArrayList<List<String>>();
    subscriptions.add(anySubscription);

    /* add req, groupId, -any- */
    anySubscription = super.getFullName();
    anySubscription.add(this.groupId);
    anySubscription.add(RPC_REQUEST_ANY_ELEMENT);
    anySubscription.add(MqttUtils.TOPIC_WILDCARD_ALL);
    subscriptions.add(anySubscription);

    /* add req, -any-, -any- */
    anySubscription = super.getFullName();
    anySubscription.add(RPC_REQUEST_ANY_ELEMENT);
    anySubscription.add(RPC_REQUEST_ANY_ELEMENT);
    anySubscription.add(MqttUtils.TOPIC_WILDCARD_ALL);
    subscriptions.add(anySubscription);

    return subscriptions;
  }

  @Override
  public List<List<String>> getSubscriptions(Iterator<String> dispatcherNameIter) {
    /* stop iteration and add common subscription*/
    if (!dispatcherNameIter.hasNext()) {
      return this.getAllSubscriptions();
    } else {
      return new ArrayList<List<String>>();
    }
  }

  @Override
  public List<String> getFullName() {
    List<String> fullName = new ArrayList<String>();

    fullName.addAll(super.getFullName());
    fullName.add(this.groupId);
    fullName.add(this.clientId);
    return fullName;
  }
}
