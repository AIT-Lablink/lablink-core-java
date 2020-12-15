//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.impl;

import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.connection.encoding.IEncodeableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncodeableFactoryManager;
import at.ac.ait.lablink.core.connection.ex.LlCoreDecoderRuntimeException;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a factory manager for encodeable objects.
 *
 * <p>The class allows the generation of encodeable objects that are registered to the manager.
 * The registration process uses introspection to get some static methods of the encodeable objects.
 * These introspection is only used during the registration process. During the creation of new
 * encodeable objects the introspection isn't used to improve the performance of the time critical
 * section.
 */
public class EncodeableFactoryManagerImpl implements IEncodeableFactoryManager {

  private static final Logger logger = LoggerFactory.getLogger(EncodeableFactoryManagerImpl.class);

  /* Map that stores the registered factories of the encodeable objects */
  private final Map<String, IEncodeableFactory>
      encodeableStore =
      new HashMap<String, IEncodeableFactory>();

  /**
   * Factory method to create an encodeable object.
   *
   * @param type Type string that is used as index for the registered factory.
   * @return a created and empty encodeable object.
   */
  @Override
  public IEncodeable createEncodeable(String type) {
    IEncodeableFactory encodeableFactory = encodeableStore.get(type);
    if (encodeableFactory == null) {
      logger.warn("Can't find factory for IEncodeable type '{}'", type);
      throw new LlCoreDecoderRuntimeException(
          "Can't find factory for IEncodeable type '" + type + "'");
    }
    return encodeableFactory.createEncodeableObject();
  }

  @Override
  public void registerEncodeableFactory(String type, IEncodeableFactory encodeableFactory) {

    if (encodeableStore.containsKey(type)) {

      IEncodeable alreadyRegisteredClass = encodeableStore.get(type).createEncodeableObject();
      IEncodeable newRegisteredClass = encodeableFactory.createEncodeableObject();

      if (!alreadyRegisteredClass.getClass().equals(newRegisteredClass.getClass())) {
        throw new LlCoreRuntimeException("Key " + type + " already exists in FactoryManager.");
      }

      // A factory for the given encodeable class is already registered.
      // Overwrite it with the new factory method.
    }
    encodeableStore.put(type, encodeableFactory);
    logger.debug("IEncodeableFactory for class '{}' registered at '{}'.",
        encodeableFactory.createEncodeableObject().getClass().getSimpleName(), type);
  }

  @Override
  public void registerEncodeableFactory(String type, Class<? extends IEncodeable> encodeableClass) {
    IEncodeableFactory factory = extractFactory(encodeableClass);
    registerEncodeableFactory(type, factory);
  }

  @Override
  public void registerEncodeableFactory(Class<? extends IEncodeable> encodeableClass) {
    String key = extractKey(encodeableClass);
    IEncodeableFactory factory = extractFactory(encodeableClass);
    registerEncodeableFactory(key, factory);
  }

  @Override
  public void unregisterEncodeableFactory(String type) {
    encodeableStore.remove(type);
  }

  @Override
  public void unregisterEncodeableFactory(Class<? extends IEncodeable> encodeableClass) {
    String key = extractKey(encodeableClass);
    unregisterEncodeableFactory(key);
  }

  /**
   * Private helper method to extract the type string of an encodeable class using introspection.
   *
   * <p>Therefore every encodeable class must have a static <code>getClassType</code> method that
   * returns the unique identifier string of the class.
   *
   * @param encodeableClass where the identifier string should be read.
   * @return The read identifier string.
   */
  private String extractKey(Class<? extends IEncodeable> encodeableClass) {
    try {
      Method method = encodeableClass.getMethod("getClassType");
      return (String) method.invoke(null);

    } catch (NoSuchMethodException ex) {
      throw new LlCoreRuntimeException("The type string of an encodeable class can't be "
          + "extracted. Maybe the 'getClassType'-method is missing.", ex);
    } catch (IllegalAccessException ex) {
      throw new LlCoreRuntimeException("The type string of an encodeable class can't be "
          + "extracted. Maybe the 'getClassType'-method is missing.", ex);
    } catch (InvocationTargetException ex) {
      throw new LlCoreRuntimeException("The type string of an encodeable class can't be "
          + "extracted. Maybe the 'getClassType'-method is missing.", ex);
    }
  }

  /**
   * Private helper method to extract the factory object of an encodeable class using introspection.
   *
   * <p>Therefore every encodeable class must have a static <code>getEncodeableFactory</code>
   * method that returns the factory object of the class.
   *
   * @param encodeableClass where the factory object should be extracted.
   * @return IEncodeable factory object of the given class.
   */
  private IEncodeableFactory extractFactory(Class<? extends IEncodeable> encodeableClass) {
    try {
      Method method = encodeableClass.getMethod("getEncodeableFactory");
      return (IEncodeableFactory) method.invoke(null);

    } catch (NoSuchMethodException ex) {
      throw new LlCoreRuntimeException("The factory class of an encodeable class can't be "
          + "extracted. Maybe the 'createEncodeableObject'-method is missing.", ex);
    } catch (IllegalAccessException ex) {
      throw new LlCoreRuntimeException("The factory class of an encodeable class can't be "
          + "extracted. Maybe the 'createEncodeableObject'-method is missing.", ex);
    } catch (InvocationTargetException ex) {
      throw new LlCoreRuntimeException("The factory class of an encodeable class can't be "
          + "extracted. Maybe the 'createEncodeableObject'-method is missing.", ex);
    }
  }

  Map<String, IEncodeableFactory> getEncodeableStore() {
    return encodeableStore;
  }
}
