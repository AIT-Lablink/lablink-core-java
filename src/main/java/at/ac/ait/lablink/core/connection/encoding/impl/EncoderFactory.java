//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.impl;

import at.ac.ait.lablink.core.connection.encoding.EncoderBase;

import org.apache.commons.configuration.Configuration;

/**
 * Factory class for creating the encoder that should be used for encoding packets.
 */
public class EncoderFactory {

  private final EEncoderType defaultEncoder;
  private final Configuration config;

  private EncoderBase encoder;
  //TODO create encoder pool for thread

  /**
   * Constructor.
   *
   * @param defaultEncoder specifies the encoder that is used as default one
   * @param config         Optional configuration that is redirected to the encoder object.
   */
  public EncoderFactory(EEncoderType defaultEncoder, Configuration config) {
    this.config = config;
    this.defaultEncoder = defaultEncoder;
  }

  /**
   * Get a specific encoder given by the key.
   *
   * @param key Key that indicated the encoder to be created.
   * @return a new object of the specified encoder.
   */
  public synchronized EncoderBase getEncoderObject(EEncoderType key) {

    //TODO borrow encoder from pool
    EncoderBase encoder = this.encoder;

    if (encoder == null) {
      encoder = createEncoderObject(key);
    }


    return encoder;
  }

  /**
   * Create a new encoder object of specific type and add it to the encoder pool.
   *
   * @param key Key that represents the encoder
   * @return the new created encoder object
   */
  private EncoderBase createEncoderObject(EEncoderType key) {
    EncoderBase encoder;

    switch (key) {
      default:
        encoder = new JsonEncoder(config);
    }

    this.encoder = encoder;
    return encoder;
  }

  /**
   * Create a default encoder that is specified by the factory instantiation.
   *
   * @return a new created object of the default encoder.
   */
  public EncoderBase getDefaultEncoderObject() {
    return getEncoderObject(this.defaultEncoder);
  }

  /**
   * Return the encoder object to the existing Pool
   *
   * @param encoder IEncoder that should be returned to the pool.
   */
  public void returnEncoderToPool(EncoderBase encoder) {
    //TODO implementation
  }

  /**
   * Enumeration of different encoder that are used within the system.
   */
  public enum EEncoderType {
    JSON
  }
}
