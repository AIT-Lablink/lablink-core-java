//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.topic;

import static org.junit.Assert.assertEquals;

import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for a message topic object.
 */
public class TopicTest {

  @Test
  public void getTopic_setSinglePrefix_test() throws Exception {
    Topic msgTopic = new Topic();
    msgTopic.setPrefix("com.example");
    msgTopic.setApplicationId("test");
    msgTopic.setTransmissionType("msg");
    msgTopic.setClientIdentifiers("group1", "client1");
    msgTopic.setSubject(Arrays.asList("measurement", "voltage", "L1"));

    List<String> actualTopic = msgTopic.getTopic();
    List<String>
        expectedTopic =
        Arrays.asList("com.example", "test", "msg", "group1", "client1", "measurement", "voltage",
            "L1");

    assertEquals(expectedTopic, actualTopic);
  }

  @Test
  public void getTopic_setMultiPrefix_test() throws Exception {
    Topic msgTopic = new Topic();
    msgTopic.setPrefix(Arrays.asList("com.example", "prefixElement2"));
    msgTopic.setApplicationId("test");
    msgTopic.setTransmissionType("msg");
    msgTopic.setClientIdentifiers("group1", "client1");
    msgTopic.setSubject(Arrays.asList("measurement", "voltage", "L1"));

    List<String> actualTopic = msgTopic.getTopic();
    List<String>
        expectedTopic =
        Arrays.asList("com.example", "prefixElement2", "test", "msg", "group1", "client1",
            "measurement", "voltage", "L1");

    assertEquals(expectedTopic, actualTopic);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void getTopic_MissingPrefix_ShouldThrow_test() throws Exception {
    Topic msgTopic = new Topic();
    msgTopic.setApplicationId("test");
    msgTopic.setTransmissionType("msg");
    msgTopic.setClientIdentifiers("group1", "client1");
    msgTopic.setSubject(Arrays.asList("measurement", "voltage", "L1"));

    msgTopic.getTopic();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void getTopic_MissingAppId_ShouldThrow_test() throws Exception {
    Topic msgTopic = new Topic();
    msgTopic.setPrefix(Arrays.asList("com.example", "prefixElement2"));
    msgTopic.setTransmissionType("msg");
    msgTopic.setClientIdentifiers("group1", "client1");
    msgTopic.setSubject(Arrays.asList("measurement", "voltage", "L1"));

    msgTopic.getTopic();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void getTopic_MissingTransmissionType_ShouldThrow_test() throws Exception {
    Topic msgTopic = new Topic();
    msgTopic.setPrefix(Arrays.asList("com.example", "prefixElement2"));
    msgTopic.setApplicationId("test");
    msgTopic.setClientIdentifiers("group1", "client1");
    msgTopic.setSubject(Arrays.asList("measurement", "voltage", "L1"));

    msgTopic.getTopic();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void getTopic_MissingGroupId_ShouldThrow_test() throws Exception {
    Topic msgTopic = new Topic();
    msgTopic.setPrefix(Arrays.asList("com.example", "prefixElement2"));
    msgTopic.setApplicationId("test");
    msgTopic.setTransmissionType("msg");
    msgTopic.setClientIdentifiers(null, "client1");
    msgTopic.setSubject(Arrays.asList("measurement", "voltage", "L1"));

    msgTopic.getTopic();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void getTopic_MissingClientId_ShouldThrow_test() throws Exception {
    Topic msgTopic = new Topic();
    msgTopic.setPrefix(Arrays.asList("com.example", "prefixElement2"));
    msgTopic.setApplicationId("test");
    msgTopic.setTransmissionType("msg");
    msgTopic.setClientIdentifiers("group1", null);
    msgTopic.setSubject(Arrays.asList("measurement", "voltage", "L1"));

    msgTopic.getTopic();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void getTopic_MissingSubject_ShouldThrow_test() throws Exception {
    Topic msgTopic = new Topic();
    msgTopic.setPrefix(Arrays.asList("com.example", "prefixElement2"));
    msgTopic.setApplicationId("test");
    msgTopic.setTransmissionType("msg");
    msgTopic.setClientIdentifiers("group1", "client1");

    msgTopic.getTopic();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void getTopic_EmptyPrefix_ShouldThrow_test() throws Exception {
    Topic msgTopic = new Topic();
    msgTopic.setPrefix("");
    msgTopic.setApplicationId("test");
    msgTopic.setTransmissionType("msg");
    msgTopic.setClientIdentifiers("group1", "client1");
    msgTopic.setSubject(Arrays.asList("measurement", "voltage", "L1"));

    msgTopic.getTopic();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void getTopic_EmptyAppId_ShouldThrow_test() throws Exception {
    Topic msgTopic = new Topic();
    msgTopic.setPrefix("com.example");
    msgTopic.setApplicationId("");
    msgTopic.setTransmissionType("msg");
    msgTopic.setClientIdentifiers("group1", "client1");
    msgTopic.setSubject(Arrays.asList("measurement", "voltage", "L1"));

    msgTopic.getTopic();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void getTopic_EmptyTransmissionType_ShouldThrow_test() throws Exception {
    Topic msgTopic = new Topic();
    msgTopic.setPrefix("com.example");
    msgTopic.setApplicationId("test");
    msgTopic.setTransmissionType("");
    msgTopic.setClientIdentifiers("group1", "client1");
    msgTopic.setSubject(Arrays.asList("measurement", "voltage", "L1"));

    msgTopic.getTopic();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void getTopic_EmptyGroupId_ShouldThrow_test() throws Exception {
    Topic msgTopic = new Topic();
    msgTopic.setPrefix("com.example");
    msgTopic.setApplicationId("test");
    msgTopic.setTransmissionType("msg");
    msgTopic.setClientIdentifiers("", "client1");
    msgTopic.setSubject(Arrays.asList("measurement", "voltage", "L1"));

    msgTopic.getTopic();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void getTopic_EmptyClientId_ShouldThrow_test() throws Exception {
    Topic msgTopic = new Topic();
    msgTopic.setPrefix("com.example");
    msgTopic.setApplicationId("test");
    msgTopic.setTransmissionType("msg");
    msgTopic.setClientIdentifiers("group1", "");
    msgTopic.setSubject(Arrays.asList("measurement", "voltage", "L1"));

    msgTopic.getTopic();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void getTopic_EmptySubject_ShouldThrow_test() throws Exception {
    Topic msgTopic = new Topic();
    msgTopic.setPrefix("com.example");
    msgTopic.setApplicationId("test");
    msgTopic.setTransmissionType("msg");
    msgTopic.setClientIdentifiers("group1", "client1");
    msgTopic.setSubject(Arrays.asList("", ""));

    msgTopic.getTopic();
  }
}