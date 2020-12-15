//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for Lablink services.
 */
public abstract class LlServiceBase implements Cloneable {

  protected static final Logger logger = LogManager.getLogger("LlServiceBase");

  /** The name of the service. */
  protected String name;

  /** The read-only flag. */
  protected boolean readOnly = false;

  /**
   * Instantiates a new instance with random alpha-numeric 
   * name and read-only flag set to {@code false}.
   */
  public LlServiceBase() {
    this(RandomStringUtils.randomAlphabetic(10), false);
  }

  /**
   * Instantiates a new instance.
   *
   * @param name service name
   * @param readonly read-only flag
   */
  public LlServiceBase(String name, boolean readonly) {
    this.setName(name);
    this.setReadOnly(readonly);
    logger.debug("Service [{}] created with access [{}].", name,
        (readonly ? "READONLY" : "READ/WRITE"));
  }

  /**
   * Instantiates a new instance with read-only flag set to {@code false}.
   *
   * @param name service name
   */
  public LlServiceBase(String name) {
    this(name, false);
  }

  /**
   * Instantiates a new instance with random alpha-numeric name.
   *
   * @param readonly read-only flag
   */
  public LlServiceBase(boolean readonly) {
    this(RandomStringUtils.randomAlphabetic(10), readonly);
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
   * Checks if is read-only.
   *
   * @return read-only flag
   */
  public boolean isReadOnly() {
    return readOnly;
  }

  /**
   * Sets the read-only falg.
   *
   * @param readOnly read-only flag
   */
  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  /** The properties. */
  protected Map<ELlServiceProperties, String> properties =
      new HashMap<ELlServiceProperties, String>();

  /**
   * Gets the collection of properties stored.
   *
   * @return the properties
   */
  public Map<ELlServiceProperties, String> getProperties() {
    return properties;
  }

  /**
   * Sets the properties as a complete collection.
   *
   * @param properties the properties
   */
  public void setProperties(Map<ELlServiceProperties, String> properties) {
    this.properties = properties;
  }

  /**
   * Adds the property.
   *
   * @param key the key
   * @param val the val
   */
  public void addProperty(ELlServiceProperties key, String val) {
    this.properties.put(key, val);
    logger.debug("Property [{}] updated with value [{}] for service [{}]", key, val, this.name);
  }

  /**
   * Gets the property.
   *
   * @param key the key
   * @return the property
   */
  public String getProperty(ELlServiceProperties key) {
    return this.properties.get(key);
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public Object duplicate() throws CloneNotSupportedException {
    LlServiceBase base = (LlServiceBase) super.clone();
    return base;
  }
}
