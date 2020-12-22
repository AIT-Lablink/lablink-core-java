//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding;

/**
 * Interface to a factory manager for encodable objects.
 *
 * <p>The interface allows the registration of encodable objects to a factory manager. This
 * manager will be used to create new encodables (mainly during decoding)
 */
public interface IEncodableFactoryManager {

  /**
   * Register a factory for encodable objects to the manager.
   *
   * @param type Type string that is used as index for the registered factory.
   * @param encodableFactory Factory for a specific encodable object to be registered.
   */
  void registerEncodableFactory(String type, IEncodableFactory encodableFactory);

  /**
   * Register a factory for encodable objects to the manager.
   *
   * <p>Therefore a special static method in the implementation of the encodable objects is
   * used to extract the factory object to create an empty class object. This method allows to
   * use a specific class with another keyword
   *
   * @param type Type string that is used as index for the registered factory.
   * @param encodableClass A special encodable class, that should be registered.
   */
  void registerEncodableFactory(String type,
                                        Class<? extends IEncodable> encodableClass);

  /**
   * Register a factory for encodable objects to the manager.
   *
   * <p>Therefore special static methods in the implementation of the encodable objects are
   * used to extract the class type string and the factory object to create an empty class object.
   *
   * @param encodableClass A special encodable class, that should be registered.
   */
  void registerEncodableFactory(Class<? extends IEncodable> encodableClass);

  /**
   * Unregister a factory for encodable objects from the manager.
   *
   * <p>Therefore special static methods in the implementation of the encodable objects is
   * used to extract the factory object to create an empty class object. This method allows to
   * use a specific class with another keyword
   *
   * @param type Type string that is used to find a registered factory.
   */
  void unregisterEncodableFactory(String type);

  /**
   * Register an factory for encodable objects to the manager.
   *
   * <p>Therefore a special static method in the implementation of the encodable objects is
   * used to extract the class type string.
   *
   * @param encodableClass IEncodable class that should be unregistered
   */
  void unregisterEncodableFactory(Class<? extends IEncodable> encodableClass);
    
  /**
   * Factory method to create an encodable object.
   *
   * @param type Type string that is used as index for the registered factory.
   * @return a created and empty encodable object.
   */
  IEncodable createEncodable(String type);
}
