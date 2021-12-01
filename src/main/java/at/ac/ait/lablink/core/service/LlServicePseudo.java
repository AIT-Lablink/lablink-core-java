//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Abstract base class for Lablink pseudo services.
 *
 * @param <T> the generic type
 */
public abstract class LlServicePseudo<T> extends LlServiceBase {

  /** The current state. */
  protected T currentState;

  protected Map<Integer, IServiceStateChangeNotifier<LlServicePseudo, T>> notifiers =
      new HashMap<Integer, IServiceStateChangeNotifier<LlServicePseudo, T>>();

  /**
   * Gets the.
   *
   * @return the t
   */
  public T get() {
    return currentState;
  }

  /**
   * Sets the.
   *
   * @param currentState the current state
   * @return true, if successful
   */
  public boolean set(T currentState) {

    // if (!this.readOnly) {
    T oldVal = this.currentState;
    this.currentState = currentState;
    notifyStateChange(oldVal, currentState);
    // }

    return true; // this.readOnly;
  }

  private void notifyStateChange(T oldVal, T newVal) {
      
    if (this.notifiers.size() > 0) {

      logger.debug("Service [{}]: state changed from [{}] to [{}]!", this.getName(), oldVal,
          newVal);

      logger.debug("Notifying to the [{}] registered listener...", this.getName(), this.notifiers.size());
        
      this.notifiers.forEach((k,v)-> v.stateChanged(this, oldVal, newVal));

        //   for (Entry<Integer, IServiceStateChangeNotifier<LlServicePseudo, T>> entry : this.notifiers
        //       .entrySet()) {
        //     entry.getValue().stateChanged(this, oldVal, newVal);
        //   }
    }
  }

  /**
   * Instantiates a new ll service psecudo.
   */
  public LlServicePseudo() {
    super();
  }

  /**
   * Instantiates a new ll service pscudo.
   *
   * @param name the name
   * @param readonly the readonly
   */
  public LlServicePseudo(String name, boolean readonly) {
    super(name, readonly);
  }

  public LlServicePseudo(String name, boolean readonly, boolean exposedToPrometheus) {
    super(name, readonly, exposedToPrometheus);
  }

  /**
   * Instantiates a new ll service pscudo.
   *
   * @param name the name
   */
  public LlServicePseudo(String name) {
    super(name);
  }

  /**
   * Instantiates a new ll service pscudo.
   *
   * @param readonly the readonly
   */
  public LlServicePseudo(boolean readonly) {
    super(readonly);
  }

  /**
   * Gets the service data type class.
   *
   * @return the service data type class
   */
  @SuppressWarnings( "unchecked" )
  public Class<T> getServiceDataTypeClass() {
    return (Class<T>) this.get().getClass();
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

  public void addStateChangeNotifier(IServiceStateChangeNotifier<LlServicePseudo, T> notifier) {
    this.notifiers.put(this.notifiers.size() + 1, notifier);
  }

}
