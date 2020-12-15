//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ci.mqtt;

/**
 * MQTT yellow pages for data points.
 */
public class MqttYellowPageForDataPoint {

  /** The name. */
  private String name;

  /** The identifier. */
  private String identifier;

  /** The description. */
  private String description;

  /** The unit. */
  private String unit;

  /** The readonly. */
  private boolean readonly;

  /** The datatype. */
  private String datatype;

  /**
   * Instantiates a new mqtt yellow page for data point.
   */
  public MqttYellowPageForDataPoint() {

  }

  /**
   * Instantiates a new mqtt yellow pages data point.
   *
   * @param name the name
   * @param id the id
   * @param desc the desc
   * @param unit the unit
   * @param ronly the ronly
   * @param datatype the datatype
   */
  public MqttYellowPageForDataPoint(String name, String id, String desc, String unit, boolean ronly,
      String datatype) {
    setName(name);
    setIdentifier(id);
    setDescription(desc);
    setUnit(unit);
    setReadonly(ronly);
    setDatatype(datatype);
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }



  /**
   * Gets the identifier.
   *
   * @return the identifier
   */
  public String getIdentifier() {
    return identifier;
  }



  /**
   * Sets the identifier.
   *
   * @param identifier the identifier to set
   */
  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }



  /**
   * Gets the description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }



  /**
   * Sets the description.
   *
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }



  /**
   * Gets the unit.
   *
   * @return the unit
   */
  public String getUnit() {
    return unit;
  }



  /**
   * Sets the unit.
   *
   * @param unit the unit to set
   */
  public void setUnit(String unit) {
    this.unit = unit;
  }

  /**
   * Checks if is readonly.
   *
   * @return the readonly
   */
  public boolean isReadonly() {
    return readonly;
  }

  /**
   * Sets the readonly.
   *
   * @param readonly the readonly to set
   */
  public void setReadonly(boolean readonly) {
    this.readonly = readonly;
  }

  /**
   * Gets the datatype.
   *
   * @return the datatype
   */
  public String getDatatype() {
    return datatype;
  }

  /**
   * Sets the datatype.
   *
   * @param datatype the datatype to set
   */
  public void setDatatype(String datatype) {
    this.datatype = datatype;
  }

}
