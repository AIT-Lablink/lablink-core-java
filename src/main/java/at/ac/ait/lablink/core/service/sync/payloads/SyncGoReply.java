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
 * IPayload for the sync go reply.
 *
 * <p>Specific message to reply to a sync go request by a sync go reply message indicating
 * whether the go step has been processed sucessfully and what the next simulation time
 * would be from the client's side
 */
public class SyncGoReply extends PayloadBase {

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
    return "SyncGoReply";
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
        return new SyncGoReply();
      }
    };
  }

  private long nextSimTime;

  public SyncGoReply() {
  }

  public SyncGoReply(long nextSimTime) {
    this.nextSimTime = nextSimTime;
  }

  public long getNextSimTime() {
    return nextSimTime;
  }


  @Override
  public void encode(IEncoder encoder) {
    encoder.putLong("nextSimTime", nextSimTime);
  }

  @Override
  public void decode(IDecoder decoder) {
    nextSimTime = decoder.getLong("nextSimTime");
  }

  @Override
  public String getType() {
    return SyncGoReply.getClassType();
  }

  @Override
  public void decodingCompleted() {
    // expected
  }

  @Override
  public void validate() {
    if (this.nextSimTime == 0) {
      throw new LlCoreRuntimeException("NextSimTime is zero!");
    }
  }

  @Override
  public String toString() {
    return "SyncGoReply{" + "nextSimTime='" + nextSimTime + "'}";
  }

}
