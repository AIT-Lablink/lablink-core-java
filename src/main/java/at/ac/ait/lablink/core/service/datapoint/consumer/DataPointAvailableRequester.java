//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.consumer;

import at.ac.ait.lablink.core.connection.ILlConnection;
import at.ac.ait.lablink.core.connection.encoding.encodables.Header;
import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;
import at.ac.ait.lablink.core.connection.rpc.IRpcRequester;
import at.ac.ait.lablink.core.connection.rpc.RpcHeader;
import at.ac.ait.lablink.core.connection.rpc.reply.IRpcReplyCallback;
import at.ac.ait.lablink.core.connection.topic.RpcDestination;
import at.ac.ait.lablink.core.connection.topic.RpcSubject;
import at.ac.ait.lablink.core.payloads.ErrorMessage;
import at.ac.ait.lablink.core.payloads.StatusMessage;
import at.ac.ait.lablink.core.service.datapoint.payloads.DataPointProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Requester for available Datapoints within a Lablink application.
 *
 * <p>This class can be used to request all available datapoints from all Lablink clients within an
 * application.
 */
public class DataPointAvailableRequester {

  private final long requestTimeout;
  private Logger logger = LoggerFactory.getLogger(DataPointAvailableRequester.class);

  private ILlConnection lablinkConnection;

  private List<DataPointInfo> dataPointInfos = new ArrayList<DataPointInfo>();

  /**
   * Constructor.
   *
   * @param lablinkConnection Lablink connection interface
   * @param requestTimeout request timeout in milliseconds
   */
  public DataPointAvailableRequester(ILlConnection lablinkConnection, long requestTimeout) {
    this.lablinkConnection = lablinkConnection;

    if (requestTimeout < 0) {
      requestTimeout = 1000;
    }
    this.requestTimeout = requestTimeout;
  }

  /**
   * Retrieve list of datapoint infos.
   *
   * @return list of datapoint infos
   */
  public List<DataPointInfo> requestDatapoints() {
    RpcSubject
        subject =
        RpcSubject.getBuilder().addSubjectElement("services").addSubjectElement("datapoints")
            .addSubjectElement("availableDatapoints").build();

    RpcDestination
        destination =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_ALL).build();

    IPayload requestPayload = new StatusMessage(StatusMessage.StatusCode.OK);

    IRpcRequester
        requester =
        lablinkConnection.registerReplyHandler(subject, new AvailableDataPointReply());
    requester.sendRequest(destination, requestPayload, -1, requestTimeout);

    try {
      Thread.sleep(requestTimeout);
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    return dataPointInfos;
  }

  private class AvailableDataPointReply implements IRpcReplyCallback {

    @Override
    public void handleReply(RpcHeader header, List<IPayload> payloads) {
      logger.info("Received availableDataPointReply from [{} {}]", header.getSourceGroupId(),
          header.getSourceClientId());
      if (logger.isInfoEnabled()) {
        if ((payloads.get(0) instanceof StatusMessage)) {
          logger.info("Client [{} {}] has no datapoints registered.", header.getSourceGroupId(),
              header.getSourceClientId());
        } else {
          logger.info("Client [{} {}] has {} datapoint registered.", header.getSourceGroupId(),
              header.getSourceClientId(), payloads.size());
        }
      }
      for (IPayload payload : payloads) {
        if (payload instanceof DataPointProperties) {
          DataPointInfo
              info =
              new DataPointInfo(header.getSourceGroupId(), header.getSourceClientId(),
                  (DataPointProperties) payload);
          dataPointInfos.add(info);
        }
      }

    }

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {
      logger
          .warn("AvailableDataPointReply receives error from {} {}: {}", header.getSourceGroupId(),
              header.getSourceClientId(), errors);
    }
  }
}
