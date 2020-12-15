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
 * Subject of a message call.
 *
 * <p>The class is used to define a subject for message publishing which will be used during the
 * publishing of new messages
 *
 * <p>The subject can be generated using a builder class that allows to set the elements of the
 * subject individually.
 */

public class MsgSubject {

  /* Elements of the subject */
  private final List<String> subject;

  /**
   * Default private constructor.
   *
   * @param subject List of string to be set as subject
   */
  private MsgSubject(List<String> subject) {
    this.subject = subject;
  }

  /**
   * Get the subject.
   *
   * @return list of string containing the subject elements.
   */
  public List<String> getSubject() {
    return subject;
  }

  /**
   * Builder class for a MsgSubject.
   *
   * <p>The builder will be used to generate a subject for publishing messages.
   */
  public static class Builder {
    private final List<String> subject = new ArrayList<String>();

    /**
     * Add a subject element to the builder. The order of calling the addition of elements will
     * influence the generated subject.
     *
     * @param element Subject part to be added to the builder
     * @return The builder.
     */
    public Builder addSubjectElement(String element) {
      subject.add(element);
      return this;
    }

    /**
     * Add subject elements to the builder. The order of calling the addition of elements will
     * influence the generated subject.
     *
     * @param elements Subject parts to be added to the builder
     * @return The builder.
     */
    public Builder addSubjectElements(List<String> elements) {
      subject.addAll(elements);
      return this;
    }

    /**
     * Generates and returns a new MsgSubject element with the included subject elements.
     *
     * @return a new MsgSubject object.
     * @throws LlCoreRuntimeException if the subject contains unwanted elements.
     */
    public MsgSubject build() {
      if (subject.isEmpty()) {
        throw new LlCoreRuntimeException("Subject contains no elements.");
      }
      MqttUtils.validateMqttTopic(subject);
      return new MsgSubject(this.subject);
    }
  }

  /**
   * Get a new builder to generate a MsgSubject.
   *
   * @return a new builder for generating a MsgSubject.
   */
  public static Builder getBuilder() {
    return new Builder();
  }
}
