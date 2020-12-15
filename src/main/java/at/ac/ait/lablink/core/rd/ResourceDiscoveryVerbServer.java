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
import java.net.UnknownHostException;


/**
 * Class ResourceDiscoveryServer.
 */
public class ResourceDiscoveryVerbServer extends Thread {

  private static Logger logger =
      LogManager.getLogger(ResourceDiscoveryVerbServer.class.getCanonicalName());

  /** The socket. */
  private MulticastSocket socket = null;

  private byte[] inBuff = new byte[Configuration.RESOURCE_DISCOVERY_BUFFER_SIZE];

  /** The outgoing packet. */
  private DatagramPacket outPacket = null;

  /**
   * Instantiates a new resource discovery server.
   *
   * @param discoveryReplyJson the discovery reply JSON
   */
  public ResourceDiscoveryVerbServer(String discoveryReplyJson) {

    byte[] buffReply;
    int buffLen;

    if (discoveryReplyJson == null) {
      buffReply = "{}".getBytes();
    } else {
      buffReply = discoveryReplyJson.getBytes();
    }

    buffLen = buffReply.length;
    outPacket = new DatagramPacket(buffReply, buffLen);
  }

  /**
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {

    logger.debug("Trying to start DiscoveryServer at group =[{}] and port=[{}].",
        Configuration.RESOURCE_DISCOVERY_GROUP_IPV4, Configuration.RESOURCE_DISCOVERY_GROUP_PORT);

    try {
      socket = new MulticastSocket(Configuration.RESOURCE_DISCOVERY_GROUP_PORT);
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

    // Set TTL
    try {
      logger.debug("The TTL was {}.", socket.getTimeToLive());
      socket.setTimeToLive(Configuration.RESOURCE_DISCOVERY_TTL_SCOPE);
      logger.debug("The TTL is set to {}.", socket.getTimeToLive());
    } catch (IOException e4) {
      logger.error(e4.getMessage());
    }

    InetAddress group = null;

    try {
      group = InetAddress.getByName(Configuration.RESOURCE_DISCOVERY_GROUP_IPV4);
    } catch (UnknownHostException e5) {
      logger.error(e5.getMessage());
    }

    logger.debug("Trying to join group...");

    try {
      socket.joinGroup(group);
    } catch (IOException e6) {
      logger.error(e6.getMessage());
    }

    logger.debug("Waiting for request...");

    while (true) {

      DatagramPacket packet = new DatagramPacket(this.inBuff, this.inBuff.length);

      try {
        socket.receive(packet);
      } catch (IOException e7) {
        logger.error(e7.getMessage());
      }

      String received = new String(packet.getData(), 0, packet.getLength());

      if (Configuration.RESOURCE_DISCOVERY_ADVERTISE_VERB.equals(received)) {

        logger.debug("Advertise Verb reveived, preparing reply...");

        try {
          socket.send(this.outPacket);
        } catch (IOException e8) {
          logger.error(e8.getMessage());
        }
      } else {
        if (Configuration.RESOURCE_DISCOVERY_END_VERB.equals(received)) {
          logger.debug("End Verb reveived, shutting down...");
          try {
            socket.leaveGroup(group);
          } catch (IOException e9) {
            logger.error(e9.getMessage());
          }
          socket.close();
          break;
        }
      }
    }
  }

}
