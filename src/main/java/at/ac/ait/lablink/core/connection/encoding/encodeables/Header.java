//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.encodeables;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.connection.encoding.IEncodeableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for a header object. Used by the packet for communication.
 *
 * <p>The element is implemented as an immutable object that can only be externally set by the
 * constructor.
 */
public abstract class Header implements IEncodeable {

  /**
   * Get a type string of the class.
   *
   * <p><b>This static method must be implemented by every subclass.</b>
   *
   * <p>Every class that is encodeable and is used by a decoder must have a unique string that
   * identifies this class. This type string will be transmitted during the communication and will
   * be used by a decoder for creating an empty object of the encodeable class.
   *
   * @return an unique type string of the class
   */
  public static String getClassType() {
    throw new IllegalStateException("Type info (getClassType) hasn't been set up in the subclass");
  }

  /**
   * Get the factory to create objects of the class.
   *
   * <p><b>This static method must be implemented by every subclass.</b>
   *
   * <p>Every class that is encodeable and is used by a decoder must have a unique factory object
   * to create empty objects of the class. This factory method will be used by the decoder to
   * create a fresh object that can be filled in with the decoded values.
   *
   * @return A factory object for creating encodeable classes
   */
  public static IEncodeableFactory getEncodeableFactory() {
    throw new IllegalStateException(
        "Header Factory method (getEncodeableFactory) hasn't been set up in the subclass");
  }

  private String applicationId;
  private String sourceGroupId;
  private String sourceClientId;
  private List<String> subject;
  private long timestamp;

  /**
   * Default constructor.
   */
  public Header() {
  }

  /**
   * Constructor.
   *
   * @param applicationId  Identifier of the application (e.g., EvTestStand)
   * @param sourceGroupId  Group identifier of the source client
   * @param sourceClientId Client identifier of the source client
   * @param subject        List of subject elements of the message
   * @param timestamp      Actual timestamp of the generated message
   */
  public Header(String applicationId, String sourceGroupId, String sourceClientId,
                List<String> subject, long timestamp) {
    this.applicationId = applicationId;
    this.sourceGroupId = sourceGroupId;
    this.sourceClientId = sourceClientId;
    this.subject = subject;
    this.timestamp = timestamp;
  }

  @Override
  public void encode(IEncoder encoder) {
    encoder.putString("appId", applicationId);
    encoder.putString("srcGrId", sourceGroupId);
    encoder.putString("srcClId", sourceClientId);
    encoder.putStringList("subject", subject);
    encoder.putLong("time", timestamp);
  }

  @Override
  public void decode(IDecoder decoder) {
    applicationId = decoder.getString("appId");
    sourceGroupId = decoder.getString("srcGrId");
    sourceClientId = decoder.getString("srcClId");
    subject = decoder.getStrings("subject");
    timestamp = decoder.getLong("time");
  }

  @Override
  public void decodingCompleted() {
    // Expected empty
  }

  @Override
  public void validate() {

    if (this.applicationId == null || this.applicationId.isEmpty()) {
      throw new LlCoreRuntimeException("ApplicationId in Header is null or empty.");
    }
    if (this.sourceGroupId == null || this.sourceGroupId.isEmpty()) {
      throw new LlCoreRuntimeException("SourceGroupId in Header is null or empty.");
    }
    if (this.sourceClientId == null || this.sourceClientId.isEmpty()) {
      throw new LlCoreRuntimeException("SourceClientId in Header is null or empty.");
    }
    if (this.subject == null || this.subject.isEmpty()) {
      throw new LlCoreRuntimeException("Subject in Header is null or empty.");
    }

    for (String subjectElement : subject) {
      if (subjectElement == null || subjectElement.isEmpty()) {
        throw new LlCoreRuntimeException("Subject Element in Header is null or empty.");
      }
    }

    if (this.timestamp == 0) {
      throw new LlCoreRuntimeException("Timestamp in Header is null.");
    }
  }

  /**
   * Read the application identifier of the header.
   *
   * @return the application identifier.
   */
  public String getApplicationId() {
    return applicationId;
  }

  /**
   * Read the source's group identifier of the header.
   *
   * @return the group identifier.
   */
  public String getSourceGroupId() {
    return sourceGroupId;
  }

  /**
   * Read the source's client identifier.
   *
   * @return the client identifier.
   */
  public String getSourceClientId() {
    return sourceClientId;
  }

  /**
   * Read the subject list of the header element.
   *
   * @return the subject.
   */
  public List<String> getSubject() {
    return Collections.unmodifiableList(subject);
  }

  /**
   * Get the timestamp of the header element.
   *
   * @return timestamp when the header was generated.
   */
  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public String toString() {
    return "applicationId='" + applicationId + '\'' + ", sourceGroupId='" + sourceGroupId + '\''
        + ", sourceClientId='" + sourceClientId + '\'' + ", subject=" + subject + ", timestamp="
        + timestamp;
  }
}
