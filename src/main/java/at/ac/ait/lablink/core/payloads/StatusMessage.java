//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.payloads;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.connection.encoding.IEncodeableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.connection.encoding.encodeables.PayloadBase;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

/**
 * IPayload type for status messages.
 *
 * <p>This internal status codes can be chosen using the enumeration
 * {@link StatusCode}.
 */
public class StatusMessage extends PayloadBase {

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
    return "statusMsg";
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
    return new IEncodeableFactory() {
      @Override
      public IEncodeable createEncodeableObject() {
        return new StatusMessage();
      }
    };
  }

  /* Status code of the message*/
  private StatusCode statusCode = StatusCode.NOT_SET;

  /* Message string of the status message */
  private String message;

  /* generation time of the status message */
  private long time;

  /**
   * Default constructor.
   */
  public StatusMessage() {
  }


  /**
   * Constructor.
   *
   * @param statusCode Using self defined error codes. The value should be below -100 for user
   *                  defined error messages.
   * @param message   Message string describing the error.
   */
  public StatusMessage(StatusCode statusCode, String message) {
    this.statusCode = statusCode;
    this.message = message;
    this.time = System.currentTimeMillis();
  }

  /**
   * Constructor.
   *
   * @param statusCode Error code using predefined values.
   */
  public StatusMessage(StatusCode statusCode) {
    this(statusCode, "");
  }

  @Override
  public void encode(IEncoder encoder) {
    encoder.putString("code", statusCode.name());
    encoder.putString("msg", message);
    encoder.putLong("time", time);
  }

  @Override
  public void decode(IDecoder decoder) {
    statusCode = StatusCode.valueOf(decoder.getString("code"));
    message = decoder.getString("msg");
    time = decoder.getLong("time");
  }

  @Override
  public String getType() {
    return StatusMessage.getClassType();
  }

  @Override
  public void decodingCompleted() {

  }

  @Override
  public void validate() {
    if (this.statusCode == null) {
      throw new LlCoreRuntimeException("Status code in StatusMessage is null.");
    }
    if (this.message == null) {
      throw new LlCoreRuntimeException("Message string in StatusMessage is null.");
    }
    if (this.time == 0) {
      throw new LlCoreRuntimeException("Time in StatusMessage is null.");
    }
  }

  /**
   * Get the status code of the message.
   *
   * @return status code of the message
   */
  public StatusCode getStatusCode() {
    return statusCode;
  }

  /**
   * Get the message of the status message.
   *
   * @return The message of the error payloads.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Get the generation time of the status message.
   *
   * @return the generation time.
   */
  public long getTime() {
    return time;
  }

  @Override
  public String toString() {
    return "StatusMessage{" + "statusCode=" + statusCode + ", message='" + message + '\''
        + ", time=" + time + '}';
  }

  /**
   * Predefined status codes. The values can also be used by the user
   * but they should not be overwritten for other status types.
   */
  public enum StatusCode {
    OK,
    NOK,
    NOT_SET,
    NO_PAYLOAD
  }
}
