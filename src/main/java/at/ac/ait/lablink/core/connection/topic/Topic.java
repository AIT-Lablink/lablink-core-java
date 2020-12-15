//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.topic;

import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Common representation and data bean of topic or subscription elements for the Lablink
 * communication.
 *
 * <p>It allows to set the required values and generates the topic that is used for the low-level
 * communication.
 */
public class Topic {

  /* Prefix string of the application (e.g., com.example)*/
  private List<String> prefix = new ArrayList<String>();

  /* Identifier of the application (e.g., TestSystem) */
  private String applicationId;

  /* Identifier of the transmission type (e.g., msg, req, log) */
  private String transmissionType;


  /* Group ID of the publishing client */
  private String groupId;

  /* Client ID of the publishing client */
  private String clientId;

  /* Subject list of the published topic */
  private List<String> subject;

  /**
   * Set the prefix of a topic with a single element.
   *
   * @param prefix Single string prefix to be set. (e.g., com.example)
   */
  public void setPrefix(String prefix) {
    this.prefix = Collections.singletonList(prefix);
  }

  /**
   * Set the prefix of a topic as a string list.
   *
   * @param prefix List of string to be set as topic prefix. (e.g., [com.example,lab,test])
   */
  public void setPrefix(List<String> prefix) {
    this.prefix = prefix;
  }

  /**
   * Set the application identifier of the topic.
   *
   * @param applicationId identifier to be set (e.g., TestSystem)
   */
  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  /**
   * Set the transmission type of the topic.
   *
   * @param transmissionType identifier of the transmission. (e.g., msg, req, log)
   */
  public void setTransmissionType(String transmissionType) {
    this.transmissionType = transmissionType;
  }

  /**
   * Set the identifier of the source client that publishes the message.
   *
   * @param groupId  Identifier of the source client's group
   * @param clientId Identifier of the source client
   */
  public void setClientIdentifiers(String groupId, String clientId) {
    this.clientId = clientId;
    this.groupId = groupId;
  }

  /**
   * Set the subject of the message to be published.
   *
   * @param subject List of subject strings.
   */
  public void setSubject(List<String> subject) {
    this.subject = subject;
  }

  /**
   * Read the application identifier of the topic.
   *
   * @return application identifier.
   */
  public String getApplicationId() {
    return this.applicationId;
  }

  /**
   * Get the subject of the message topic.
   *
   * @return the subject of the message topic.
   */
  public List<String> getSubject() {
    return subject;
  }

  /**
   * Get the group identifier that is set for the message topic.
   *
   * @return the set group ID.
   */
  public String getGroupId() {
    return this.groupId;
  }

  /**
   * Get the client identifier that is set for the message topic.
   *
   * @return the set client ID.
   */
  public String getClientId() {
    return this.clientId;
  }

  /**
   * Get the message topic elements as a combined list of all elements.
   *
   * <p>The topic element will be converted into a string list with the parameters
   * [prefix, applicationId, transmissionType, srcGroupId, srcClientId, subject]. Following example
   * will show a possible output [com.example, TestSystem, msg, Group1, Client1, measurement,
   * voltage, L1].
   *
   * @return the created list of topic elements.
   */
  public List<String> getTopic() {
    validateNonEmptyElements();

    List<String> topic = new ArrayList<String>();
    topic.addAll(prefix);
    topic.add(applicationId);
    topic.add(transmissionType);
    topic.add(groupId);
    topic.add(clientId);
    topic.addAll(subject);
    return topic;
  }

  /**
   * Check if all values are set.
   *
   * @throws LlCoreRuntimeException if a value isn't set.
   */
  private void validateNonEmptyElements() {

    try {
      if (prefix.isEmpty()) {
        throw new LlCoreRuntimeException("Error during topic validation: Prefix is empty.");
      }
      for (String s : prefix) {
        if (s.isEmpty()) {
          throw new LlCoreRuntimeException(
              "Error during topic validation: Prefix list contains empty element.");
        }
      }
      if (applicationId.isEmpty()) {
        throw new LlCoreRuntimeException(
            "Error during topic validation: Application identifier is empty.");
      }
      if (transmissionType.isEmpty()) {
        throw new LlCoreRuntimeException(
            "Error during topic validation: Transmission Type is empty.");
      }
      if (groupId.isEmpty()) {
        throw new LlCoreRuntimeException(
            "Error during topic validation: Group identifier is empty.");
      }
      if (clientId.isEmpty()) {
        throw new LlCoreRuntimeException(
            "Error during topic validation: Client identifier is empty.");
      }
      if (subject.isEmpty()) {
        throw new LlCoreRuntimeException(
            "Error during topic validation: Subject list is empty.");
      }
      for (String s : subject) {
        if (s.isEmpty()) {
          throw new LlCoreRuntimeException(
              "Error during topic validation: Subject list contains empty element.");
        }
      }
    } catch (NullPointerException ex) {
      throw new LlCoreRuntimeException("Error during topic validation", ex);
    }
  }
}
