//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.spi;

import at.ac.ait.lablink.core.client.ci.ILlClientCommInterface;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * Utility functionality for the Lablink service provider interface.
 */
public final class SpiUtility {


  /**
   * Gets the comm interface implementations.
   *
   */
  public static void getCommInterfaceImplementations() {
    ServiceLoader<ILlClientCommInterface> serviceLoader =
        ServiceLoader.load(ILlClientCommInterface.class);
    for (ILlClientCommInterface cpService : serviceLoader) {
      System.out.println(cpService.getClass().getCanonicalName());
    }

  }

  public static void printSomething() {
    System.out.println("Hello");
  }

  /**
   * Gets the annotation data.
   *
   */
  public static void getAnnotationData() {

    ServiceLoader<ILlClientCommInterface> serviceLoader =
        ServiceLoader.load(ILlClientCommInterface.class);

    System.out.println("Loading Annotations");

    if (serviceLoader == null) {
      System.out.println("Nothing found ");
    }

    for (ILlClientCommInterface cpService : serviceLoader) {

      System.out.println(cpService.getClass().getCanonicalName());
      Class cls = cpService.getClass();

      ALlHostImplementation impl = cpService.getClass().getAnnotation(ALlHostImplementation.class);

      if (!(impl == null)) {
        System.out.println("Access Name: " + impl.accessName());
        System.out.println("Description: " + impl.description());
        System.out.println("IsPseudoHost: " + impl.isPseudoHost());
      } else {
        System.out.println("Nothing found ");
      }
    }
  }

  /**
   * Gets the host implementations.
   *
   * @return the host implementations
   */
  public static Map<String, HostImplementationSpi> getHostImplementations() {

    Map<String, HostImplementationSpi> implMap = new HashMap<String, HostImplementationSpi>();

    ServiceLoader<ILlClientCommInterface> serviceLoader =
        ServiceLoader.load(ILlClientCommInterface.class);

    Iterator<ILlClientCommInterface> sp = serviceLoader.iterator();

    while (sp.hasNext()) {

      try {
        // for (ILlClientCommInterface implementer : serviceLoader) {

        ILlClientCommInterface csp = sp.next();

        ALlHostImplementation impl = csp.getClass().getAnnotation(ALlHostImplementation.class);

        if (impl != null) {
          implMap.put(impl.accessName(), new HostImplementationSpi(impl, csp.getClass()));
        }
      } catch (ServiceConfigurationError ee) {
        System.out.println("Erorr processing sp: " + ee.getMessage());
      }
    }
    return implMap;

  }
}
