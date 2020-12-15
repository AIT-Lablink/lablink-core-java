//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.dispatching;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Manager for handling the callback executions of incoming messages.
 */
public class CallbackExecutorManager {

  /**
   * Default logger of the class.
   */
  protected static final Logger logger = LoggerFactory.getLogger(CallbackExecutorManager.class);

  /* Executor service for handling incoming messages and executes the callback handlers*/
  private ExecutorService executor;

  private int numberOfExecutionThreads = -1;

  /**
   * Constructor.
   *
   * @param config Optional configuration for execution service of callbacks
   */
  public CallbackExecutorManager(Configuration config) {

    if (config == null) {
      logger.info("No configuration is set for JsonDecoder. Use default configuration.");
      config = new BaseConfiguration();
    }

    numberOfExecutionThreads =
        config.getInt("lowlevelComm.numberOfParallelExecutions", numberOfExecutionThreads);

    if (numberOfExecutionThreads > 0) {
      executor = Executors.newFixedThreadPool(numberOfExecutionThreads);
    } else {
      executor = Executors.newCachedThreadPool();
    }
  }

  /**
   * Shutdown the Executor service for handling callback methods.
   */
  public void shutdown() {
    shutdownThreadPoolAndAwaitTermination();
  }

  /**
   * Shutdown the thread pool in two steps. At first shutdown waiting threads and wait 20 seconds
   * to give currently running thread the chance for finishing. Then interrupt these running
   * threads.
   */
  private void shutdownThreadPoolAndAwaitTermination() {
    executor.shutdown();
    try {

      if (!executor.awaitTermination(20, TimeUnit.SECONDS)) {
        executor.shutdownNow();
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
          logger.error("ExecutorManager Thread Pool did not terminate.");
        }
      }
    } catch (InterruptedException ie) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Add a new callback executor for a unique received message that should be executed by another
   * thread.
   *
   * @param callback CallbackExecutor that should be executed.
   */
  public void addNewCallbackExecution(CallbackExecutor callback) {
    logger.trace("New Executor is added for execution: {}", callback);
    executor.execute(new CallbackExecutorConsumer(callback));
  }


  /**
   * Consumer thread that executes callback methods for incoming messages.
   */
  private class CallbackExecutorConsumer implements Runnable {

    CallbackExecutor callback;

    /**
     * Constructor.
     *
     * @param callback Callback for unique incoming message that should be executed by another
     *                 thread.
     */
    public CallbackExecutorConsumer(CallbackExecutor callback) {
      this.callback = callback;
    }

    @Override
    public void run() {
      logger.trace("Execute callback: {}", callback);
      callback.handleCallback();
    }
  }
}
