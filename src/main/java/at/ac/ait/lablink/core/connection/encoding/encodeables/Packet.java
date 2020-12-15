//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.encodeables;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.connection.encoding.IEncodeableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.connection.ex.LlCoreDecoderRuntimeException;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that represents a packet for the Lablink communication.
 *
 * <p>The packet is the top-level container for the communication. It contains a header field
 * that contains information for the transmission and one or more encodeables containers
 */
public class Packet implements IEncodeable {

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
    return "packet";
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
        return new Packet();
      }
    };
  }

  private static final Logger logger = LogManager.getLogger(Packet.class.getSimpleName());

  /**
   * Header of the packet.
   */
  private Header header;

  /**
   * List of Payloads of the packet.
   */
  private List<IPayload> payloads;

  /**
   * Default constructor.
   */
  public Packet() {
  }

  /**
   * Constructor.
   *
   * @param header   Header of the packet
   * @param payloads List of payloads elements of the packet.
   */
  public Packet(Header header, List<IPayload> payloads) {
    this.header = header;

    try {
      this.payloads = payloads;
    } catch (NullPointerException ex) {
      logger.warn("IPayload List isn't initialized: ", ex);
      this.payloads = Collections.emptyList();
    }
  }


  @Override
  public void encode(IEncoder encoder) {
    encoder.putEncodeable("header", header);
    encoder.putEncodeableList("payload", payloads);
  }

  @Override
  public void decode(IDecoder decoder) {

    IEncodeable header = decoder.getEncodeable("header");
    if (header instanceof Header) {
      this.header = (Header) header;
    } else {
      throw new LlCoreDecoderRuntimeException(
          "Decoded element (" + header.getClass() + ") isn't a header element.");
    }

    List<? extends IEncodeable> encodeables = decoder.getEncodeables("payload");

    List<IPayload> listBuilder = new ArrayList<IPayload>();
    for (IEncodeable encodeable : encodeables) {
      if (encodeable instanceof IPayload) {
        listBuilder.add((IPayload) encodeable);
      } else {
        throw new LlCoreDecoderRuntimeException(
            "Decoded element (" + encodeable.getClass() + ") isn't a encodeables element.");
      }
    }
    payloads = listBuilder;
  }

  @Override
  public String getType() {
    return Packet.getClassType();
  }

  @Override
  public void decodingCompleted() {
    logger.trace("Decoding of packet completed (" + this + ").");
  }

  @Override
  public void validate() {

    if (header == null) {
      throw new LlCoreRuntimeException("Header in Packet is null.");
    }
    header.validate();

    if (payloads == null || payloads.isEmpty()) {
      throw new LlCoreRuntimeException("Payloads in Packet is null or empty.");
    }
    for (IPayload payload : payloads) {
      payload.validate();
    }
  }

  @Override
  public String toString() {
    return "Packet{" + "header=" + header + ", payloads=" + payloads + '}';
  }

  /**
   * Get the header element of the packet.
   *
   * @return The header element of the packet.
   */
  public Header getHeader() {
    return header;
  }

  /**
   * Get the payloads of the packet
   *
   * @return The payloads of the packet as list.
   */
  public List<IPayload> getPayloads() {
    return payloads;
  }
}

