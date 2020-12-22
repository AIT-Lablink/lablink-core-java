//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync.payloads;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.connection.encoding.encodables.PayloadBase;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

/**
 * IPayload for the sync go request.
 *
 * <p>Specific message to request clients of a synchronized simulation to progress in time
 * until the given simulation time
 */
public class SyncGoRequest extends PayloadBase {

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
    return "SyncGoRequest";
  }

  /**
   * Get the factory to create objects of the class.
   *
   * <p><b>This static method must be implemented by every subclass.</b>
   *
   * <p>Every class that is encodable and is used by a decoder must have a unique factory object to
   * create empty objects of the class. This factory method will be used by the decoder to create a
   * fresh object that can be filled in with the decoded values.
   *
   * @return A factory object for creating encodable classes
   */
  public static IEncodableFactory getEncodableFactory() {
    return new IEncodableFactory() {
      @Override
      public IEncodable createEncodableObject() {
        return new SyncGoRequest();
      }
    };
  }

  private long actualSimTime;
  private long simUntil;

  public SyncGoRequest() {
  }

  public SyncGoRequest(long actualSimTime, long simUntil) {
    this.actualSimTime = actualSimTime;
    this.simUntil = simUntil;
  }


  @Override
  public void encode(IEncoder encoder) {

    encoder.putLong("actualTime", actualSimTime);
    encoder.putLong("simUntil", simUntil);
  }

  @Override
  public void decode(IDecoder decoder) {

    actualSimTime = decoder.getLong("actualTime");
    simUntil = decoder.getLong("simUntil");
  }

  @Override
  public String getType() {
    return SyncGoRequest.getClassType();
  }

  @Override
  public void decodingCompleted() {
    // expected
  }

  @Override
  public void validate() {
    if (this.simUntil == 0) {
      throw new LlCoreRuntimeException("SimUntil time is zero!");
    }
  }

  @Override
  public String toString() {
    return "SyncGoRequest{" + "actualSimTime=" + actualSimTime + ", simUntil=" + simUntil + '}';
  }

  public long getActualSimTime() {
    return actualSimTime;
  }

  public long getSimUntil() {
    return simUntil;
  }
}
