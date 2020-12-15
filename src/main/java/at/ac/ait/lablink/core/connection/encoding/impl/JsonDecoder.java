//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.impl;

import at.ac.ait.lablink.core.connection.encoding.DecoderBase;
import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.connection.ex.LlCoreDecoderRuntimeException;
import at.ac.ait.lablink.core.connection.ex.LlCoreEncoderRuntimeException;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Implementation of a Json IDecoder.
 *
 * <p>The Json decoder will decode a JSON string into an IEncodeable Java representation.
 */
public class JsonDecoder extends DecoderBase {

  private static final Logger logger = LoggerFactory.getLogger(JsonDecoder.class);

  /** Stack for handling recursion of IEncodeable objects. **/
  private final Deque<JsonValue> decoderStack = new ArrayDeque<JsonValue>();

  /** Top level element of the the decoded packet. **/
  private IEncodeable firstElement;

  private final int defaultMaxStackSize = 200;

  /** Maximum allowed stack size. **/
  private int maxStackSize = defaultMaxStackSize;

  /**
   * Default Constructor.
   */
  public JsonDecoder() {
    this(null);
  }

  /**
   * Constructor with optional configuration object.
   *
   * <p>The JsonDecoder can be configured with a <code>Configuration</code> object. This object
   * can be memory based or it can be loaded from a resources/properties file. The configuration
   * will only be updated or taken during the creation of the object.<br>
   * The following list shows the current implemented configuration properties withs their default
   * values (between brackets):
   * <ul>
   *
   * <li><b>encoding.maxStackSize</b> (200, int): Maximum allowed size of decoder stack. The
   * stack will be used for the creation of nested {@link IEncodeable} objects or for lists of
   * {@link IEncodeable} objects.</li>
   * </ul>
   *
   * @param config Configuration object that is used to parametrize the JsonDecoder.
   *               Different parameters can be set. If no parameter is set, the encoder
   *               will use the default settings (see also {JsonEncoder}).
   */
  public JsonDecoder(Configuration config) {

    if (config == null) {
      logger.info("No configuration is set for JsonDecoder. Use default configuration.");
      config = new BaseConfiguration();
    }

    maxStackSize = config.getInt("encoding.maxStackSize", defaultMaxStackSize);
    logger.info("IEncoder: MaxStackSize: {}", maxStackSize);
  }

  @Override
  protected IEncodeable getDecodedElement() {

    if (decoderStack.size() > 0) {
      throw new LlCoreDecoderRuntimeException("Stack size (" + decoderStack.size() + ") > 0. "
          + "Decoding hasn't finished and is stuck in a nested object or you read a decoded "
          + "object until a new decoding has been started.");
    }
    return firstElement;
  }

  @Override
  protected void decodeElement(byte[] source) {

    initDecoder(source);

    JsonValue value = decoderStack.getFirst();

    if (!value.isObject()) {
      throw new LlCoreDecoderRuntimeException(
          "Top-level element can't be decoded. The element" + "isn't a JSON object.");
    }

    String objectType = ((JsonObject) value).get("$type").asString();
    firstElement = encodeableFactoryManager.createEncodeable(objectType);

    firstElement.decode(this);
    firstElement.decodingCompleted();
    decoderStack.removeFirst();
  }

  /**
   * Initialize the decoder and parse the Json String.
   *
   * @param source Json String to be parsed
   */
  void initDecoder(byte[] source) {
    firstElement = null;
    decoderStack.clear();

    try {
      JsonValue value = Json.parse(new String(source));
      decoderStack.addFirst(value);
    } catch (ParseException ex) {
      throw new LlCoreDecoderRuntimeException("Error during reading the JSON string.", ex);
    }
  }

  private JsonValue getLastJsonValue(String key) {
    JsonValue value = decoderStack.getFirst();

    if (!value.isObject()) {
      throw new LlCoreDecoderRuntimeException(
          "Json element can't be decoded. The element" + "isn't a JSON object.");
    }
    return ((JsonObject) value).get(key);
  }

  @Override
  public String getString(String key) {
    JsonValue value = getLastJsonValue(key);
    if (value.isNull()) {
      logger.warn("IEncodeable Element '{}' is null ({})", key, value);
      return "";
    } else {
      return value.asString();
    }
  }

  @Override
  public List<String> getStrings(String key) {
    JsonValue value = getLastJsonValue(key);
    if (value.isNull()) {
      logger.warn("IEncodeable Element '{}' is null ({})", key, value);
      return Collections.emptyList();
    }

    JsonArray array = value.asArray();
    List<String> retList = new ArrayList<String>();

    for (JsonValue val : array) {
      if (val.isNull()) {
        logger.warn("IEncodeable Element  in list '{}' is null ({})", key, array);
        retList.add("");
      } else {
        retList.add(val.asString());
      }
    }
    return retList;
  }

  @Override
  public float getFloat(String key) {
    JsonValue value = getLastJsonValue(key);
    if (value.isNull()) {
      logger.warn("IEncodeable Element '{}' is null ({})", key, value);
      return 0.0f;
    } else {
      return value.asFloat();
    }
  }

  @Override
  public double getDouble(String key) {
    JsonValue value = getLastJsonValue(key);
    if (value.isNull()) {
      logger.warn("IEncodeable Element '{}' is null ({})", key, value);
      return 0.0;
    } else {
      return value.asDouble();
    }
  }

  @Override
  public boolean getBoolean(String key) {
    JsonValue value = getLastJsonValue(key);
    if (value.isNull()) {
      logger.warn("IEncodeable Element '{}' is null ({})", key, value);
      return false;
    } else {
      return value.asBoolean();
    }
  }

  @Override
  public int getInt(String key) {
    JsonValue value = getLastJsonValue(key);
    if (value.isNull()) {
      logger.warn("IEncodeable Element '{}' is null ({})", key, value);
      return 0;
    } else {
      return value.asInt();
    }
  }

  @Override
  public long getLong(String key) {
    JsonValue value = getLastJsonValue(key);
    if (value.isNull()) {
      logger.warn("IEncodeable Element '{}' is null ({})", key, value);
      return 0;
    } else {
      return value.asLong();
    }
  }

  @Override
  public byte[] getBlob(String key) {
    JsonValue value = getLastJsonValue(key);
    if (value.isNull()) {
      logger.warn("IEncodeable Element '{}' is null ({})", key, value);
      return new byte[0];
    } else {
      String str = getLastJsonValue(key).asString();
      return Base64.decodeBase64(str);
    }
  }

  @Override
  public IEncodeable getEncodeable(String key) {
    JsonObject object = getLastJsonValue(key).asObject();

    String objectType = object.get("$type").asString();
    IEncodeable element = encodeableFactoryManager.createEncodeable(objectType);

    checkStackSize();
    decoderStack.addFirst(object);
    element.decode(this);
    element.decodingCompleted();
    decoderStack.removeFirst();
    return element;
  }

  @Override
  public List<? extends IEncodeable> getEncodeables(String key) {
    JsonValue value = getLastJsonValue(key);
    if (value.isNull()) {
      logger.warn("IEncodeable Element '{}' is null ({})", key, value);
      return Collections.emptyList();
    }

    JsonArray array = value.asArray();

    List<IEncodeable> retList = new ArrayList<IEncodeable>();

    for (JsonValue val : array) {
      if (!val.isObject()) {
        throw new LlCoreDecoderRuntimeException(
            "IEncodeable can't be decoded. " + "The element isn't a JSON object.");
      }
      if (val.isNull()) {
        logger.warn("IEncodeable Element  in list '{}' is null ({})", key, array);
      } else {
        JsonObject object = (JsonObject) val;

        String objectType = object.get("$type").asString();
        IEncodeable element = encodeableFactoryManager.createEncodeable(objectType);

        checkStackSize();
        decoderStack.addFirst(object);
        element.decode(this);
        element.decodingCompleted();
        decoderStack.removeFirst();
        retList.add(element);
      }
    }
    return retList;
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
    if (decoderStack.size() > this.maxStackSize) {
      throw new LlCoreEncoderRuntimeException("Maximum encoder stack size exceeded. "
          + "Maybe there is a recursion in the object to be encoded.");
    }
  }

  int getMaxStackSize() {
    return maxStackSize;
  }
}
