//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.impl;

import at.ac.ait.lablink.core.connection.encoding.EncoderBase;
import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.connection.ex.LlCoreEncoderRuntimeException;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.WriterConfig;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Specific JSON encoder.
 *
 * <p>IEncoder that implements the encodes the internal Lablink encodeables objects into a
 * JSON string representation.
 */
public class JsonEncoder extends EncoderBase {

  private static final Logger logger = LoggerFactory.getLogger(JsonEncoder.class);

  /* stack for nested encoding objects */
  private final Deque<JsonValue> encoderStack = new ArrayDeque<JsonValue>();

  private final int defaultMaxStackSize = 200;

  /* maximum allowed stack size*/
  private int maxStackSize = defaultMaxStackSize;

  /**
   * Default Constructor.
   */
  public JsonEncoder() {
    this(null);
  }

  /**
   * Constructor with optional configuration object.
   *
   * <p>The JsonEncoder can be configured with a <code>Configuration</code> object. This object
   * can be memory based or it can be loaded from a resources/properties file. The configuration
   * will only be updated or taken during the creation of the object.<br>
   * The following list shows the current implemented configuration properties withs their default
   * values (between brackets):
   * <ul>
   *
   * <li><b>encoding.maxStackSize</b> (200, int): Maximum allowed size of encoder stack. The
   * stack will be used for the creation of nested {@link IEncodeable} objects or for lists of
   * {@link IEncodeable} objects.</li>
   * </ul>
   *
   * @param config Configuration object that is used to parametrize the JsonEncoder.
   *               Different parameters can be set. If no parameter is set, the encoder
   *               will use the default settings.
   */
  public JsonEncoder(Configuration config) {

    if (config == null) {
      logger.info("No configuration is set for JsonEncoder. Use default configuration.");
      config = new BaseConfiguration();
    }

    maxStackSize = config.getInt("encoding.maxStackSize", defaultMaxStackSize);
    logger.info("IEncoder: MaxStackSize: {}", maxStackSize);
  }

  @Override
  protected void encodeElement(IEncodeable value) {
    logger.trace("Start encoding a object with {}", value);

    initEncoder();

    JsonObject baseObject = (JsonObject) encoderStack.getFirst();
    checkValueIsNull(value.getType());
    baseObject.add("$type", value.getType());
    value.encode(this);
  }

  /**
   * Initialise the encoder.
   */
  void initEncoder() {

    logger.trace("Init encoder.");
    encoderStack.clear();
    JsonObject baseObject = Json.object();
    encoderStack.addFirst(baseObject);
  }

  @Override
  public byte[] getEncoded() {

    if (encoderStack.size() > 1) {
      throw new LlCoreEncoderRuntimeException("Stack size (" + encoderStack.size() + ") > 1. "
          + "Encoding hasn't finished and is stuck in a nested object or you read an encoded "
          + "object until a new encoding has been started.");
    }

    JsonValue baseObject = encoderStack.getFirst();
    return baseObject.toString(WriterConfig.MINIMAL).getBytes();
  }

  /**
   * Return the encoded element as string.
   *
   * @return string representation of an encoded element
   */
  String getEncodedString() {
    return new String(getEncoded());
  }

  @Override
  public void putString(String key, String value) {

    logger.trace("Add string to JSON encoder: {} ({})",key, value);
    checkJsonObjectKey(key);
    checkValueIsNull(value);
    JsonObject actEncoderObject = (JsonObject) encoderStack.getFirst();
    actEncoderObject.add(key, value);
  }

  @Override
  public void putStringList(String key, List<String> values) {
    logger.trace("Add string list to JSON encoder: {} ({})",key, values);
    checkJsonObjectKey(key);
    checkValueIsNull(values);
    JsonObject actEncoderObject = (JsonObject) encoderStack.getFirst();
    JsonArray jsonArray = Json.array(values.toArray(new String[0]));
    actEncoderObject.add(key, jsonArray);
  }

  @Override
  public void putFloat(String key, float value) {
    logger.trace("Add float to JSON encoder: {} ({})",key, value);
    checkJsonObjectKey(key);
    checkValueIsNull(value);
    JsonObject actEncoderObject = (JsonObject) encoderStack.getFirst();
    actEncoderObject.add(key, value);
  }

  @Override
  public void putDouble(String key, double value) {

    logger.trace("Add double to JSON encoder: {} ({})",key, value);
    checkJsonObjectKey(key);
    checkValueIsNull(value);
    JsonObject actEncoderObject = (JsonObject) encoderStack.getFirst();
    actEncoderObject.add(key, value);
  }

  @Override
  public void putBoolean(String key, boolean value) {
    logger.trace("Add boolean to JSON encoder: {} ({})",key, value);
    checkJsonObjectKey(key);
    checkValueIsNull(value);
    JsonObject actEncoderObject = (JsonObject) encoderStack.getFirst();
    actEncoderObject.add(key, value);
  }

  @Override
  public void putInt(String key, int value) {
    logger.trace("Add int to JSON encoder: {} ({})",key, value);
    checkJsonObjectKey(key);
    checkValueIsNull(value);
    JsonObject actEncoderObject = (JsonObject) encoderStack.getFirst();
    actEncoderObject.add(key, value);
  }


  @Override
  public void putLong(String key, long value) {
    logger.trace("Add long to JSON encoder: {} ({})",key, value);
    checkJsonObjectKey(key);
    checkValueIsNull(value);
    JsonObject actEncoderObject = (JsonObject) encoderStack.getFirst();
    actEncoderObject.add(key, value);
  }

  @Override
  public void putBlob(String key, byte[] value) {
    logger.trace("Add blob to JSON encoder: {}",key);
    checkJsonObjectKey(key);
    checkValueIsNull(value);
    JsonObject actEncoderObject = (JsonObject) encoderStack.getFirst();

    String blob = Base64.encodeBase64String(value);
    actEncoderObject.add(key, blob);
  }

  @Override
  public void putEncodeable(String key, IEncodeable value) {

    logger.trace("Add object to JSON encoder: {} ({})",key, value);
    checkJsonObjectKey(key);
    checkValueIsNull(value);
    checkStackSize();

    JsonObject actEncoderObject = Json.object();
    actEncoderObject.add("$type", value.getType());
    encoderStack.addFirst(actEncoderObject);
    value.encode(this);
    encoderStack.removeFirst();
    JsonObject parentEncoderObject = (JsonObject) encoderStack.getFirst();
    parentEncoderObject.add(key, actEncoderObject);
  }

  @Override
  public void putEncodeableList(String key, List<? extends IEncodeable> values) {

    logger.trace("Add list to JSON encoder: {} ({})",key, values);
    checkJsonObjectKey(key);
    checkValueIsNull(values);
    checkStackSize();

    JsonArray actEncoderObject = (JsonArray) Json.array();
    encoderStack.addFirst(actEncoderObject);
    this.addArray(values);
    encoderStack.removeFirst();
    JsonObject parentEncoderObject = (JsonObject) encoderStack.getFirst();
    parentEncoderObject.add(key, actEncoderObject);
  }

  /**
   * Add an array to the last Json object in the stack.
   *
   * @param values array to be added
   * @throws LlCoreEncoderRuntimeException if the maximum stack size exceeds. Therefore
   *                                            decrease your array list of {@link IEncodeable}
   *                                            objects or overwrite the default stack size
   *                                            value.
   */
  private void addArray(List<? extends IEncodeable> values) {
    for (IEncodeable value : values) {
      checkStackSize();
      checkValueIsNull(value);
      JsonObject actEncoderObject = Json.object();
      actEncoderObject.add("$type", value.getType());
      encoderStack.addFirst(actEncoderObject);
      value.encode(this);
      encoderStack.removeFirst();
      JsonArray parentEncoderObject = (JsonArray) encoderStack.getFirst();
      parentEncoderObject.add(actEncoderObject);
    }
  }

  /**
   * Checks if a key is valid or if a key already exists in the last Json object in the stack.
   *
   * @param key to be checked
   * @throws LlCoreEncoderRuntimeException if the key already exists or if the key
   *     contains disallowed characters
   */
  private void checkJsonObjectKey(String key) {
    validateKeyString(key);
    checkExistingKey(key);
  }

  /**
   * Validate key string for allowed values.
   *
   * @param key to be validated
   * @throws LlCoreEncoderRuntimeException if the key contains disallowed characters
   */
  private void validateKeyString(String key) {

    if (key.startsWith("$")) {
      throw new LlCoreEncoderRuntimeException("Key " + key + " starts with a $. "
          + "Keys with starting $ are reserved for internal management.");
    }
  }

  /**
   * Checks if a key already exists in the last Json object in the stack
   *
   * @param key to be checked
   * @throws LlCoreEncoderRuntimeException if the key already exists.
   */
  private void checkExistingKey(String key) {
    JsonObject actEncoderObject = (JsonObject) encoderStack.getFirst();
    if (actEncoderObject.names().contains(key)) {
      throw new LlCoreEncoderRuntimeException(
          "Key " + key + " already available in IEncoder object");
    }
  }

  private void checkValueIsNull(Object value) {
    if (value == null) {
      throw new LlCoreEncoderRuntimeException(
          "Given value in JsonEncoder is null. Abort encoding.");
    }
  }

  /**
   * Check if the maximum stack size is exceeded.
   *
   * <p>Every encoded object is stored in the stack. A recursion in the encoded object can exceed
   * the maximum stack size. Also a high number of nested encodeable objects can cause the reaching
   * of the maximum stack size. Therefore increase the default value of the allowed stack size or
   * decrease your nested objects.
   *
   * @throws LlCoreEncoderRuntimeException if the maximum stack size is reached.
   */
  private void checkStackSize() {
    if (encoderStack.size() > this.maxStackSize) {
      throw new LlCoreEncoderRuntimeException("Maximum encoder stack size exceeded. "
          + "Maybe there is a recursion in the object to be encoded.");
    }
  }

  int getMaxStackSize() {
    return maxStackSize;
  }
}
