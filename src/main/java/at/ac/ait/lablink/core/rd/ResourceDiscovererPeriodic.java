//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.rd;

import at.ac.ait.lablink.core.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


public class ResourceDiscovererPeriodic extends Thread {

  private String version = Configuration.RESOURCE_DISCOVERY_VERSION;
  private String encoding = Configuration.RESOURCE_DISCOVERY_ENCODING_USE;

  private IResourceDiscoveryNotifier notifier;

  private byte[] inBuff = new byte[Configuration.RESOURCE_DISCOVERY_BUFFER_SIZE];

  private static Logger logger =
      LogManager.getLogger(ResourceDiscovererPeriodic.class.getCanonicalName());

  private MulticastSocket socket = null;

  public ResourceDiscovererPeriodic(IResourceDiscoveryNotifier listner) {
    this.notifier = listner;
  }

  @Override
  public void run() {

    this.setName(this.getClass().getCanonicalName());
    logger.debug("Trying to start DiscoveryServer at group =[{}] and port=[{}].",
        Configuration.RESOURCE_DISCOVERY_GROUP_IPV4, Configuration.RESOURCE_DISCOVERY_GROUP_PORT);

    // Create socket
    try {
      socket = new MulticastSocket(Configuration.RESOURCE_DISCOVERY_GROUP_PORT);
    } catch (IOException e1) {
      logger.error(e1.getMessage());
    }

    // Set TTL
    try {
      logger.debug("The TTL was {}.", socket.getTimeToLive());
      socket.setTimeToLive(Configuration.RESOURCE_DISCOVERY_TTL_SCOPE);
      logger.debug("The TTL is set to {}.", socket.getTimeToLive());
    } catch (IOException e1) {
      logger.error(e1.getMessage());
    }

    // Set interface
    try {
      logger.debug("The default receiving interface was {}.",
          socket.getInterface().getHostAddress());
      // socket.setInterface(InetAddress.getLocalHost());
      // socket.setInterface(InetAddress.getByName(RdUtility.getHostAddresses()[0]));
      logger.debug("The RD IPV4 interface configuration found to be '{}'.",
          RdUtility.getLlRdConfigInterface());
      socket.setInterface(InetAddress.getByName(RdUtility.getLlRdHost()[0]));
      logger.debug("The receiving interface is now set to {}.",
          socket.getInterface().getHostAddress());
    } catch (SocketException e2) {
      logger.error(e2.getMessage());
    } catch (UnknownHostException ex) {
      logger.error(ex.getMessage());
    }

    // Set socket reusability
    try {
      socket.setReuseAddress(true);
      logger.debug("Enabling reuse...");
    } catch (SocketException e2) {
      logger.error(e2.getMessage());
    }
    // Prepare address
    InetAddress group = null;

    try {
      group = InetAddress.getByName(Configuration.RESOURCE_DISCOVERY_GROUP_IPV4);
    } catch (UnknownHostException e1) {
      logger.error(e1.getMessage());
    }

    logger.debug("Trying to join group...");

    // Join group
    try {
      socket.joinGroup(group);
    } catch (IOException e1) {
      logger.error(e1.getMessage());
    }

    // 1. Send the resource discovery verb
    logger.debug("Collecting advertisement...");


    // How long to wait for the replies
    long finaltime =
        System.currentTimeMillis() + Configuration.RESOURCE_DISCOVERY_ADVERTISE_WAIT_MS;

    // Set socket timeout
    try {
      socket.setSoTimeout(Configuration.RESOURCE_DISCOVERY_SOCKET_TIMEOUT_MS);
    } catch (SocketException ex) {
      logger.error(ex.getMessage());
    }

    while (true) {

      boolean timeout = false;
      DatagramPacket inPacket = new DatagramPacket(this.inBuff, this.inBuff.length);

      try {
        socket.receive(inPacket);
      } catch (SocketTimeoutException te) {
        logger.debug(te.getMessage());
        timeout = true;
      } catch (IOException ex) {
        logger.error(ex.getMessage());
      }

      String payload = new String(inPacket.getData(), 0, inPacket.getLength());

      if (!timeout) {

        this.notifier.onReply(new ResourceMessage(inPacket, this.encoding, this.version));

        logger.debug("Received {} bytes from {}.", payload.length(), inPacket.getAddress());
      }

      if (System.currentTimeMillis() >= finaltime) {
        break;
      } else {
        logger.debug("Remaing time is {}ms.", finaltime - System.currentTimeMillis());
      }


    } // while ends

    try {
      socket.leaveGroup(group);
    } catch (IOException ex) {
      logger.error(ex.getMessage());
    }

    socket.close();


  } // run() ends

}
