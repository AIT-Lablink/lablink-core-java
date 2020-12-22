//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.impl;

import at.ac.ait.lablink.core.connection.ClientIdentifier;
import at.ac.ait.lablink.core.connection.dispatching.ICallbackBase;
import at.ac.ait.lablink.core.connection.dispatching.IDispatcherCallback;
import at.ac.ait.lablink.core.connection.dispatching.IDispatcherInterface;
import at.ac.ait.lablink.core.connection.dispatching.impl.DispatchingTreeNode;
import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;
import at.ac.ait.lablink.core.connection.encoding.encodables.Packet;
import at.ac.ait.lablink.core.connection.publishing.PublishingManager;
import at.ac.ait.lablink.core.connection.rpc.IRpcRequester;
import at.ac.ait.lablink.core.connection.rpc.RpcHeader;
import at.ac.ait.lablink.core.connection.rpc.reply.impl.RpcReplyDispatcher;
import at.ac.ait.lablink.core.connection.topic.RpcDestination;
import at.ac.ait.lablink.core.connection.topic.RpcSubject;
import at.ac.ait.lablink.core.connection.topic.Topic;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;
import at.ac.ait.lablink.core.payloads.ErrorMessage;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of an RPC requester object.
 */
public class RpcRequesterImpl implements IRpcRequester {

  private static final Logger logger = LoggerFactory.getLogger(RpcRequesterImpl.class);

  private final ClientIdentifier clientId;
  private final String dispatcherIdentifier;

  private final RpcSubject rpcSubject;

  /* Dispatcher callback that should be used for handling replies */
  private final IDispatcherCallback replyCallback;

  /* Error callback that should be used for handling error messages */
  private final ICallbackBase errorCallback;

  private PublishingManager publishingManager;

  private RpcReplyDispatcher rootReplyDispatcher;

  private int defaultNoOfReturns = 1;
  private long defaultTimeoutMs = 30000;

  private ExecutorService requestExecutorService = Executors.newCachedThreadPool();

  /**
   * Constructor.
   *
   * @param dispatcherIdentifier Transmission identifier for a request (usually "req")
   * @param clientId             identification of the client
   * @param rpcSubject           Subject identifier of the requester
   * @param replyCallback        callback for handling replies.
   * @param errorCallback        callback for handling errors.
   * @param config               optional configuration
   */
  public RpcRequesterImpl(String dispatcherIdentifier, ClientIdentifier clientId,
                          RpcSubject rpcSubject, IDispatcherCallback replyCallback,
                          ICallbackBase errorCallback, Configuration config) {
    this.dispatcherIdentifier = dispatcherIdentifier;
    this.clientId = clientId;
    this.rpcSubject = rpcSubject;

    this.replyCallback = replyCallback;
    this.errorCallback = errorCallback;

    if (config == null) {
      logger.debug("No configuration is set for IRpcRequester. Use default configuration.");
      config = new BaseConfiguration(); /* Initialize empty configuration */
    }

    defaultNoOfReturns = config.getInt("rpc.request.noOfReturns", defaultNoOfReturns);
    defaultTimeoutMs = config.getLong("rpc.request.timeoutMs", defaultTimeoutMs);

    logger.debug("RPC request default Number of Returns: {}", this.defaultNoOfReturns);
    logger.debug("RPC request default Timeout in Milliseconds: {}", this.defaultTimeoutMs);

  }

  /**
   * Set the publishing manager for sending requests.
   *
   * @param publishingManager to be set
   */
  public void setPublishingManager(PublishingManager publishingManager) {
    this.publishingManager = publishingManager;
  }

  /**
   * Set the root reply dispatcher that should be used to add new callback handlers.
   *
   * @param rootReplyDispatcher to be set.
   */
  public void setRootReplyDispatcher(RpcReplyDispatcher rootReplyDispatcher) {
    this.rootReplyDispatcher = rootReplyDispatcher;
  }

  @Override
  public String sendRequest(RpcDestination destination, IPayload payload) {
    return this.sendRequest(destination, Collections.singletonList(payload));
  }

  @Override
  public String sendRequest(RpcDestination destination, IPayload payload, int noOfReturns) {
    return this.sendRequest(destination, Collections.singletonList(payload), noOfReturns);
  }

  @Override
  public String sendRequest(RpcDestination destination, IPayload payload, int noOfReturns,
                            long timeoutInMs) {
    return this
        .sendRequest(destination, Collections.singletonList(payload), noOfReturns, timeoutInMs);
  }


  @Override
  public String sendRequest(RpcDestination destination, List<IPayload> payloads) {
    return this.sendRequest(destination, payloads, defaultNoOfReturns, defaultTimeoutMs);
  }

  @Override
  public String sendRequest(RpcDestination destination, List<IPayload> payloads, int noOfReturns) {
    return this.sendRequest(destination, payloads, noOfReturns, defaultTimeoutMs);
  }

  @Override
  public String sendRequest(RpcDestination destination, List<IPayload> payloads, int noOfReturns,
                            long timeoutInMs) {

    Request request = new Request(destination, payloads, noOfReturns, timeoutInMs);

    requestExecutorService.submit(request);
    return request.getActPacketIdentifier();
  }


  @Override
  public RpcSubject getSubject() {
    return this.rpcSubject;
  }

  /**
   * Generate a unique and short UUID.
   *
   * @return A unique short UUID
   */
  private static String shortUuid() {
    UUID uuid = UUID.randomUUID();
    long number = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
    return Long.toString(number, Character.MAX_RADIX);
  }

  /**
   * Private class to handle a specific request that is sent over the system.
   *
   * <p>It will be used to independently handle the timeout and callbacks of each request that is
   * sent. The request will add itself to the dispatcher with a second callback. This callback is
   * used to provide the a counter for received replies.
   */
  private class Request extends TimerTask implements IDispatcherCallback {

    private List<IPayload> payloads;
    private RpcDestination destination;
    private CountDownLatch countDownLatch;
    boolean unlimitedReceiving = false;

    private int numberOfReturns;
    private long timeoutSetValueMs;

    private String actPacketIdentifier;

    public Request(RpcDestination destination, List<IPayload> payloads, int numberOfReturns,
                   long timeoutMs) {

      this.destination = destination;
      this.payloads = payloads;

      this.numberOfReturns = numberOfReturns;
      this.timeoutSetValueMs = timeoutMs;

      if (numberOfReturns == -1) { // Unlimited receiving
        unlimitedReceiving = true;
        countDownLatch = new CountDownLatch(1);
      } else {
        countDownLatch = new CountDownLatch(numberOfReturns);
      }
      actPacketIdentifier = RpcRequesterImpl.shortUuid();
    }

    @Override
    public void handleMessage(byte[] payload) {
      if (!unlimitedReceiving) {
        countDownLatch.countDown();
      }
    }

    public String getActPacketIdentifier() {
      return actPacketIdentifier;
    }

    @Override
    public void run() {

      List<String> replyToTopic = new ArrayList<String>();
      replyToTopic.addAll(rpcSubject.getSubject());
      replyToTopic.add(actPacketIdentifier);

      Topic topic = new Topic();
      topic.setPrefix(clientId.getPrefix());
      topic.setApplicationId(clientId.getAppId());
      topic.setTransmissionType(dispatcherIdentifier);
      topic.setClientIdentifiers(destination.getGroupId(), destination.getClientId());
      topic.setSubject(rpcSubject.getSubject());

      RpcHeader
          header =
          new RpcHeader(topic.getApplicationId(), clientId.getGroupId(), clientId.getClientId(),
              topic.getSubject(), System.currentTimeMillis(), topic.getGroupId(),
              topic.getClientId(), actPacketIdentifier);

      Packet packet = new Packet(header, payloads);

      try {

        IDispatcherInterface replyToDispatcher = new DispatchingTreeNode();
        replyToDispatcher.addCallback(replyCallback);
        replyToDispatcher.addCallback(this);
        rootReplyDispatcher.addDispatcher(replyToTopic.iterator(), replyToDispatcher);
        publishingManager.publishPacket(topic.getTopic(), packet);

        boolean
            correctCountDown =
            countDownLatch.await(this.timeoutSetValueMs, TimeUnit.MILLISECONDS);

        if (!unlimitedReceiving && !correctCountDown) {
          if (logger.isDebugEnabled()) {
            logger.debug(
                "Timeout Timer exceeds: Not all replies ({} of expected {}) are received before "
                    + "timeout exceeds.", (this.numberOfReturns - countDownLatch.getCount()),
                this.numberOfReturns);
          }
          try {
            errorCallback.handleError(packet.getHeader(), Collections
                .singletonList(new ErrorMessage(ErrorMessage.EErrorCode.TIMEOUT_ERROR, "Timeout")));
          } catch (Exception ignore) {
            // Expected
          }
        }

        logger.debug("Run cleanup of request {}", replyToTopic);

        rootReplyDispatcher.removeDispatcher(replyToTopic.iterator());

      } catch (LlCoreRuntimeException ex) {
        try {
          errorCallback.handleError(packet.getHeader(), Collections.singletonList(
              new ErrorMessage(ErrorMessage.EErrorCode.PROCESSING_ERROR,
                  "Error during sending request: " + ex.getMessage())));
        } catch (Exception ignore) {
          // Expected
        }
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }

    }
  }
}
