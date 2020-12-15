//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.topic;

import at.ac.ait.lablink.core.connection.mqtt.impl.MqttUtils;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Identifier for a message subscription
 *
 * <p>This class defines a possible subscription of a message topic. It will be a combination
 * of source group identifier, source client identifier and a subject string containing wildcards.
 */
public class MsgSubscription {

  /* subject list of the subscription */
  private final List<String> subject;

  /* source client identifier or any element of the subscription */
  private final String sourceGroupId;

  /* source client group identifier or any element of the subscription */
  private final String sourceClientId;

  /**
   * Default private constructor.
   *
   * @param srcGroupId Group ID for the subscription
   * @param srcClientId Client ID for the subscription
   * @param subject     subject of the subscription
   */
  private MsgSubscription(String srcGroupId, String srcClientId, List<String> subject) {
    this.sourceGroupId = srcGroupId;
    this.sourceClientId = srcClientId;
    this.subject = subject;
  }

  /**
   * Get the subject of the subscription.
   *
   * @return a list of subject elements.
   */
  public List<String> getSubscriptionSubject() {
    return subject;
  }

  /**
   * Get the group identifier of the subscription.
   *
   * @return the group identifier.
   */
  public String getSubscriptionGroupId() {
    return sourceGroupId;
  }

  /**
   * Get the client identifier of the subscription.
   *
   * @return the client identifier.
   */
  public String getSubscriptionClientId() {
    return sourceClientId;
  }

  /**
   * Builder class for a MsgSubscription.
   *
   * <p>The builder will be used to generate a subject for message subscriptions.
   */
  public static class Builder {
    private final List<String> subject = new ArrayList<String>();
    private String srcClientId;

    private String srcGroupId;

    private final EMsgSourceChooser chooser;

    /**
     * Constructor.
     *
     * @param chooser defines the source for receiving possible messages.
     */
    Builder(EMsgSourceChooser chooser) {
      this.chooser = chooser;
    }

    /**
     * Set the client identifier of the message's source.
     *
     * @param srcClientId client identifier of the sending client.
     * @return this builder.
     */
    public Builder setSrcClientId(String srcClientId) {

      if (srcClientId.equals("+") || srcClientId.equals("#")) {
        throw new LlCoreRuntimeException(
            "Validation exception, topic element contains + or # element");
      }
      this.srcClientId = srcClientId;
      return this;
    }

    /**
     * Set the group identifier of the message's source.
     *
     * @param srcGroupId group identifier of the sending client.
     * @return this builder.
     */
    public Builder setSrcGroupId(String srcGroupId) {
      if (srcGroupId.equals("+") || srcGroupId.equals("#")) {
        throw new LlCoreRuntimeException(
            "Validation exception, topic element contains + or # element");
      }
      this.srcGroupId = srcGroupId;
      return this;
    }

    /**
     * Add an element for the subject. This method can be called multiple times to generate the
     * whole subject.
     *
     * @param element one element of the subject.
     * @return this builder
     */
    public Builder addSubjectElement(String element) {
      subject.add(element);
      return this;
    }

    /**
     * Add a list of elements for the subject. This method can be called multiple times to generate
     * the whole subject.
     *
     * @param elements elements of the subject.
     * @return this builder
     */
    public Builder addSubjectElements(List<String> elements) {
      subject.addAll(elements);
      return this;
    }

    /**
     * Add an "receive any" element to the subject. This method can be called multiple times to
     * generate the whole subject and it is allowed to call it in the middle of the subject
     * creation.
     *
     * @return this builder
     */
    public Builder addSubjectAnyElement() {
      subject.add(MqttUtils.TOPIC_WILDCARD_ANY);
      return this;
    }

    /**
     * Add an "receive all" element to the subject. It is only allowed to call this method once
     * at the end of the subject creation.
     *
     * @return this builder
     */
    public Builder addSubjectAllChildren() {
      subject.add(MqttUtils.TOPIC_WILDCARD_ALL);
      return this;
    }

    /**
     * Build a message subscription object with the given parameters from the builder.
     *
     * @return a new and initialized message subscription
     * @throws LlCoreRuntimeException if an error occurs during the validation of the
     *                                     subscription elements.
     */
    public MsgSubscription build() {

      switch (chooser) {

        case RECEIVE_FROM_ALL:
          srcClientId = MqttUtils.TOPIC_WILDCARD_ANY;
          srcGroupId = MqttUtils.TOPIC_WILDCARD_ANY;
          break;
        case RECEIVE_FROM_GROUP:
          srcClientId = MqttUtils.TOPIC_WILDCARD_ANY;
          break;
        case RECEIVE_FROM_CLIENT:
          break;
        default:
          throw new LlCoreRuntimeException("Msg source choosing wasn't set correctly.");
      }

      if (srcClientId == null) {
        throw new LlCoreRuntimeException("Client identifier isn't set.");
      }
      if (srcGroupId == null) {
        throw new LlCoreRuntimeException("Group identifier isn't set.");
      }
      if (subject.isEmpty()) {
        throw new LlCoreRuntimeException("Subject contains no elements.");
      }

      MqttUtils.validateMqttSubscription(srcClientId);
      MqttUtils.validateMqttSubscription(srcGroupId);
      MqttUtils.validateMqttSubscription(subject);

      return new MsgSubscription(srcGroupId, srcClientId, subject);
    }

  }

  /**
   * Get a new builder to generate a MsgSubscription.
   *
   * @param chooser defines the source for receiving possible messages.
   * @return a new builder for generating a MsgSubscription.
   */
  public static Builder getBuilder(EMsgSourceChooser chooser) {
    return new Builder(chooser);
  }

  /**
   * Source of the message that should be received. The configuration will be used to define
   * possible sources of the message.
   */
  public enum EMsgSourceChooser {
    /**
     * Receive the message with the given subject from any client within the system.
     */
    RECEIVE_FROM_ALL,

    /**
     * Receive the message with the given subject from any client of a specific group.
     */
    RECEIVE_FROM_GROUP,

    /**
     * Receive the message with the given subject from a specific client.
     */
    RECEIVE_FROM_CLIENT
  }
}
