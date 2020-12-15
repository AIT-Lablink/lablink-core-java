//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.impl;

import at.ac.ait.lablink.core.connection.encoding.DecoderBase;

import org.apache.commons.configuration.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating the decoder that should be used for decoding packets.
 */
public class DecoderFactory {

  private final EDecoderType defaultDecoder;
  private final Configuration config;
  private EncodeableFactoryManagerImpl encodeableFactoryManager;

  private Map<EDecoderType, DecoderBase> decoders = new HashMap<EDecoderType, DecoderBase>();

  /**
   * Constructor.
   *
   * @param defaultDecoder specifies the decoder that is used as default one
   * @param config         Optional configuration that is redirected to the decoder object.
   */
  public DecoderFactory(EDecoderType defaultDecoder, Configuration config) {
    this.config = config;
    this.defaultDecoder = defaultDecoder;
  }

  /**
   * Get a specific decoder given by the key.
   *
   * @param key Key that indicated the decoder to be created.
   * @return a new object of the specified decoder.
   */
  public synchronized DecoderBase getDecoderObject(EDecoderType key) {

    DecoderBase decoder = decoders.get(key);

    if (decoder == null) {
      decoder = createDecoderObject(key);
    }

    return decoder;
  }


  private DecoderBase createDecoderObject(EDecoderType key) {

    DecoderBase decoder;

    switch (key) {
      default:
        decoder = new JsonDecoder(config);
    }

    decoder.setEncodeableFactoryManager(this.encodeableFactoryManager);

    this.decoders.put(key, decoder);
    return decoder;
  }

  /**
   * Get a default decoder that is specified by the factory instantiation.
   *
   * @return a new created object of the default decoder.
   */
  public DecoderBase getDefaultDecoderObject() {
    return getDecoderObject(this.defaultDecoder);
  }


  /**
   * Return the encoder object to the existing Pool
   *
   * @param decoder IEncoder that should be returned to the pool.
   */
  public void returnDecoderToPool(DecoderBase decoder) {
    //TODO implementation
  }

  /**
   * Set the factory manager for encodeable objects that will be redirect to the generated
   * decoder objects.
   *
   * @param encodeableFactoryManager that should be rediredt to the generated decoder.
   */
  public void setEncodeableFactoryManager(EncodeableFactoryManagerImpl encodeableFactoryManager) {
    this.encodeableFactoryManager = encodeableFactoryManager;
  }


  /**
   * Enumeration of different decoder that are used within the system.
   */
  public enum EDecoderType {
    JSON
  }

}
