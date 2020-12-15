//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.reply.impl;

import at.ac.ait.lablink.core.connection.dispatching.IDispatcherInterface;
import at.ac.ait.lablink.core.connection.dispatching.impl.DispatchingTreeNode;
import at.ac.ait.lablink.core.connection.mqtt.impl.MqttUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Special dispatcher for handling RPC replies.
 *
 * <p>The Reply Dispatcher combines three tree nodes into one. It will also check the group and
 * the client identifier in a single node.
 */
public class RpcReplyDispatcher extends DispatchingTreeNode {

  private static final Logger logger = LoggerFactory.getLogger(RpcReplyDispatcher.class);

  private final String clientId;
  private final String groupId;

  private boolean canBeRemoved = false;

  /**
   * Constructor.
   *
   * @param groupId  Group identifier of the destination client.
   * @param clientId Client identifier of the destination client.
   */
  RpcReplyDispatcher(String groupId, String clientId) {

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
      if (directChildName.equals(groupId)) {
        if (!dispatcherNameIter.hasNext()) {
          return;
        }
        directChildName = dispatcherNameIter.next();
      }

      //Ignore an available client ID
      if (directChildName.equals(clientId)) {
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
      if (!dispatcherNameIter.hasNext()) {
        clearCallbackHandlers();
        if (!this.hasDispatcherRegistered()) {
          this.canBeRemoved = true;
        }
        return;
      }

      String directChildName = dispatcherNameIter.next();

      //Ignore an available group ID
      if (directChildName.equals(groupId)) {
        if (!dispatcherNameIter.hasNext()) {
          return;
        }
        directChildName = dispatcherNameIter.next();
      }

      //Ignore an available client ID
      if (directChildName.equals(clientId)) {
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
    if (searchName.equals(groupId)) {
      if (!dispatcherNameIter.hasNext()) {
        return null;
      }
      searchName = dispatcherNameIter.next();
    }

    //Ignore an available client ID
    if (searchName.equals(clientId)) {
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
      if (!(childName.equals(this.groupId))) {
        logger.debug("False group identifier received during RPC reply handling.");
        return;
      }
      listPosition++;

      childName = names.get(listPosition);
      if (!(childName.equals(this.clientId))) {
        logger.debug("False client identifier received during RPC reply handling.");
        return;
      }
      listPosition++;
    }

    super.execute(names, listPosition, mqttPayload);
  }

  @Override
  public List<List<String>> getAllSubscriptions() {
    List<List<String>> subscriptions = new ArrayList<List<String>>();

    List<String> subscription = new ArrayList<String>();
    subscription.addAll(this.getFullName());
    subscription.add(MqttUtils.TOPIC_WILDCARD_ALL);

    subscriptions.add(subscription);
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
