//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding;

/**
 * Interface to a factory manager for encodeable objects.
 *
 * <p>The interface allows the registration of encodeable objects to a factory manager. This
 * manager will be used to create new encodeables (mainly during decoding)
 */
public interface IEncodeableFactoryManager {

  /**
   * Register a factory for encodeable objects to the manager.
   *
   * @param type Type string that is used as index for the registered factory.
   * @param encodeableFactory Factory for a specific encodeable object to be registered.
   */
  void registerEncodeableFactory(String type, IEncodeableFactory encodeableFactory);

  /**
   * Register a factory for encodeable objects to the manager.
   *
   * <p>Therefore a special static method in the implementation of the encodeable objects is
   * used to extract the factory object to create an empty class object. This method allows to
   * use a specific class with another keyword
   *
   * @param type Type string that is used as index for the registered factory.
   * @param encodeableClass A special encodeable class, that should be registered.
   */
  void registerEncodeableFactory(String type,
                                        Class<? extends IEncodeable> encodeableClass);

  /**
   * Register a factory for encodeable objects to the manager.
   *
   * <p>Therefore special static methods in the implementation of the encodeable objects are
   * used to extract the class type string and the factory object to create an empty class object.
   *
   * @param encodeableClass A special encodeable class, that should be registered.
   */
  void registerEncodeableFactory(Class<? extends IEncodeable> encodeableClass);

  /**
   * Unregister a factory for encodeable objects from the manager.
   *
   * <p>Therefore special static methods in the implementation of the encodeable objects is
   * used to extract the factory object to create an empty class object. This method allows to
   * use a specific class with another keyword
   *
   * @param type Type string that is used to find a registered factory.
   */
  void unregisterEncodeableFactory(String type);

  /**
   * Register an factory for encodeable objects to the manager.
   *
   * <p>Therefore a special static method in the implementation of the encodeable objects is
   * used to extract the class type string.
   *
   * @param encodeableClass IEncodeable class that should be unregistered
   */
  void unregisterEncodeableFactory(Class<? extends IEncodeable> encodeableClass);
    
  /**
   * Factory method to create an encodeable object.
   *
   * @param type Type string that is used as index for the registered factory.
   * @return a created and empty encodeable object.
   */
  IEncodeable createEncodeable(String type);
}
