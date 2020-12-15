//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.encodeables;

import at.ac.ait.lablink.core.connection.encoding.IEncodeableFactory;

/**
 * Abstract base class for a encodeables object. Used by the packet for communication
 *
 * <p>Subclass must have an empty constructor to use the same class with other type of class strings
 * than as expected with the static type string.
 *
 * <p>The Java implementation should use this abstract base class to implement a payloads object. So
 * the IEncodeableFactoryManager can use all functionality.
 */
public abstract class PayloadBase implements IPayload {

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
    throw new IllegalStateException("Type info hasn't been set up in the subclass");
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
        "IPayload Factory method (getEncodeableFactory) hasn't been set up in the subclass");
  }

  public abstract void validate();
}
