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
 * Subject of a RPC call.
 *
 * <p>The class is used to define a subject for RPC communication which will be used during the
 * registration of a Requester or Handler.
 *
 * <p>The subject can be generated using a builder class that allows to set the elements of the
 * subject individually.
 */
public class RpcSubject {

  /* Elements of the subject */
  private final List<String> subject;

  /**
   * Default private constructor.
   *
   * @param subject List of string to be set as subject
   */
  private RpcSubject(List<String> subject) {
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
   * Builder class for a RpcSubject.
   *
   * <p>The builder will be used to generate a subject for RPC requests.
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
     * Generates and returns a new RpcSubject element with the included subject elements.
     *
     * @return a new RpcSubject object.
     * @throws LlCoreRuntimeException if the subject contains unwanted elements.
     */
    public RpcSubject build() {
      if (subject.isEmpty()) {
        throw new LlCoreRuntimeException("Subject contains no elements.");
      }
      MqttUtils.validateMqttSubscription(subject);
      return new RpcSubject(subject);
    }
  }

  /**
   * Get a new builder to generate a RpcSubject.
   *
   * @return a new builder for generating a RpcSubject.
   */
  public static Builder getBuilder() {
    return new Builder();
  }
}
