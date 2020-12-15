//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.messaging;

import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.connection.encoding.IEncodeableFactory;
import at.ac.ait.lablink.core.connection.encoding.encodeables.Header;

import java.util.List;

/**
 * Special header for the messaging transmission. This header class is a implementation of the
 * common and abstract {@link Header} element.
 */
public class MsgHeader extends Header {

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
    return "msg-header";
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
        return new MsgHeader();
      }
    };
  }

  /**
   * Default constructor.
   */
  public MsgHeader() {
    super();
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
  public MsgHeader(String applicationId, String sourceGroupId, String sourceClientId,
                   List<String> subject, long timestamp) {
    super(applicationId, sourceGroupId, sourceClientId, subject, timestamp);
  }

  @Override
  public String getType() {
    return MsgHeader.getClassType();
  }

  @Override
  public String toString() {
    return "MsgHeader{" + super.toString() + '}';
  }
}
