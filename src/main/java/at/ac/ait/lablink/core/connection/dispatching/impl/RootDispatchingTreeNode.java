//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.dispatching.impl;

import at.ac.ait.lablink.core.connection.dispatching.IDispatcherInterface;
import at.ac.ait.lablink.core.connection.dispatching.IRootDispatcher;
import at.ac.ait.lablink.core.connection.ex.LowLevelCommRuntimeException;
import at.ac.ait.lablink.core.connection.mqtt.IMqttSubscriber;
import at.ac.ait.lablink.core.connection.mqtt.impl.MqttUtils;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

/**
 * The {@link RootDispatchingTreeNode} is a {@link DispatchingTreeNode} without a parent reference
 * and with subscription and handle methods towards the low level communication.
 *
 * <p>It will override some methods for easier access to the dispatching tree. It will use a list
 * of topic elements instead of iterators and checks the first element and remove it to handle the
 * tree walk correctly.
 */
public class RootDispatchingTreeNode extends DispatchingTreeNode implements IRootDispatcher {

  private static final Logger logger = LoggerFactory.getLogger(RootDispatchingTreeNode.class);

  /* Subscriber that provides methods to subscribe and unsubscribe topic to/from the broker. */
  private IMqttSubscriber mqttSubscriber;

  /**
   * Default constructor for the class
   *
   * <p>The initialisation (call {@link #init(IDispatcherInterface, String)}) mustn't be called.
   *
   * @param name Name element of the will be set during the construction.
   */
  public RootDispatchingTreeNode(String name) {
    init(null, name); /* Call init method from base class */
  }

  @Override
  public void addDispatcher(List<String> subscription, IDispatcherInterface node) {
    MqttUtils.validateMqttSubscription(subscription);

    Iterator<String> dispatcherNameIter = subscription.iterator();
    checkEmptyIterator(dispatcherNameIter);
    String rootName = dispatcherNameIter.next();
    checkRootElementName(rootName);
    MqttUtils.validateSubscriptionTopicElement(rootName);
    super.addDispatcher(dispatcherNameIter, node);

    this.subscribeDispatcherOnBroker(subscription.iterator());
  }

  /**
   * Subscribe a topic at the Mqtt broker.
   *
   * @param dispatcherName Topic Elements to be subscribed
   */
  private void subscribeDispatcherOnBroker(Iterator<String> dispatcherName) {
    List<List<String>> subscriptions = this.getSubscriptions(dispatcherName);

    if (!subscriptions.isEmpty()) {
      try {
        mqttSubscriber.subscribe(MqttUtils.convertStringSubscriptionsToMqttTopics(subscriptions));
      } catch (LowLevelCommRuntimeException ex) {
        logger.debug("Can't subscribe topics: {}", ex.getMessage());
      }
    }
  }

  @Override
  public void removeDispatcher(List<String> subscription) {
    MqttUtils.validateMqttSubscription(subscription);
    this.unsubscribeDispatcherFromBroker(subscription.iterator());

    Iterator<String> dispatcherNameIter = subscription.iterator();
    checkEmptyIterator(dispatcherNameIter);
    String rootName = dispatcherNameIter.next();
    checkRootElementName(rootName);
    MqttUtils.validateSubscriptionTopicElement(rootName);
    super.removeDispatcher(dispatcherNameIter);
  }

  /**
   * Helper method to unsubscribe a topic from the broker.
   *
   * @param dispatcherName List of topic elements for the subscription
   */
  private void unsubscribeDispatcherFromBroker(Iterator<String> dispatcherName) {
    List<List<String>> subscriptions = this.getSubscriptions(dispatcherName);

    if (!subscriptions.isEmpty()) {
      try {
        mqttSubscriber.unsubscribe(MqttUtils.convertStringSubscriptionsToMqttTopics(subscriptions));
      } catch (LowLevelCommRuntimeException ex) {
        logger.debug("Can't unsubscribe topics: {}", ex.getMessage());
      }
    }
  }

  @Override
  public IDispatcherInterface getDispatcher(List<String> dispatcherName) {
    Iterator<String> dispatcherNameIter = dispatcherName.iterator();

    checkEmptyIterator(dispatcherNameIter);
    String rootName = dispatcherNameIter.next();
    checkRootElementName(rootName);
    return super.getDispatcher(dispatcherNameIter);
  }

  @Override
  public void execute(List<String> names, int listPosition, byte[] mqttPayload) {

    if (names.size() < 1) {
      throw new LlCoreRuntimeException("List has no elements.");
    }

    String rootName = names.get(listPosition);
    listPosition++;
    checkRootElementName(rootName);
    super.execute(names, listPosition, mqttPayload);
  }

  @Override
  public List<List<String>> getSubscriptions(Iterator<String> dispatcherNameIter) {
    checkEmptyIterator(dispatcherNameIter);
    String rootName = dispatcherNameIter.next();
    checkRootElementName(rootName);
    return super.getSubscriptions(dispatcherNameIter);
  }

  @Override
  public boolean hasDispatcher(List<String> dispatcherName) {
    return this.getDispatcher(dispatcherName) != null;
  }

  @Override
  public void setMqttSubscriber(IMqttSubscriber mqttSubscriber) {
    this.mqttSubscriber = mqttSubscriber;
  }

  @Override
  public void handleRawMqttMessage(String topic, byte[] mqttPayload) {
    List<String> topicList = MqttUtils.convertMqttTopicToStringList(topic);
    this.execute(topicList, 0, mqttPayload);
  }

  @Override
  public void onEstablishedMqttConnection() {
    List<List<String>> subscriptions = this.getAllSubscriptions();

    List<String> topics = MqttUtils.convertStringSubscriptionsToMqttTopics(subscriptions);

    if (!topics.isEmpty()) {
      try {
        mqttSubscriber.subscribe(topics);
      } catch (LowLevelCommRuntimeException ex) {
        logger.info("Can't subscribe topics. No connection to IMqttSubscriber");
      }
    }
  }

  @Override
  public void onLostMqttConnection() {
    /* do nothing */
  }

  @Override
  public void onDisconnectingMqttConnection() {
    List<List<String>> subscriptions = this.getAllSubscriptions();

    List<String> topics = MqttUtils.convertStringSubscriptionsToMqttTopics(subscriptions);

    if (!topics.isEmpty()) {
      try {
        mqttSubscriber.unsubscribe(topics);
      } catch (LowLevelCommRuntimeException ex) {
        logger.info("Can't subscribe topics. No connection to IMqttSubscriber");
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>The root dispatcher node shouldn't be removed at any time.
   *
   * @return always false
   */
  @Override
  public boolean canBeRemoved() {
    return false;
  }

  /**
   * Check if the iterator is empty and has no next element.
   *
   * <p>If the iterator has no next element it will throw a runtime exception
   *
   * @param dispatcherNameIter iterator to be tested
   * @throws LlCoreRuntimeException if the iterator has no elements.
   */
  private void checkEmptyIterator(Iterator<String> dispatcherNameIter) {
    if (!dispatcherNameIter.hasNext()) {
      throw new LlCoreRuntimeException("Iterator has no elements.");
    }
  }

  /**
   * Check if the given name matches with the name of the class.
   *
   * @param rootName to be checked.
   * @throws LlCoreRuntimeException if the name doesn't match
   */
  private void checkRootElementName(String rootName) {
    if (!rootName.equals(name)) {
      throw new LlCoreRuntimeException(
          "Incorrect topic namespace. This root is '" + name + "'. Your topic root is '" + rootName
              + "'.");
    }
  }
}
