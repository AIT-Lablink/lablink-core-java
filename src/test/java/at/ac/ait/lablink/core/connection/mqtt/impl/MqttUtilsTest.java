//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.mqtt.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for class MqttUtils.
 */

@SuppressWarnings("unchecked")
@RunWith(JUnitParamsRunner.class)
public class MqttUtilsTest {

  @Test
  public void convertStringSubscriptionsToMqttTopic_ListOfList_test() {
    List<List<String>> stringElements = new ArrayList<List<String>>();
    List<String> subscriptionTopics = new ArrayList<String>();

    for (Object obj : mqttSubscriptionConvert()) {
      Object[] object = (Object[]) obj;
      stringElements.add((List<String>) object[1]);
      subscriptionTopics.add((String) object[0]);
    }

    List<String> converted = MqttUtils.convertStringSubscriptionsToMqttTopics(stringElements);
    assertEquals(subscriptionTopics, converted);
  }

  @Test
  @Parameters(method = "mqttSubscriptionConvert")
  @TestCaseName("MqttSubscriptionConversion('{0}')")
  public void convertStringSubscriptionToMqtt_test(String mqttTopic, List<String> listTopic) {
    String topic = MqttUtils.convertStringSubscriptionToMqtt(listTopic);
    assertEquals(mqttTopic, topic);
  }

  @Test
  @Parameters(method = "mqttPublishTopicConvert")
  @TestCaseName("MqttTopicConversion('{0}')")
  public void convertStringListTopicToMqtt_test(String mqttTopic, List<String> listTopic) {
    String topic = MqttUtils.convertStringListTopicToMqtt(listTopic);
    assertEquals(mqttTopic, topic);
  }

  @Test
  @Parameters(method = "mqttPublishTopics")
  @TestCaseName("MqttTopicConversion('{0}')")
  public void convertMqttTopicToStringList_test(String mqttTopic, List<String> expTopic) {
    List<String> topic = MqttUtils.convertMqttTopicToStringList(mqttTopic);
    assertEquals(expTopic, topic);
  }

  @Test
  @Parameters(method = "mqttSubscriptions")
  @TestCaseName("MqttSubscription('{0}') validation throws {1}")
  public void validateMqttSubscription_InputString_test(String subscription,
                                            Class<? extends Exception> expectedException) {
    if (expectedException != null) {
      assertThrows(expectedException, () -> MqttUtils.validateMqttSubscription(subscription));
    } else {
      MqttUtils.validateMqttSubscription(subscription);
    }
  }

  @Test
  @Parameters(method = "mqttSubscriptions")
  @TestCaseName("MqttTopic('{0}') validation throws {1}")
  public void validateTopicSubscription_InputList_test(String topic,
                                             Class<? extends Exception> expectedException) {
    List<String> splitTopic = Arrays.asList(topic.split("/"));

    if (expectedException != null) {
      assertThrows(expectedException, () -> MqttUtils.validateMqttSubscription(splitTopic));
    } else {
      MqttUtils.validateMqttSubscription(splitTopic);
    }
  }

  @Test
  @Parameters(method = "mqttTopics")
  @TestCaseName("MqttTopic('{0}') validation throws {1}")
  public void validateMqttTopic_InputString_test(
      String topic, 
      Class<? extends Exception> expectedException
  ) {
    if (expectedException != null) {
      assertThrows(expectedException, () -> MqttUtils.validateMqttTopic(topic));
    } else {
      MqttUtils.validateMqttTopic(topic);
    }
  }

  @Test
  @Parameters(method = "mqttTopics")
  @TestCaseName("MqttTopic('{0}') validation throws {1}")
  public void validateMqttTopic_InputList_test(String topic,
                                        Class<? extends Exception> expectedException) {
    List<String> splitTopic = Arrays.asList(topic.split("/"));

    if (expectedException != null) {
      assertThrows(expectedException, () -> MqttUtils.validateMqttTopic(splitTopic));
    } else {
      MqttUtils.validateMqttTopic(splitTopic);
    }
  }

  @Test
  @Parameters(method = "topicElements")
  @TestCaseName("topicElement('{0}') validation throws {1}")
  public void validateTopicElement_test(String topicElement,
                                        Class<? extends Exception> expectedException) {
    if (expectedException != null) {
      assertThrows(expectedException, () -> MqttUtils.validateTopicElement(topicElement));
    } else {
      MqttUtils.validateTopicElement(topicElement);
    }
  }

  @Test
  @Parameters(method = "topicElements,topicStartElements")
  @TestCaseName("firstTopicElement('{0}') validation throws {1}")
  public void validateFirstTopicElement_test(String topicElement,
                                             Class<? extends Exception> expectedException) {
    if (expectedException != null) {
      assertThrows(expectedException, () -> MqttUtils.validateFirstTopicElement(topicElement));
    } else {
      MqttUtils.validateFirstTopicElement(topicElement);
    }
  }

  @Test
  @Parameters(method = "topicElements,topicSubscriptionElements")
  @TestCaseName("topicElement('{0}') validation throws {1}")
  public void validateSubscriptionTopicElement_test(String topicElement,
                                                    Class<? extends Exception> expectedException) {
    if (expectedException != null) {
      assertThrows(expectedException, 
          () -> MqttUtils.validateSubscriptionTopicElement(topicElement));
    } else {
      MqttUtils.validateSubscriptionTopicElement(topicElement);
    }
  }

  /* Objects for parametrized tests */

  private Object[] topicStartElements() {
    return new Object[]{new Object[]{"$Test", LlCoreRuntimeException.class}};
  }

  private Object[] topicSubscriptionElements() {
    return new Object[]{new Object[]{"+", null}, new Object[]{"#", null},};
  }

  private Object[] topicElements() {
    return new Object[]{new Object[]{"Test 1", LlCoreRuntimeException.class},
        new Object[]{"", LlCoreRuntimeException.class},
        new Object[]{" Test", LlCoreRuntimeException.class},
        new Object[]{"Test\r", LlCoreRuntimeException.class},
        new Object[]{"Test\r\n", LlCoreRuntimeException.class},
        new Object[]{"Test\n", LlCoreRuntimeException.class},
        new Object[]{"Test\t2", LlCoreRuntimeException.class}, new Object[]{"Hallo", null},
        new Object[]{"Hallo_2", null}};
  }

  private Object[] mqttTopics() {
    return new Object[]{new Object[]{"at/ac/test/hallo", null},
        new Object[]{"/at/ac/test/hallo", LlCoreRuntimeException.class},
        new Object[]{"at/+/test/hallo", LlCoreRuntimeException.class},
        new Object[]{"at/ac/test/+", LlCoreRuntimeException.class},
        new Object[]{"at/ac/test/#", LlCoreRuntimeException.class},
        new Object[]{"at/ac/te#st/", null},
        new Object[]{"at/#/test/hallo", LlCoreRuntimeException.class},
        new Object[]{"$at/ac/test/hallo/", LlCoreRuntimeException.class},
        new Object[]{"at/ac//hallo/", LlCoreRuntimeException.class},
        new Object[]{"", LlCoreRuntimeException.class},};
  }

  private Object[] mqttSubscriptions() {
    return new Object[]{new Object[]{"at/ac/test/hallo", null},
        new Object[]{"/at/ac/test/hallo", LlCoreRuntimeException.class},
        new Object[]{"at/+/test/hallo", null}, new Object[]{"at/ac/test/+", null},
        new Object[]{"at/ac/test/#", null}, new Object[]{"at/ac/te#st/", null},
        new Object[]{"at/#/test/hallo", LlCoreRuntimeException.class},
        new Object[]{"at/ac/test/hallo/", null},
        new Object[]{"at/ac//hallo/", LlCoreRuntimeException.class},
        new Object[]{"+/+/+/#", null}, new Object[]{"", LlCoreRuntimeException.class},};
  }

  private Object[] mqttPublishTopics() {
    return new Object[]{
        new Object[]{"at/ac/test/hallo", Arrays.asList("at", "ac", "test", "hallo")},
        new Object[]{"/at/ac/test/hallo", Arrays.asList("", "at", "ac", "test", "hallo")},
        new Object[]{"at/+/test/hallo", Arrays.asList("at", "+", "test", "hallo")},
        new Object[]{"at/ac/te#st/", Arrays.asList("at", "ac", "te#st")},
        new Object[]{"at/#/test/hallo", Arrays.asList("at", "#", "test", "hallo")},
        new Object[]{"$at/ac/test/hallo/", Arrays.asList("$at", "ac", "test", "hallo")},
        new Object[]{"at/ac//hallo/", Arrays.asList("at", "ac", "", "hallo")},};
  }

  private Object[] mqttPublishTopicConvert() {
    return new Object[]{
        new Object[]{"at/ac/test/hallo", Arrays.asList("at", "ac", "test", "hallo")},
        new Object[]{"/at/ac/test/hallo", Arrays.asList("", "at", "ac", "test", "hallo")},
        new Object[]{"at/+/test/hallo", Arrays.asList("at", "+", "test", "hallo")},
        new Object[]{"at/ac/te#st", Arrays.asList("at", "ac", "te#st")},
        new Object[]{"at/#/test/hallo", Arrays.asList("at", "#", "test", "hallo")},
        new Object[]{"$at/ac/test/hallo", Arrays.asList("$at", "ac", "test", "hallo")},
        new Object[]{"at/ac//hallo", Arrays.asList("at", "ac", "", "hallo")},};
  }

  private Object[] mqttSubscriptionConvert() {
    return new Object[]{
        new Object[]{"at/ac/test/hallo", Arrays.asList("at", "ac", "test", "hallo")},
        new Object[]{"/at/ac/test/hallo", Arrays.asList("", "at", "ac", "test", "hallo")},
        new Object[]{"at/+/test/hallo", Arrays.asList("at", "+", "test", "hallo")},
        new Object[]{"at/ac/test/+", Arrays.asList("at", "ac", "test", "+")},
        new Object[]{"at/ac/test/#", Arrays.asList("at", "ac", "test", "#")},
        new Object[]{"at/ac/te#st", Arrays.asList("at", "ac", "te#st")},
        new Object[]{"at/#/test/hallo", Arrays.asList("at", "#", "test", "hallo")},
        new Object[]{"at/ac/test/hallo", Arrays.asList("at", "ac", "test", "hallo")},
        new Object[]{"at/ac//hallo", Arrays.asList("at", "ac", "", "hallo")},
        new Object[]{"+/+/+/#", Arrays.asList("+", "+", "+", "#")},};
  }
}