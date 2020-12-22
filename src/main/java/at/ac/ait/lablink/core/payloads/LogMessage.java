//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.payloads;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.connection.encoding.encodables.PayloadBase;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

/**
 * A payloads type for LOG messages.
 */
public class LogMessage extends PayloadBase {

  /**
   * Get a type string of the class.
   *
   * <p><b>This static method must be implemented by every subclass.</b>
   *
   * <p>Every class that is encodable and is used by a decoder must have a unique string that
   * identifies this class. This type string will be transmitted during the communication and will
   * be used by a decoder for creating an empty object of the encodable class.
   *
   * @return an unique type string of the class
   */
  public static String getClassType() {
    return "logMsg";
  }

  /**
   * Get the factory to create objects of the class.
   *
   * <p><b>This static method must be implemented by every subclass.</b>
   *
   * <p>Every class that is encodable and is used by a decoder must have a unique factory object
   * to create empty objects of the class. This factory method will be used by the decoder to
   * create a fresh object that can be filled in with the decoded values.
   *
   * @return A factory object for creating encodable classes
   */
  public static IEncodableFactory getEncodableFactory() {
    return new IEncodableFactory() {
      @Override
      public IEncodable createEncodableObject() {
        return new LogMessage();
      }
    };
  }


  private String loggerName;
  private String threadName;
  private String logLevel;
  private String message;
  private long timeStamp;

  /**
   * Default constructor.
   */
  public LogMessage() {
  }

  /**
   * Constructor
   *
   * @param loggerName Name of the logger.
   * @param threadName Current running thread of the logger message.
   * @param logLevel   Level of the log message.
   * @param message    Message of the log.
   * @param timeStamp  Timestamp of the log message.
   */
  public LogMessage(String loggerName, String threadName, String logLevel, String message,
                    long timeStamp) {
    this.loggerName = loggerName;
    this.threadName = threadName;
    this.logLevel = logLevel;
    this.message = message;
    this.timeStamp = timeStamp;
  }

  @Override
  public void encode(IEncoder encoder) {
    encoder.putString("logName", loggerName);
    encoder.putString("thread", threadName);
    encoder.putString("level", logLevel);
    encoder.putString("msg", message);
    encoder.putLong("time", timeStamp);
  }

  @Override
  public void decode(IDecoder decoder) {
    this.loggerName = decoder.getString("logName");
    this.threadName = decoder.getString("thread");
    this.logLevel = decoder.getString("level");
    this.message = decoder.getString("msg");
    this.timeStamp = decoder.getLong("time");
  }

  @Override
  public String getType() {
    return LogMessage.getClassType();
  }

  @Override
  public void decodingCompleted() {

  }

  @Override
  public void validate() {

    if (this.loggerName == null) {
      throw new LlCoreRuntimeException("Logger name in StatusMessage is null.");
    }
    if (this.threadName == null) {
      throw new LlCoreRuntimeException("Thread name in StatusMessage is null.");
    }
    if (this.logLevel == null) {
      throw new LlCoreRuntimeException("Logger Level in StatusMessage is null.");
    }
    if (this.message == null) {
      throw new LlCoreRuntimeException("Message string in StatusMessage is null.");
    }
    if (this.timeStamp == 0) {
      throw new LlCoreRuntimeException("Time in LogMessage is null.");
    }
  }

  /**
   * Get the logger name of the payloads.
   *
   * @return the logger name.
   */
  public String getLoggerName() {
    return loggerName;
  }

  /**
   * Get the thread name of the log message.
   *
   * @return the thread name.
   */
  public String getThreadName() {
    return threadName;
  }

  /**
   * Get the log level of the message.
   *
   * @return log level
   */
  public String getLogLevel() {
    return logLevel;
  }

  /**
   * Get the message of the log.
   *
   * @return the log message.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Get the time stamp of the log
   *
   * @return time stamp in milliseconds since epoch.
   */
  public long getTimeStamp() {
    return timeStamp;
  }
}
