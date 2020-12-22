//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.connection.encoding.encodables.Header;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import java.util.List;

/**
 * Header object for RPC communication.
 */
public class RpcHeader extends Header {

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
    return "rpc-header";
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
        return new RpcHeader();
      }

    };
  }


  private String destinationGroupId;
  private String destinationClientId;
  private String packetId;

  /**
   * Default Constructor.
   */
  public RpcHeader() {
    super();
  }

  /**
   * Constructor.
   *
   * @param applicationId       Identifier of the application (e.g., EvTestStand)
   * @param sourceGroupId       Group identifier of the source client
   * @param sourceClientId      Client identifier of the source client
   * @param subject             List of subject elements of the message
   * @param timestamp           Actual timestamp of the generated message
   * @param destinationGroupId  Group identifier of the destination client
   * @param destinationClientId Client identifier of the destination client
   * @param packetId            Unique packet identifier.
   */
  public RpcHeader(String applicationId, String sourceGroupId, String sourceClientId,
                   List<String> subject, long timestamp, String destinationGroupId,
                   String destinationClientId, String packetId) {
    super(applicationId, sourceGroupId, sourceClientId, subject, timestamp);
    this.destinationGroupId = destinationGroupId;
    this.destinationClientId = destinationClientId;
    this.packetId = packetId;
  }

  @Override
  public void encode(IEncoder encoder) {
    super.encode(encoder);
    encoder.putString("dstGrId", destinationGroupId);
    encoder.putString("dstClId", destinationClientId);
    encoder.putString("packetId", packetId);
  }

  @Override
  public void decode(IDecoder decoder) {
    super.decode(decoder);
    destinationGroupId = decoder.getString("dstGrId");
    destinationClientId = decoder.getString("dstClId");
    packetId = decoder.getString("packetId");
  }

  @Override
  public String getType() {
    return RpcHeader.getClassType();
  }

  @Override
  public void decodingCompleted() {
    super.decodingCompleted();
  }

  @Override
  public void validate() {
    super.validate();

    if (destinationGroupId == null || destinationGroupId.isEmpty()) {
      throw new LlCoreRuntimeException("DestinationGroupId in Header is null or empty.");
    }
    if (destinationClientId == null || destinationClientId.isEmpty()) {
      throw new LlCoreRuntimeException("DestinationClientId in Header is null or empty.");
    }
    if (packetId == null || packetId.isEmpty()) {
      throw new LlCoreRuntimeException("PacketId in Header is null or empty.");
    }
  }

  /**
   * Read the Group identifier of the destination client.
   *
   * @return group identifier
   */
  public String getDestinationGroupId() {
    return destinationGroupId;
  }

  /**
   * Read the client identifier of the destination client.
   *
   * @return client identifier
   */
  public String getDestinationClientId() {
    return destinationClientId;
  }

  /**
   * Read the unique identifier of the packet.
   *
   * @return unique identifier.
   */
  public String getPacketId() {
    return packetId;
  }

  @Override
  public String toString() {
    return "RpcHeader{" + super.toString() + ", destinationGroupId='" + destinationGroupId + '\''
        + ", destinationClientId='" + destinationClientId + '\'' + ", packetId='" + packetId + '\''
        + '}';
  }
}
