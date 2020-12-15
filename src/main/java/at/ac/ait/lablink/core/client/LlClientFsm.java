//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jeasy.states.api.Event;
import org.jeasy.states.api.EventHandler;
import org.jeasy.states.api.FiniteStateMachine;
import org.jeasy.states.api.FiniteStateMachineException;
import org.jeasy.states.api.State;
import org.jeasy.states.api.Transition;
import org.jeasy.states.core.FiniteStateMachineBuilder;
import org.jeasy.states.core.TransitionBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Finite state machine for Lablink clients.
 */
public class LlClientFsm {

  /**
   * The Enum EPossibleTransitionTriggers.
   */
  public enum EPossibleTransitionTriggers {

    /** The init. */
    INIT,
    /** The create. */
    CREATE,
    /** The start. */
    START,
    /** The shutdown. */
    SHUTDOWN
  }

  /**
   * Instantiates a new FSM for the LL Client.
   *
   * @param cfsmlogic the cfsmlogic
   */
  public LlClientFsm(ILlClientFsmLogic cfsmlogic) {
    this.fsmLogic = cfsmlogic;

    createStates();
    createTransitions();
    createFsm();

    logger.debug("Client FSM created.");
  }

  /**
   * Creates the states.
   */
  private void createStates() {

    logger.debug("Creating states...");

    stNone = new State(ELlClientStates.LABLINK_CLIENT_INTERFACE_STATE_NOTINSTANTIATED.getId());
    stCreate = new State(ELlClientStates.LABLINK_CLIENT_INTERFACE_STATE_INSTANTIATED.getId());
    stInit = new State(ELlClientStates.LABLINK_CLIENT_INTERFACE_STATE_INITIALIZED.getId());
    stStart = new State(ELlClientStates.LABLINK_CLIENT_INTERFACE_STATE_STARTED.getId());
    stShutdown = new State(ELlClientStates.LABLINK_CLIENT_INTERFACE_STATE_SHUTDOWN.getId());

    this.states.add(stNone);
    this.states.add(stCreate);
    this.states.add(stInit);
    this.states.add(stStart);
    this.states.add(stShutdown);
  }

  /** The logger. */
  private static Logger logger = LogManager.getLogger(LlClientFsm.class.getCanonicalName());

  /** The communication interface. The logic */
  private ILlClientFsmLogic fsmLogic;

  /** The states. */
  private Set<State> states = new HashSet<State>();

  /** All possible states for this FSM. */
  private State stNone;
  private State stCreate;
  private State stInit;
  private State stStart;
  private State stShutdown;

  /** All possible transitions for this FSM. */
  private Transition noneToCreate;
  private Transition createToInit;
  private Transition createToShutdown;
  private Transition initToStart;
  private Transition initToShutdown;
  private Transition startToShutdown;

  /** The client fsm. */
  private FiniteStateMachine clientFsm;

  /**
   * Creates the transitions.
   *
   * @param name the name
   * @param src the src
   * @param dest the dest
   * @param evnt the evnt
   * @param evntHndl the evnt hndl
   * @return the transition
   */
  private Transition buildTransition(String name, State src, State dest, Class evnt,
      EventHandler evntHndl) {

    return new TransitionBuilder().name(name).sourceState(src).eventType(evnt)
        .eventHandler(evntHndl).targetState(dest).build();

  }

  /**
   * Creates the transitions.
   */
  private void createTransitions() {

    logger.debug("Creating transitions...");

    noneToCreate = buildTransition("None -> Create", stNone, stCreate, EvntCreate.class,
        new NoneToCreateHandler());

    createToInit = buildTransition("Create -> Init", stCreate, stInit, EvntInit.class,
        new CreateToInitHandler());

    createToShutdown = buildTransition("Create -> Shutdown", stCreate, stShutdown,
        EvntShutdown.class, new CreateToShutdownHandler());

    initToStart = buildTransition("Init -> Start", stInit, stStart, EvntStart.class,
        new InitToStartHandler());

    initToShutdown = buildTransition("Init -> Shutdown", stInit, stShutdown, EvntShutdown.class,
        new InitToShutdownHandler());

    startToShutdown = buildTransition("Start -> Shutdown", stStart, stShutdown, EvntShutdown.class,
        new StartToShutdownHandler());
  }


  /**
   * Creates the transitions old.
   */
  private void createTransitionsOld() {
    noneToCreate = new TransitionBuilder().name("None -> Create").sourceState(stNone)
        .eventType(EvntCreate.class).eventHandler(new NoneToCreateHandler()).targetState(stCreate)
        .build();

    createToInit = new TransitionBuilder().name("Create -> Init").sourceState(stCreate)
        .eventType(EvntInit.class).eventHandler(new CreateToInitHandler()).targetState(stInit)
        .build();

    createToShutdown = new TransitionBuilder().name("Create -> Shutdown").sourceState(stCreate)
        .eventType(EvntShutdown.class).eventHandler(new CreateToShutdownHandler())
        .targetState(stShutdown).build();

    initToStart =
        new TransitionBuilder().name("Init -> Start").sourceState(stInit).eventType(EvntStart.class)
            .eventHandler(new InitToStartHandler()).targetState(stStart).build();

    initToShutdown = new TransitionBuilder().name("Init -> Shutdown").sourceState(stInit)
        .eventType(EvntShutdown.class).eventHandler(new InitToShutdownHandler())
        .targetState(stShutdown).build();

    startToShutdown = new TransitionBuilder().name("Start -> Shutdown").sourceState(stStart)
        .eventType(EvntShutdown.class).eventHandler(new StartToShutdownHandler())
        .targetState(stShutdown).build();

  }

  /**
   * Creates the FSM.
   */
  private void createFsm() {
    this.clientFsm = new FiniteStateMachineBuilder(states, stNone).registerFinalState(stShutdown)
        .registerTransition(noneToCreate).registerTransition(createToInit)
        .registerTransition(createToShutdown).registerTransition(initToStart)
        .registerTransition(initToShutdown).registerTransition(startToShutdown).build();

    logger.debug("FSM created and is in the state [{}].",
        this.clientFsm.getCurrentState().toString());
  }

  /**
   * The Class EvntCreate.
   */
  class EvntCreate extends Event {

    /**
     * Instantiates a new evnt create.
     */
    public EvntCreate() {
      super("CREATE");
    }
  }

  /**
   * The Class EvntInit.
   */
  class EvntInit extends Event {

    /**
     * Instantiates a new evnt init.
     */
    public EvntInit() {
      super("INIT");
    }
  }

  /**
   * The Class EvntStart.
   */
  class EvntStart extends Event {

    /**
     * Instantiates a new evnt start.
     */
    public EvntStart() {
      super("START");
    }
  }

  /**
   * The Class EvntShutdown.
   */
  class EvntShutdown extends Event {

    /**
     * Instantiates a new evnt shutdown.
     */
    public EvntShutdown() {
      super("SHUTDOWN");
    }

  }

  /**
   * The Class NoneToCreateHandler.
   */
  class NoneToCreateHandler implements EventHandler {

    /**
     * Instantiates a new none to create handeler.
     */
    public NoneToCreateHandler() {
      super();
    }

    /** 
     * @see org.jeasy.states.api.EventHandler#handleEvent(org.jeasy.states.api.Event)
     */
    @Override
    public void handleEvent(Event event) throws Exception {
      logger.debug("Received event {} at {}.", event.getName(), event.getTimestamp());
      fsmLogic.onCreateSuccess();
      logger.info("Client sussessfuly transitioned to [{}] state.", getCurrentState());
    }
  }

  /**
   * The Class CreateToInitHandler.
   */
  class CreateToInitHandler implements EventHandler {

    /** 
     * @see org.jeasy.states.api.EventHandler#handleEvent(org.jeasy.states.api.Event)
     */
    @Override
    public void handleEvent(Event event) throws Exception {
      logger.debug("Received event {} at {}.", event.getName(), event.getTimestamp());
      fsmLogic.onInitSuccess();
      logger.info("Client sussessfuly transitioned to [{}] state.", getCurrentState());
    }
  }

  /**
   * The Class CreateToShutdownHandler.
   */
  // Event Handlers
  class CreateToShutdownHandler implements EventHandler {

    /** 
     * @see org.jeasy.states.api.EventHandler#handleEvent(org.jeasy.states.api.Event)
     */
    @Override
    public void handleEvent(Event event) throws Exception {
      logger.debug("Received event {} at {}.", event.getName(), event.getTimestamp());
      fsmLogic.onShutdownSuccess();
      logger.info("Client sussessfuly transitioned to [{}] state.", getCurrentState());
    }

  }

  /**
   * The Class InitToStartHandler.
   */
  class InitToStartHandler implements EventHandler {
    /** 
     * @see org.jeasy.states.api.EventHandler#handleEvent(org.jeasy.states.api.Event)
     */
    @Override
    public void handleEvent(Event event) throws Exception {
      logger.debug("Received event {} at {}.", event.getName(), event.getTimestamp());
      fsmLogic.onStartSuccess();
      logger.info("Client sussessfuly transitioned to [{}] state.", getCurrentState());
    }

  }

  /**
   * The Class InitToShutdownHandler.
   */
  // Event Handlers
  class InitToShutdownHandler implements EventHandler {

    /** 
     * @see org.jeasy.states.api.EventHandler#handleEvent(org.jeasy.states.api.Event)
     */
    @Override
    public void handleEvent(Event event) throws Exception {
      logger.debug("Received event {} at {}.", event.getName(), event.getTimestamp());
      fsmLogic.onShutdownSuccess();
      logger.info("Client sussessfuly transitioned to [{}] state.", getCurrentState());
    }

  }

  /**
   * The Class StartToShutdownHandler.
   */
  class StartToShutdownHandler implements EventHandler {

    /** 
     * @see org.jeasy.states.api.EventHandler#handleEvent(org.jeasy.states.api.Event)
     */
    @Override
    public void handleEvent(Event event) throws Exception {
      logger.debug("Received event {} at {}.", event.getName(), event.getTimestamp());
      fsmLogic.onShutdownSuccess();
      logger.info("Client sussessfuly transitioned to [{}] state.", getCurrentState());
    }

  }

  /**
   * Gets the current state. The states are mapped on to the Enum ELlClientStates.
   *
   * @return the current (mapped) state
   */
  public ELlClientStates getCurrentState() {
    return ELlClientStates.getFromId(this.clientFsm.getCurrentState().getName().toString());
  }

  /**
   * Transition to.
   *
   * @param transition the transition
   */
  public void transitionTo(EPossibleTransitionTriggers transition) {
    switch (transition) {
      case CREATE:
        try {
          this.clientFsm.fire(new EvntCreate());
        } catch (FiniteStateMachineException ex) {
          logger.error(ex.getMessage());
        }
        break;
      case INIT:
        try {
          this.clientFsm.fire(new EvntInit());
        } catch (FiniteStateMachineException ex) {
          logger.error(ex.getMessage());
        }
        break;
      case START:
        try {
          this.clientFsm.fire(new EvntStart());
        } catch (FiniteStateMachineException ex) {
          logger.error(ex.getMessage());
        }
        break;
      case SHUTDOWN:
        try {
          this.clientFsm.fire(new EvntShutdown());
        } catch (FiniteStateMachineException ex) {
          logger.error(ex.getMessage());
        }
        break;
      default:
        logger.error("Invalid state [{}] encounterd.", transition.toString());
        break;

    }
  }
}
