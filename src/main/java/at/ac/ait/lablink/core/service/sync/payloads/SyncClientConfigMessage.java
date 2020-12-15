//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync.payloads;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.connection.encoding.IEncodeableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.connection.encoding.encodeables.PayloadBase;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

/**
 * An individual configuration message for a specific sync client.
 */
public class SyncClientConfigMessage extends PayloadBase {

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
    return "SyncClientConfigMessage";
  }

  /**
   * Get the factory to create objects of the class.
   *
   * <p><b>This static method must be implemented by every subclass.</b>
   *
   * <p>Every class that is encodeable and is used by a decoder must have a unique factory object to
   * create empty objects of the class. This factory method will be used by the decoder to create a
   * fresh object that can be filled in with the decoded values.
   *
   * @return A factory object for creating encodeable classes
   */
  public static IEncodeableFactory getEncodeableFactory() {
    return new IEncodeableFactory() {
      @Override
      public IEncodeable createEncodeableObject() {
        return new SyncClientConfigMessage();
      }
    };
  }

  private String clientSpecificConfig;

  public SyncClientConfigMessage() {
  }

  public SyncClientConfigMessage(String clientSpecificConfig) {
    this.clientSpecificConfig = clientSpecificConfig;
  }

  @Override
  public void encode(IEncoder encoder) {
    encoder.putString("clientConfig", clientSpecificConfig);
  }

  @Override
  public void decode(IDecoder decoder) {
    clientSpecificConfig = decoder.getString("clientConfig");
  }

  @Override
  public String getType() {
    return SyncClientConfigMessage.getClassType();
  }

  @Override
  public void decodingCompleted() {
    // expected
  }

  @Override
  public void validate() {

    if (this.clientSpecificConfig == null || this.clientSpecificConfig.isEmpty()) {
      throw new LlCoreRuntimeException(
          "ClientSpecificConfig in SyncClientConfigMessage is null or empty.");
    }
  }


  @Override
  public String toString() {
    return "SyncClientConfigMessage{" + " clientSpecificConfig='" + clientSpecificConfig + '\''
        + '}';
  }

  public JsonObject getClientConfig() {
    //TODO throw error if null
    return Json.parse(clientSpecificConfig).asObject();
  }

}
