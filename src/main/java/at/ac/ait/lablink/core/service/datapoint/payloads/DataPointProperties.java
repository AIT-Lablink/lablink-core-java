//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.payloads;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.connection.encoding.encodables.PayloadBase;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import java.util.List;

/**
 * IDataPoint Properties data class.
 *
 * <p>This class contains properties and attributes for a datapoint
 */
public class DataPointProperties extends PayloadBase {

  /**
   * Get a datapointType string of the class.
   *
   * <p><b>This static method must be implemented by every subclass.</b>
   *
   * <p>Every class that is encodable and is used by a decoder must have a unique string that
   * identifies this class. This datapointType string will be transmitted during the
   * communication and will
   * be used by a decoder for creating an empty object of the encodable class.
   *
   * @return an unique datapointType string of the class
   */
  public static String getClassType() {
    return "datapointProperties";
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
        return new DataPointProperties();
      }
    };
  }

  /**
   * Identifier of the datapoint.
   */
  private List<String> identifier;

  /**
   * Optional friendly name of the datapoint.
   */
  private String name = "";

  /**
   * Optional unit of the datapoint.
   */
  private String unit = "";

  /**
   * Flag if the datapoint is writeable.
   */
  private boolean writeable;


  /**
   * Class type of the datapoint value.
   */
  private Class datapointType = Object.class;

  /**
   * Default constructor.
   */
  public DataPointProperties() {
  }

  /**
   * Constructor.
   *
   * @param identifier Identifier of the datapoint.
   * @param name       Friendly name of the datapoint.
   * @param unit       Unit of the datapoint.
   * @param writeable  Set the writeable flag
   * @param type       Set the type of the class.
   */
  public DataPointProperties(List<String> identifier, String name, String unit, boolean writeable,
                             Class type) {
    this.identifier = identifier;
    this.name = name;
    this.unit = unit;
    this.writeable = writeable;
    this.datapointType = type;
  }

  @Override
  public void encode(IEncoder encoder) {
    encoder.putStringList("id", identifier);
    encoder.putString("name", name);
    encoder.putString("unit", unit);
    encoder.putBoolean("writeable", writeable);
    encoder.putString("datapointType", datapointType.getName());
  }

  @Override
  public void decode(IDecoder decoder) {
    identifier = decoder.getStrings("id");
    name = decoder.getString("name");
    unit = decoder.getString("unit");
    writeable = decoder.getBoolean("writeable");
    try {
      datapointType = Class.forName(decoder.getString("datapointType"));
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public String getType() {
    return DataPointProperties.getClassType();
  }

  @Override
  public void decodingCompleted() {

  }

  @Override
  public void validate() {

    if (this.identifier == null || this.identifier.isEmpty()) {
      throw new LlCoreRuntimeException("Identifier is null or empty.");
    }
    if (this.datapointType == null) {
      throw new LlCoreRuntimeException("Type is null or empty.");
    }
  }


  /**
   * Get the identifier of the datapoint.
   *
   * @return identifier
   */
  public List<String> getIdentifier() {
    return identifier;
  }

  /**
   * Get the friendly name of the datapoint.
   *
   * @return name of the datapoint
   */
  public String getName() {
    return name;
  }

  /**
   * Get the unit of the datapoint.
   *
   * @return unit.
   */
  public String getUnit() {
    return unit;
  }

  /**
   * Check if the writeable flag is set.
   *
   * @return the writeable flag
   */
  public boolean isWriteable() {
    return writeable;
  }

  /**
   * Get the data type of the value.
   *
   * @return value's datatype
   */
  public Class getDatapointType() {
    return datapointType;
  }

  @Override
  public String toString() {
    return "DataPointProperties{" + "identifier=" + identifier + ", name='" + name + '\''
        + ", unit='" + unit + '\'' + ", writeable=" + writeable + ", datapointType=" + datapointType
        + '}';
  }
}
