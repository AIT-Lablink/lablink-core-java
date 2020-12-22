//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.impl;

import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactoryManager;
import at.ac.ait.lablink.core.connection.ex.LlCoreDecoderRuntimeException;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a factory manager for encodable objects.
 *
 * <p>The class allows the generation of encodable objects that are registered to the manager.
 * The registration process uses introspection to get some static methods of the encodable objects.
 * These introspection is only used during the registration process. During the creation of new
 * encodable objects the introspection isn't used to improve the performance of the time critical
 * section.
 */
public class EncodableFactoryManagerImpl implements IEncodableFactoryManager {

  private static final Logger logger = LoggerFactory.getLogger(EncodableFactoryManagerImpl.class);

  /* Map that stores the registered factories of the encodable objects */
  private final Map<String, IEncodableFactory>
      encodableStore =
      new HashMap<String, IEncodableFactory>();

  /**
   * Factory method to create an encodable object.
   *
   * @param type Type string that is used as index for the registered factory.
   * @return a created and empty encodable object.
   */
  @Override
  public IEncodable createEncodable(String type) {
    IEncodableFactory encodableFactory = encodableStore.get(type);
    if (encodableFactory == null) {
      logger.warn("Can't find factory for IEncodable type '{}'", type);
      throw new LlCoreDecoderRuntimeException(
          "Can't find factory for IEncodable type '" + type + "'");
    }
    return encodableFactory.createEncodableObject();
  }

  @Override
  public void registerEncodableFactory(String type, IEncodableFactory encodableFactory) {

    if (encodableStore.containsKey(type)) {

      IEncodable alreadyRegisteredClass = encodableStore.get(type).createEncodableObject();
      IEncodable newRegisteredClass = encodableFactory.createEncodableObject();

      if (!alreadyRegisteredClass.getClass().equals(newRegisteredClass.getClass())) {
        throw new LlCoreRuntimeException("Key " + type + " already exists in FactoryManager.");
      }

      // A factory for the given encodable class is already registered.
      // Overwrite it with the new factory method.
    }
    encodableStore.put(type, encodableFactory);
    logger.debug("IEncodableFactory for class '{}' registered at '{}'.",
        encodableFactory.createEncodableObject().getClass().getSimpleName(), type);
  }

  @Override
  public void registerEncodableFactory(String type, Class<? extends IEncodable> encodableClass) {
    IEncodableFactory factory = extractFactory(encodableClass);
    registerEncodableFactory(type, factory);
  }

  @Override
  public void registerEncodableFactory(Class<? extends IEncodable> encodableClass) {
    String key = extractKey(encodableClass);
    IEncodableFactory factory = extractFactory(encodableClass);
    registerEncodableFactory(key, factory);
  }

  @Override
  public void unregisterEncodableFactory(String type) {
    encodableStore.remove(type);
  }

  @Override
  public void unregisterEncodableFactory(Class<? extends IEncodable> encodableClass) {
    String key = extractKey(encodableClass);
    unregisterEncodableFactory(key);
  }

  /**
   * Private helper method to extract the type string of an encodable class using introspection.
   *
   * <p>Therefore every encodable class must have a static <code>getClassType</code> method that
   * returns the unique identifier string of the class.
   *
   * @param encodableClass where the identifier string should be read.
   * @return The read identifier string.
   */
  private String extractKey(Class<? extends IEncodable> encodableClass) {
    try {
      Method method = encodableClass.getMethod("getClassType");
      return (String) method.invoke(null);

    } catch (NoSuchMethodException ex) {
      throw new LlCoreRuntimeException("The type string of an encodable class can't be "
          + "extracted. Maybe the 'getClassType'-method is missing.", ex);
    } catch (IllegalAccessException ex) {
      throw new LlCoreRuntimeException("The type string of an encodable class can't be "
          + "extracted. Maybe the 'getClassType'-method is missing.", ex);
    } catch (InvocationTargetException ex) {
      throw new LlCoreRuntimeException("The type string of an encodable class can't be "
          + "extracted. Maybe the 'getClassType'-method is missing.", ex);
    }
  }

  /**
   * Private helper method to extract the factory object of an encodable class using introspection.
   *
   * <p>Therefore every encodable class must have a static <code>getEncodableFactory</code>
   * method that returns the factory object of the class.
   *
   * @param encodableClass where the factory object should be extracted.
   * @return IEncodable factory object of the given class.
   */
  private IEncodableFactory extractFactory(Class<? extends IEncodable> encodableClass) {
    try {
      Method method = encodableClass.getMethod("getEncodableFactory");
      return (IEncodableFactory) method.invoke(null);

    } catch (NoSuchMethodException ex) {
      throw new LlCoreRuntimeException("The factory class of an encodable class can't be "
          + "extracted. Maybe the 'createEncodableObject'-method is missing.", ex);
    } catch (IllegalAccessException ex) {
      throw new LlCoreRuntimeException("The factory class of an encodable class can't be "
          + "extracted. Maybe the 'createEncodableObject'-method is missing.", ex);
    } catch (InvocationTargetException ex) {
      throw new LlCoreRuntimeException("The factory class of an encodable class can't be "
          + "extracted. Maybe the 'createEncodableObject'-method is missing.", ex);
    }
  }

  Map<String, IEncodableFactory> getEncodableStore() {
    return encodableStore;
  }
}
