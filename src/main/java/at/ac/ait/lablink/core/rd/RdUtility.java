//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.rd;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class RdUtility.
 */
public final class RdUtility {

  private static Logger logger =
      LogManager.getLogger(ResourceDiscovererPeriodic.class.getCanonicalName());

  /**
   * Gets the host addresses.
   *
   * @return the host addresses
   */
  public static String[] getHostAddresses() {
    Set<String> hostAddresses = new HashSet<>();
    try {
      for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
        if (!ni.isLoopback() && ni.isUp() && ni.getHardwareAddress() != null) {
          for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
            if (ia.getBroadcast() != null) { // If limited to IPV4
              hostAddresses.add(ia.getAddress().getHostAddress());
            }
          }
        }
      }
    } catch (SocketException ex) {
      // Do nothing ...
    }
    return hostAddresses.toArray(new String[0]);
  }

  /**
   * Gets the all host addresses.
   *
   * @return the all host addresses
   */
  private static Set<String> getAllHostAddresses() {
    Set<String> hostAddresses = new HashSet<>();
    try {
      for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
        if (!ni.isLoopback() && ni.isUp() && ni.getHardwareAddress() != null) {
          for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
            if (ia.getBroadcast() != null) { // If limited to IPV4
              hostAddresses.add(ia.getAddress().getHostAddress());
            }
          }
        }
      }
    } catch (SocketException ex) {
      // Do nothing ...
    }
    return hostAddresses;
  }


  /**
   * Gets the ll rd host.
   *
   * @return the ll rd host
   */
  public static String[] getLlRdHost() {

    String llinterface = getLlRdConfigInterface();

    logger.debug("configuration LLRDIPV4 with value {}.", llinterface);

    if (llinterface == null) {
      logger.debug("configuration LLRDIPV4 not found.");
      return getHostAddresses();
    } else {
      logger.debug("Found configuration LLRDIPV4 with value {}.", llinterface);
      Set<String> hosts = getAllHostAddresses();
      if (hosts.contains(llinterface)) {
        return new String[] {llinterface};
      } else {
        return hosts.toArray(new String[0]);
      }
    }
  }

  /**
   * Getter for the resource discovery config interface.
   * @return resource discovery config interface
   */
  public static String getLlRdConfigInterface() {
    String llinterface = System.getenv("LLRDIPV4");
    llinterface = llinterface == null ? System.getProperty("ait.ll.rdipv4") : llinterface;
    return llinterface;
  }
}
