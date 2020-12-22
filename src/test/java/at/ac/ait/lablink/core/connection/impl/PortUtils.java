//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.impl;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * Utility methods for TCP communication during tests.
 */
public class PortUtils {

  public static void waitForAvailablePort(int port) {

    for (int i = 0; i < 10; i++) {
      if (portAvailable(port)) {
        return;
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }
    throw new RuntimeException("Port " + port + " isn't available");
  }

  /**
   * Checks to see if a specific port is available.
   *
   * @param port the port to check for availability
   */
  public static boolean portAvailable(int port) {

    boolean isAvailable = false;
    ServerSocket ss = null;
    DatagramSocket ds = null;
    try {
      ss = new ServerSocket(port);
      ss.setReuseAddress(true);
      ds = new DatagramSocket(port);
      ds.setReuseAddress(true);
      isAvailable = true;
    } catch (IOException ex) {
      //expected
    } finally {
      if (ds != null) {
        ds.close();
      }

      if (ss != null) {
        try {
          ss.close();
        } catch (IOException ex) {
            // should not be thrown
        }
      }
    }

    return isAvailable;
  }
}
