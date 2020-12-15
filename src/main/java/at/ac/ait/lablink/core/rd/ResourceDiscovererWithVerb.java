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

/**
 * Class ResourceDiscovererWithVerb.
 */
public class ResourceDiscovererWithVerb extends Thread {

  /** The notifier. */
  private IResourceDiscoveryNotifier notifier;

  /** The logger. */
  private static Logger logger =
      LogManager.getLogger(ResourceDiscoveryVerbServer.class.getCanonicalName());

  /** The socket. */
  private MulticastSocket socket = null;

  /** The buffer. */
  private byte[] inBuff = new byte[Configuration.RESOURCE_DISCOVERY_BUFFER_SIZE];

  /** The outgoing packet. */
  private DatagramPacket outPacket = null;

  /**
   * Instantiates a new resource discoverer with verb.
   *
   * @param listner the listener
   */
  public ResourceDiscovererWithVerb(IResourceDiscoveryNotifier listner) {
    this.notifier = listner;
  }

  /**
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {

    outPacket = new DatagramPacket(Configuration.RESOURCE_DISCOVERY_ADVERTISE_VERB.getBytes(),
        Configuration.RESOURCE_DISCOVERY_ADVERTISE_VERB.length());

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
      socket.setInterface(InetAddress.getLocalHost());
      logger.debug("The transmitting interface is now set to {}.",
          socket.getInterface().getHostAddress());
    } catch (SocketException e2) {
      logger.error(e2.getMessage());
    } catch (UnknownHostException e3) {
      logger.error(e3.getMessage());
    }

    // Prepare address
    InetAddress group = null;

    try {
      group = InetAddress.getByName(Configuration.RESOURCE_DISCOVERY_GROUP_IPV4);
    } catch (UnknownHostException e4) {
      logger.error(e4.getMessage());
    }

    logger.debug("Trying to join group...");

    // Join group
    try {
      socket.joinGroup(group);
    } catch (IOException e5) {
      logger.error(e5.getMessage());
    }

    // 1. Send the resource discovery verb
    logger.debug("Requesting advertisement...");

    try {
      socket.send(this.outPacket);
    } catch (IOException e6) {
      logger.error(e6.getMessage());
    }

    // How long to wait for the replies
    long finaltime =
        System.currentTimeMillis() + Configuration.RESOURCE_DISCOVERY_ADVERTISE_WAIT_MS;

    // Set socket timeout
    try {
      socket.setSoTimeout(1000);
    } catch (SocketException e7) {
      logger.error(e7.getMessage());
    }

    while (true) {
      DatagramPacket inPacket = new DatagramPacket(this.inBuff, this.inBuff.length);

      try {
        socket.receive(inPacket);
      } catch (SocketTimeoutException e8) {
        logger.debug(e8.getMessage());
      } catch (IOException e9) {
        logger.error(e9.getMessage());
      }

      String payload = new String(inPacket.getData(), 0, inPacket.getLength());
      this.notifier.onReply(new ResourceMessage(inPacket,
          Configuration.RESOURCE_DISCOVERY_ENCODING_USE, Configuration.RESOURCE_DISCOVERY_VERSION));
      logger.debug("Received {} bytes from {}.", payload.length(), inPacket.getAddress());

      if (System.currentTimeMillis() >= finaltime) {
        break;
      }

    } // while ends

    try {
      socket.leaveGroup(group);
    } catch (IOException e10) {
      logger.error(e10.getMessage());
    }

    socket.close();

  } // run() ends

}
