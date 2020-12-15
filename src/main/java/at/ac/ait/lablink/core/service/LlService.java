//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Abstract class extending the Lablink service base class.
 *
 * @param <T> the generic type
 */
public abstract class LlService<T> extends LlServiceBase {

  /** The current state value of the service. */
  private T curState;

  protected Map<Integer, IServiceStateChangeNotifier<LlService, T>> notifiers =
      new HashMap<Integer, IServiceStateChangeNotifier<LlService, T>>();

  // ******************************** CONSTRUCTORS ************************************** //

  /**
   * Instantiates a new Lablink Service with a generated (not very friendly) name and read/write
   * access.
   */
  public LlService() {
    super();
  }

  /**
   * Instantiates a new Lablink Service with a generated (no very friendly) name. The access can be
   * specified with the boolean flag.
   *
   * @param readonly if true, the service will be readonly.
   */
  public LlService(boolean readonly) {
    super(readonly);
  }

  /**
   * Instantiates a new Lablink service with the name provided. The resulting instance will be
   * read/write enabled.
   *
   * @param name the name
   */
  public LlService(String name) {
    super(name, false);
  }

  /**
   * Instantiates a new Lablink service with the name and access type specified with the boolean
   * flag.
   *
   * @param name the Name for the new service
   * @param readonly if true, the service will be readonly.
   */
  public LlService(String name, boolean readonly) {
    super(name, readonly);
  }

  // ********************************************************************** //

  /**
   * Gets the class for the data type of the service.
   * 
   * @return the class object of the data type
   */
  @SuppressWarnings("unchecked")
  public Class<T> getServiceDataTypeClass() {
    return (Class<T>) curState.getClass();
  }

  /**
   * Gets the service data type.
   *
   * @return the service data type
   */
  public ELlServiceDataTypes getServiceDataType() {
    ELlServiceDataTypes val =
        ELlServiceDataTypes.getFromId(this.getServiceDataTypeClass().getSimpleName());
    return val;
  }


  /**
   * Gets the Current State.
   *
   * @return the curState
   */
  public T getCurState() {
    return curState;
  }

  private void notifyStateChange(T oldVal, T newVal) {
    if (this.notifiers.size() > 0) {

      logger.debug("Service [{}]: state changed from [{}] to [{}]!", this.getName(), oldVal,
          newVal);

      logger.debug("Notifying to the [{}] registered listener...", this.notifiers.size());

      for (Entry<Integer, IServiceStateChangeNotifier<LlService, T>> entry : this.notifiers
          .entrySet()) {
        entry.getValue().stateChanged(this, oldVal, newVal);
      }
    }
  }

  /**
   * Sets the Current State.
   *
   * @param curVal the new cur state
   */
  public void setCurState(T curVal) {
    T oldVal = this.curState;
    this.curState = curVal;
    notifyStateChange(oldVal, curVal);
  }

  /**
   * The get function.
   *
   * @return the t
   */
  public abstract T get();

  /**
   * The set function.
   *
   * @param newval the newval
   * @return true, if successful
   */
  public abstract boolean set(T newval);

  public void addStateChangeNotifier(IServiceStateChangeNotifier<LlService, T> notifier) {
    this.notifiers.put(this.notifiers.size() + 1, notifier);
    logger.debug("Another notifier added for service [{}].", this.getName());
  }
}
