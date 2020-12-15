//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.rd;

import at.ac.ait.lablink.core.Configuration;
import at.ac.ait.lablink.core.utility.Utility;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
public class ResourceDiscoveryPeriodicServer extends Thread {

  /** The logger. */
  private static Logger logger =
      LogManager.getLogger(ResourceDiscoveryPeriodicServer.class.getCanonicalName());

  /** The socket. */
  private MulticastSocket socket = null;

  /** The outgoing packet. */
  private DatagramPacket outPacket = null;

  /** The reply. */
  private byte[] reply;

  private String encoding = Configuration.RESOURCE_DISCOVERY_ENCODING_USE;

  /**
   * Instantiates a new resource discovery server.
   *
   * @param discoveryReplyJson the discovery reply JSON
   */
  public ResourceDiscoveryPeriodicServer(String discoveryReplyJson) {
    this.reply = discoveryReplyJson.getBytes();
  }

  /**
   * Instantiates a new resource discovery periodic server.
   *
   * @param meta the meta
   * @throws JsonGenerationException the json generation exception
   * @throws JsonMappingException the json mapping exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public ResourceDiscoveryPeriodicServer(ResourceDiscoveryClientMeta meta)
      throws JsonGenerationException, JsonMappingException, IOException {

    this.reply = Utility.getResourceDiscoveryMetaEncoded(meta);

    // if (this.encoding == Configuration.RESOURCE_DISCOVERY_ENCODING_TYPE_BSON) {
    // this.reply = Utility.getBson(meta);
    // } else {
    // this.reply = Utility.getJson(meta).getBytes();
    // }

  }

  /**
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {

    this.setName(this.getClass().getCanonicalName());

    logger.debug("Trying to start DiscoveryServer at group =[{}] and port=[{}].",
        Configuration.RESOURCE_DISCOVERY_GROUP_IPV4, Configuration.RESOURCE_DISCOVERY_GROUP_PORT);

    try {
      socket = new MulticastSocket();
    } catch (IOException e1) {
      logger.error(e1.getMessage());
    }

    // Set TTL
    try {
      logger.debug("The TTL was {}.", socket.getTimeToLive());
      socket.setTimeToLive(Configuration.RESOURCE_DISCOVERY_TTL_SCOPE);
      logger.debug("The TTL is set to {}.", socket.getTimeToLive());
    } catch (IOException e2) {
      logger.error(e2.getMessage());
    }

    // Set interface
    try {
      socket.setInterface(InetAddress.getLocalHost());
      logger.debug("The transmitting interface is now set to {}.",
          socket.getInterface().getHostAddress());
    } catch (SocketException e3) {
      logger.error(e3.getMessage());
    } catch (UnknownHostException e4) {
      logger.error(e4.getMessage());
    }

    // byte[] buffReply;
    // int buffLen;

    // if (this.reply.length <= 0) {
    // buffReply = "{}".getBytes();
    // } else {
    // buffReply = this.reply;
    // }
    //
    // buffLen = buffReply.length;

    try {
      outPacket = new DatagramPacket(reply, reply.length,
          InetAddress.getByName(Configuration.RESOURCE_DISCOVERY_GROUP_IPV4),
          Configuration.RESOURCE_DISCOVERY_GROUP_PORT);
    } catch (UnknownHostException e5) {
      logger.error(e5.getMessage());
    }

    while (true) {

      logger.debug("Sending packet...");

      try {
        socket.send(outPacket);
      } catch (IOException e6) {
        logger.error(e6.getMessage());
      }

      logger.debug("Waiting...");
      try {
        Thread.sleep(Configuration.RESOURCE_DISCOVERY_PERIODIC_ADVERTISE_PAUSE_MS);
      } catch (InterruptedException e7) {
        logger.error(e7.getMessage());
      }
    }
  }

}
