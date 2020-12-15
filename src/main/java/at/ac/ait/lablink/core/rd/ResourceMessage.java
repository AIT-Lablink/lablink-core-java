//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.rd;

import at.ac.ait.lablink.core.Configuration;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.sql.Timestamp;

/**
 * Message for Lablink resource discovery.
 */
public class ResourceMessage {

  /** The packet. */
  private DatagramPacket packet;

  /** The timestamp. */
  private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

  /** The version. */
  private String version = Configuration.RESOURCE_DISCOVERY_VERSION;

  /** The payload encoding. */
  private String payloadEncoding;

  /**
   * Instantiates a new resource message.
   *
   * @param packet the packet
   * @param plenc the encoding of the payload (JSON, BSON etc.)
   * @param version the version
   */
  public ResourceMessage(DatagramPacket packet, String plenc, String version) {
    super();
    this.setPacket(packet);
    this.setPayloadEncoding(plenc);

    this.version = version;
  }

  /**
   * Gets the packet.
   *
   * @return the packet
   */
  private DatagramPacket getPacket() {
    return packet;
  }

  /**
   * Sets the packet.
   *
   * @param packet the packet to set
   */
  private void setPacket(DatagramPacket packet) {
    this.packet = packet;
  }

  /**
   * Gets the timestamp.
   *
   * @return the timestamp
   */
  public Timestamp getTimestamp() {
    return timestamp;
  }

  /**
   * Sets the timestamp.
   *
   * @param timestamp the timestamp to set
   */
  private void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * Gets the version.
   *
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Gets the payload encoding.
   *
   * @return the payloadEncoding
   */
  public String getPayloadEncoding() {
    return payloadEncoding;
  }

  /**
   * Sets the payload encoding.
   *
   * @param payloadEncoding the payloadEncoding to set
   */
  private void setPayloadEncoding(String payloadEncoding) {
    this.payloadEncoding = payloadEncoding;
  }

  public String getPayload() {
    return new String(this.packet.getData(), 0, this.packet.getLength());
  }

  public InetAddress getSenderIPv4Address() {
    return this.packet.getAddress();
  }

  public int getSenderPort() {
    return this.packet.getPort();
  }

  public long getPayloadLength() {
    return this.packet.getLength();
  }

}
