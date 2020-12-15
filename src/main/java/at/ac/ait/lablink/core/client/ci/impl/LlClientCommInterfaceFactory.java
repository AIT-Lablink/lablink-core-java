//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ci.impl;

import at.ac.ait.lablink.core.client.ILlClientLogic;
import at.ac.ait.lablink.core.client.ci.ILlClientCommInterface;
import at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException;
import at.ac.ait.lablink.core.client.ex.NoSuchPseudoHostException;
import at.ac.ait.lablink.core.spi.HostImplementationSpi;
import at.ac.ait.lablink.core.spi.SpiUtility;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * A factory for creating LlClientCommInterface objects.
 */
public class LlClientCommInterfaceFactory {

  /**
   * Gets the implementation.
   *
   * @param clogic Lablink client logic
   * @return Lablink client communication interface implementation
   * @throws at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException no such comm 
   *     interface exception
   * @throws at.ac.ait.lablink.core.client.ex.NoSuchPseudoHostException no such pseudo 
   *     host exception 
   * @throws java.lang.IllegalAccessException illegal access exception
   * @throws java.lang.InstantiationException instantiation exception
   * @throws java.lang.NoSuchMethodException no such method exception
   * @throws java.lang.reflect.InvocationTargetException invocation target exception
   */
  public static ILlClientCommInterface getHostImplementation(ILlClientLogic clogic) throws
      at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException, 
      at.ac.ait.lablink.core.client.ex.NoSuchPseudoHostException, 
      java.lang.IllegalAccessException,
      java.lang.InstantiationException,
      java.lang.NoSuchMethodException,
      java.lang.reflect.InvocationTargetException {
    ILlClientCommInterface newInterface = null;

    String implementation = clogic.getHostImplementationSp();

    Map<String, HostImplementationSpi> info = SpiUtility.getHostImplementations();

    if (info.get(implementation) == null) {
      throw new NoSuchCommInterfaceException();
    } else {
      newInterface = info.get(implementation).getHostImplementer().getDeclaredConstructor()
          .newInstance();
      newInterface.setClientLogic(clogic);
    }

    return newInterface;
  }
}
