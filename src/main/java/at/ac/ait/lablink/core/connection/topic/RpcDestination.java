//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.topic;

import at.ac.ait.lablink.core.connection.mqtt.impl.MqttUtils;
import at.ac.ait.lablink.core.connection.rpc.request.impl.RpcRequestDispatcher;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

/**
 * Destination identifier for sending RPC calls.
 *
 * <p>This class defines a possible destination for the calls. A RPC call can be sent
 * to a specific client, to a group of client or to all clients. Every element can contain the
 * String "-ANY-" to define a matchAll.
 */
public class RpcDestination {

  /* Client ID for the destination */
  private final String clientId;

  /* Group ID for the destination */
  private final String groupId;

  /**
   * Default private constructor.
   *
   * @param groupId  Group Id for the destination.
   * @param clientId Client Id for the destination.
   */
  private RpcDestination(String groupId, String clientId) {
    this.groupId = groupId;
    this.clientId = clientId;
  }

  /**
   * Get the client identifier of the destination.
   *
   * @return the client identifier or -ANY-.
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * Get the group identifier of the destination
   *
   * @return the group identifier or -ANY-.
   */
  public String getGroupId() {
    return groupId;
  }

  /**
   * Builder class for a RpcDestination.
   *
   * <p>The builder will be used to generate a subject for RPC requests.
   */
  public static class Builder {

    private final ERpcDestinationChooser chooser;
    private String groupId;

    private String clientId;

    /**
     * Constructor.
     *
     * @param chooser defines the number of clients that should receive the call.
     */
    Builder(ERpcDestinationChooser chooser) {
      this.chooser = chooser;
    }

    /**
     * Add the group identifier to the builder.
     *
     * @param groupId to be set.
     * @return the builder
     */
    public Builder setGroupId(String groupId) {
      this.groupId = groupId;
      return this;
    }

    /**
     * Add the client identifier to the builder.
     *
     * @param clientId to be set.
     * @return the builder
     */
    public Builder setClientId(String clientId) {
      this.clientId = clientId;
      return this;
    }

    /**
     * Generate and return a new RpcDestination element with the given combination of included
     * elements.
     *
     * @return the generated RpcDestination object
     * @throws at.ac.ait.lablink.core.ex.LlCoreRuntimeException if a element doesn't fit the
     *                                                               requirements.
     */
    public RpcDestination build() {
      switch (chooser) {
        case SEND_TO_ALL:
          clientId = RpcRequestDispatcher.RPC_REQUEST_ANY_ELEMENT;
          groupId = RpcRequestDispatcher.RPC_REQUEST_ANY_ELEMENT;
          break;
        case SEND_TO_GROUP:
          clientId = RpcRequestDispatcher.RPC_REQUEST_ANY_ELEMENT;
          break;
        case SEND_TO_CLIENT:
          break;
        default:
          throw new LlCoreRuntimeException("Destination choosing wasn't set correctly.");
      }

      if (clientId == null) {
        throw new LlCoreRuntimeException("Client identifier isn't set.");
      }
      if (groupId == null) {
        throw new LlCoreRuntimeException("Group identifier isn't set.");
      }

      MqttUtils.validateMqttSubscription(this.groupId);
      MqttUtils.validateMqttSubscription(this.clientId);

      return new RpcDestination(this.groupId, this.clientId);
    }
  }

  /**
   * Get a new builder to generate a RpcDestination.
   *
   * @param chooser Possible client configuration that should receive the request.
   * @return a new builder for generating a RpcDestination.
   */
  public static Builder getBuilder(ERpcDestinationChooser chooser) {
    return new Builder(chooser);
  }

  /**
   * Routes for a RPC request. The configuration will be used to define possible
   * destinations for a request.
   */
  public enum ERpcDestinationChooser {

    /**
     * Send a RPC request to all connected clients (system broadcast).
     */
    SEND_TO_ALL,

    /**
     * Send a RPC request to all clients of a specific/given group (group broadcast).
     */
    SEND_TO_GROUP,

    /**
     * Send a RPC request to a specific client. A client is has a unique identifier based on the
     * group and client identifier.
     */
    SEND_TO_CLIENT
  }
}
