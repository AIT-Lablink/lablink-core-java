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
 * IPayload type for error messages.
 *
 * <p>This payloads type is used in a special way within the system. If such an error message is
 * received by the incoming handler it will be treated separately. The error message contains an
 * error code which is a negative number. The values between -1 and -100 are reserved for
 * internal errors. This internal error codes can be chosen using the enumeration
 * {@link EErrorCode}. Values below -100 can be used by the user to define its own error types. An
 * error message can also be generated within the client to inform the user for internal problems
 * (mainly for error states during decoding and processing incoming messages).
 */
public class ErrorMessage extends PayloadBase {

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
    return "errorMsg";
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
        return new ErrorMessage();
      }
    };
  }

  /* Error code of the message*/
  private EErrorCode errorCode = EErrorCode.NO_ERROR;

  /* Message string of the error message */
  private String message;

  /* generation time of the error message */
  private long time;

  /**
   * Default constructor.
   */
  public ErrorMessage() {
  }


  /**
   * Constructor.
   *
   * @param errorCode Error code using predefined values.
   * @param message   Message string describing the error.
   */
  public ErrorMessage(EErrorCode errorCode, String message) {
    this.errorCode = errorCode;
    this.message = message;
    this.time = System.currentTimeMillis();
  }

  @Override
  public void encode(IEncoder encoder) {
    encoder.putString("code", errorCode.name());
    encoder.putString("msg", message);
    encoder.putLong("time", time);
  }

  @Override
  public void decode(IDecoder decoder) {
    errorCode = EErrorCode.valueOf(decoder.getString("code"));
    message = decoder.getString("msg");
    time = decoder.getLong("time");
  }

  @Override
  public String getType() {
    return ErrorMessage.getClassType();
  }

  @Override
  public void decodingCompleted() {

  }

  @Override
  public void validate() {
    if (this.errorCode == null) {
      throw new LlCoreRuntimeException("Error code in ErrorMessage is null.");
    }
    if (this.message == null || this.message.isEmpty()) {
      throw new LlCoreRuntimeException("Message string in ErrorMessage is null or empty.");
    }
    if (this.time == 0) {
      throw new LlCoreRuntimeException("Time in ErrorMessage is null.");
    }
  }

  /**
   * Get the error code of the message.
   *
   * @return error code of the message
   */
  public EErrorCode getErrorCode() {
    return errorCode;
  }

  /**
   * Get the message of the error message.
   *
   * @return The message of the error payloads.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Get the generation time of the error message.
   *
   * @return the generation time.
   */
  public long getTime() {
    return time;
  }

  @Override
  public String toString() {
    return "ErrorMessage{" + "errorCode=" + errorCode + ", message='" + message + '\'' + ", time="
        + time + '}';
  }

  /**
   * Predefined error codes for internal error states. The values can also be used by the user
   * but they should not be overwritten for other error types.
   */
  public enum EErrorCode {
    NO_ERROR(0), DECODING_ERROR(-1), VALIDATION_ERROR(-2), PROCESSING_ERROR(-3), TIMEOUT_ERROR(
        -4), EMPTY_PAYLOAD(-5), SYNC_ERROR(-100);

    private final int errorCode;

    EErrorCode(int errorCode) {
      this.errorCode = errorCode;
    }

    public int getErrorCode() {
      return this.errorCode;
    }
  }
}
