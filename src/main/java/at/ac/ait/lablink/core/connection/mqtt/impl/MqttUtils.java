//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.mqtt.impl;

import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper methods for the Mqtt topic and subscription handling.
 *
 * <p>This class provides static methods that can be used for handling the conversion and validation
 * of topics and subscription strings.
 */
public class MqttUtils {

  /**
   * Separator between two MQTT topic elements.
   */
  public static final String TOPIC_SEPARATOR_SYMBOL = "/";

  /**
   * Wildcard character for a single element in a MQTT subscription.
   */
  public static final String TOPIC_WILDCARD_ANY = "+";

  /**
   * Wildcard character for a unspecific number of elements on the right side of a string.
   */
  public static final String TOPIC_WILDCARD_ALL = "#";

  /**
   * Convert a list of Mqtt subscription elements into a single string.
   *
   * @param subscription List of subscription elements to be merged into one string.
   * @return the Mqtt subscription build from elements.
   */
  public static String convertStringSubscriptionToMqtt(List<String> subscription) {
    StringBuilder mqttTopic = new StringBuilder();

    if (subscription == null || subscription.isEmpty()) {
      throw new LlCoreRuntimeException("Mqtt subscription has no elements.");
    }

    for (int i = 0; i < subscription.size() - 1; i++) {
      mqttTopic.append(subscription.get(i)).append(MqttUtils.TOPIC_SEPARATOR_SYMBOL);
    }
    mqttTopic.append(subscription.get(subscription.size() - 1));

    return mqttTopic.toString();
  }

  /**
   * Converts a list of Mqtt subscriptions contains subscription elements into a list of Mqtt
   * subscriptions as strings
   *
   * @param subscriptions List of subscriptions. The subscription are also a list of elements.
   * @return a list of Mqtt subscription strings.
   */
  public static List<String> convertStringSubscriptionsToMqttTopics(
      List<List<String>> subscriptions) {

    List<String> mqttTopics = new ArrayList<String>();

    for (List<String> subscription : subscriptions) {
      String mqttTopic = MqttUtils.convertStringSubscriptionToMqtt(subscription);
      mqttTopics.add(mqttTopic);
    }

    return mqttTopics;
  }

  /**
   * Convert a list of Mqtt topic elements into a single string.
   *
   * @param listTopic List of topic elements to be merged into a string.
   * @return Mqtt topic build from elements.
   */
  public static String convertStringListTopicToMqtt(List<String> listTopic) {

    if (listTopic == null || listTopic.isEmpty()) {
      throw new LlCoreRuntimeException("Mqtt topic has no elements.");
    }

    StringBuilder mqttTopic = new StringBuilder();

    for (int i = 0; i < listTopic.size() - 1; i++) {
      mqttTopic.append(listTopic.get(i)).append(MqttUtils.TOPIC_SEPARATOR_SYMBOL);
    }
    mqttTopic.append(listTopic.get(listTopic.size() - 1));

    return mqttTopic.toString();
  }

  /**
   * Convert a Mqtt topic into a list of topic elements.
   *
   * @param mqttTopic string to be converted.
   * @return List of strings with the split elements of the MQTT topic.
   */
  public static List<String> convertMqttTopicToStringList(String mqttTopic) {

    String[] splitTopic = mqttTopic.split(MqttUtils.TOPIC_SEPARATOR_SYMBOL);
    return Arrays.asList(splitTopic);
  }

  /**
   * Validate a full MQTT subscription for disallowed characters or elements.
   *
   * @param elements List of Strings representing Mqtt topics subscription elements
   * @throws LlCoreRuntimeException if the validation fails
   */
  public static void validateMqttSubscription(List<String> elements) {

    if (elements.size() < 1) {
      throw new LlCoreRuntimeException("No elements in topic string");
    }

    validateSubscriptionTopicElement(elements.get(0));

    for (int i = 0; i < elements.size() - 1; i++) {
      validateSubscriptionTopicElement(elements.get(i));

      if (elements.get(i).equals("#")) {
        throw new LlCoreRuntimeException("Not the last topic element is #");
      }
    }
  }

  /**
   * Validate a full MQTT subscription for disallowed characters or elements.
   *
   * @param subscription String with the subscription topic to be validated.
   * @throws LlCoreRuntimeException if the validation fails
   */
  public static void validateMqttSubscription(String subscription) {

    List<String> elements = Arrays.asList(subscription.split(TOPIC_SEPARATOR_SYMBOL));

    validateMqttSubscription(elements);
  }

  /**
   * Validate a full MQTT subscription for disallowed characters or elements.
   *
   * @param elements List of split topic elements
   * @throws LlCoreRuntimeException if the validation fails.
   */
  public static void validateMqttTopic(List<String> elements) {

    if (elements == null) {
      throw new LlCoreRuntimeException("Topic elements list isn't set and null.");
    }

    if (elements.size() < 1) {
      throw new LlCoreRuntimeException("No elements in topic string");
    }

    validateFirstTopicElement(elements.get(0));

    for (int i = 0; i < elements.size() - 1; i++) {
      validateTopicElement(elements.get(i));

      if (elements.get(i).equals("#")) {
        throw new LlCoreRuntimeException("Not the last topic element is #");
      }
    }

    validateTopicElement(elements.get(elements.size() - 1));
  }

  /**
   * Validate a full MQTT subscription for disallowed characters or elements.
   *
   * @param topic whole Mqtt topic as string.
   * @throws LlCoreRuntimeException if the validation fails.
   */
  public static void validateMqttTopic(String topic) {

    List<String> elements = Arrays.asList(topic.split(TOPIC_SEPARATOR_SYMBOL));

    validateMqttTopic(elements);
  }


  /**
   * Validate the first element of a MQTT topic string.
   *
   * @param rootName String to be validated.
   * @throws LlCoreRuntimeException if the validation fails.
   */
  public static void validateFirstTopicElement(String rootName) {

    if (rootName.startsWith("$")) {
      throw new LlCoreRuntimeException(
          "Validation exception, root topic element starts with disallowed $ character.");
    }

    validateTopicElement(rootName);
  }

  /**
   * Validate a single element of a MQTT subscription.
   *
   * @param elementName String to be validated.
   * @throws LlCoreRuntimeException if the validation fails.
   */
  public static void validateSubscriptionTopicElement(String elementName) {

    if (elementName.equals("+") || elementName.equals("#")) {
      return;
    }

    validateTopicElement(elementName);
  }

  /**
   * Validate a single element of a MQTT topic.
   *
   * @param elementName Element string to be validated.
   * @throws LlCoreRuntimeException if the validation fails
   */
  public static void validateTopicElement(String elementName) {

    if (elementName.contains(" ") || elementName.contains("\n") || elementName.contains("\r")
        || elementName.contains("\t") || elementName.contains("\f")) {
      throw new LlCoreRuntimeException("Validation exception, topic element '" + elementName
          + "' contains disallowed space or whitespace character");
    }

    if (elementName.equals("+") || elementName.equals("#")) {
      throw new LlCoreRuntimeException(
          "Validation exception, topic element '" + elementName + "' contains + or # element");
    }

    if (elementName.length() < 1) {
      throw new LlCoreRuntimeException("Validation exception, topic element '" + elementName
          + "' must contain at least one character");
    }
  }
}
