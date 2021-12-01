//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client;

import at.ac.ait.lablink.core.client.ex.ClientNotReadyException;
import at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException;
import at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException;
import at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException;
import at.ac.ait.lablink.core.client.ex.NoSuchPseudoHostException;
import at.ac.ait.lablink.core.client.ex.PseudoHostException;

import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface ILlClientFsmLogic {

  public void onCreateSuccess() throws 
      ClientNotReadyException, NoSuchCommInterfaceException,
      NoSuchPseudoHostException, NoSuchMethodException,
      IllegalAccessException, InstantiationException, 
      InvocationTargetException;

  public void onInitSuccess() throws 
      ConfigurationException, ClientNotReadyException,
      NoServicesInClientLogicException, DataTypeNotSupportedException, 
      PseudoHostException;

  public void onStartSuccess() throws 
      ClientNotReadyException, PseudoHostException, IOException;

  public void onShutdownSuccess() throws 
      ClientNotReadyException;
}
