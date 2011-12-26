/*
    This software is licensed under the MIT License.
    
    Copyright (c) 2010-2011 by Topher ZiCornell.  All rights reserved.

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
 */
package xephyrus.sam.core;

import xephyrus.sam.core.annotations.StartingState;
import xephyrus.sam.core.annotations.StateMachineState;
import xephyrus.sam.core.queue.ProcessingQueue;
import xephyrus.sam.core.queue.ProcessingQueueException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

/**
 * The base for all state machines.  Every state machine must extend from this class.
 *
 * Initialization of a state machine should follow these steps:
 * <ul>
 *   <li>
 *       Call the super to register the types:
 *       <br/><pre>super(stateType,payloadType);</pre>
 *   </li>
 *   <li>
 *       Register all the needed methods for the working states:
 *       <br/><pre>registerState(StateType.OhMy,"doOhMy");</pre>
 *   </li>
 *   <li>
 *       Register any needed completion listeners:
 *       <br/><pre>registerCompletionListener(new CompleteIt());</pre>
 *       <br/><br/>
 *       I usually work the completion code into the state machine along side the worker methods
 *       because, well, that just makes sense to me.  So:
 *       <br/><pre>registerCompletionListener(this);</pre>
 *   </li>
 *   <li>
 *       Register a processing queue:
 *       <br/><pre>setProcessQueue(new MemoryProcessQueue());</pre>
 *       <br/><br/>
 *       Ideally this would be set through something like Spring, or some configuration tool.
 *   </li>
 *   <li>
 *       Start the machine:
 *       <br/><pre>start();</pre>
 *   </li>
 * </ul>
 *
 * @param <S>
 *     An enum of all the valid states for this state machine.  If no start state is set, the first
 *     element in this enum will be considered the start state.  There are no restrictions on the
 *     enum itself.
 * @param <P>
 *     The "payload" for this state machine.  The payload is what is processed by the state
 *     machine.  It is also the object which tracks the execution details as it progresses through
 *     the state machine.
 */
public class StateMachine<S extends Enum, P extends Payload>
  extends Thread
{
  /**
   * A strongly-typed list of the possible triggers from within the StateMachine processing cycle.
   */
  public enum MachineCycles
  {
    AFTER,
    BEFORE,
    FAILING,
    FULL,
    STARTING,
    STOPPING,
    YIELD
  }

  /**
   * A strongly-typed list of the possible triggers from within the state worker executor.
   */
  public enum StateCycles
  {
    AFTER,
    BEFORE,
    ERROR
  }

  /**
   * The executor for the state workers.  This thread handles execution of a single state worker
   * execution on a single payload.
   */
  public class StateWorker
    extends Thread
  {
    /**
     * Creates a new one of these for the given {@link ProcessInfo} object, which contains the
     * state and payload.
     *
     * @param info
     *   The worker set.
     */
    public StateWorker (ProcessInfo<S, P> info)
    {
      _info = info;
    }

    /**
     * The state worker executor process.
     */
    @SuppressWarnings({"unchecked"})
    @Override
    public void run ()
    {
      try
      {
        Method method = _stateMap.get(_info.getState());
        S afterState = null;
        if (method != null)
        {
          triggerStateCycle(StateCycles.BEFORE, _info.getState(), _info.getPayload());
          afterState = (S) method.invoke(_machine, _info.getPayload());
          if (afterState != null)
          {
            _processingQueue.push(new ProcessInfo<S,P>(_info.getPayload(),afterState));
          }
          triggerStateCycle(StateCycles.AFTER,afterState, _info.getPayload());
        }
        if ((method == null) || (afterState == null))
        {
          triggerNotifyComplete(_info.getPayload(), _info.getState(),null);
        }
      }
      catch (Throwable cant)
      {
        triggerStateCycle(StateCycles.ERROR, _info.getState(), _info.getPayload(),
            (cant instanceof InvocationTargetException ?
                ((InvocationTargetException) cant).getTargetException() : cant));
        triggerNotifyComplete(_info.getPayload(), _info.getState(),
            (cant instanceof InvocationTargetException ?
                ((InvocationTargetException) cant).getTargetException() : cant));
      }
    }

    /**
     * Provides the wrapped state and payload info.
     *
     * @return
     *   The worker set.
     */
    public ProcessInfo<S, P> getInfo ()
    {
      return _info;
    }

    private ProcessInfo<S,P> _info;
  }

  /**
   * Initializes things for this state machine with the class definitions for the state enum and
   * the payload class.
   *
   * @param stateType
   *   The state enum.
   * @param payloadType
   *   The payload class.
   */
  public StateMachine (Class<S> stateType, Class<P> payloadType)
  {
    _stateType = stateType;
    _payloadType = payloadType;
    _stopRequested = false;
    _machine = this;
    registerAnnotatedStates();
  }

  /**
   * Initializes things for this state machine with the class definitions for the state enum and
   * payload class, and using the specified machine object as the source for the worker methods.
   *
   * @param stateType
   *   The state enum.
   * @param payloadType
   *   The payload type.
   * @param machine
   *   The machine worker method source.
   */
  public StateMachine (Class<S> stateType, Class<P> payloadType, Object machine)
  {
    _stateType = stateType;
    _payloadType = payloadType;
    _stopRequested = false;
    _machine = machine;
    registerAnnotatedStates();
  }

  /**
   * Registers a listener to be informed whenever processing of a payload completes.  The listener
   * will be called regardless of whether the processing ends in an exception, a null state or a
   * proper end-state.
   *
   * @param listener
   *     The listener to be registered for completion callbacks.
   */
  public final void addCompletionListener (CompletionListener<S, P> listener)
  {
    _completionListeners.add(listener);
  }

  /**
   * Registers a listener to be informed at various stages throughout the StateMachine processing
   * cycle.
   *
   * @param machineCycleListener
   *   The listener to be registered.
   */
  public void addMachineCycleListener (MachineCycleListener machineCycleListener)
  {
    _machineCycleListeners.add(machineCycleListener);
  }

  /**
   * Registers a listener to be informed around when state processing is executed.
   *
   * @param stateCycleListener
   *   The listener to be registered.
   */
  public void addStateCycleListener (StateCycleListener<S,P> stateCycleListener)
  {
    _stateCycleListeners.add(stateCycleListener);
  }

  /**
   * Provides the machine cycle controller for this state machine.
   *
   * @return
   *   The machine cycle controller.
   */
  public MachineCycleController getMachineCycleController ()
  {
    _cycleController = (_cycleController == null ?
        new DefaultMachineCycleController<S,P>(this) :
        _cycleController);
    return _cycleController;
  }

  /**
   * Sets the machine cycle controller to be used in governing the processing cycle of this state
   * machine.
   *
   * @param cycleController
   *   The machine cycle controller.
   */
  public void setMachineCycleController (MachineCycleController cycleController)
  {
    _cycleController = cycleController;
  }

  /**
   * Provides direct access to the process queue.
   *
   * @return
   *   The process queue, raw and dangerous.
   */
  public ProcessingQueue<S, P> getProcessingQueue ()
  {
    return _processingQueue;
  }

  /**
   * Sets the process queue to be used by this state machine.  For simple and non-robust
   * reference or academic type applications, you can use {@link
   * xephyrus.sam.core.queue.MemoryProcessingQueue}.
   *
   * @param processingQueue
   *     The process queue to use for this state machine.
   */
  public final void setProcessingQueue (ProcessingQueue<S, P> processingQueue)
  {
    _processingQueue = processingQueue;
  }

  /**
   * Provides the start state for new payloads.  Any new payloads registered with this state
   * machine will initially be in the state given by this method.
   *
   * @return
   *     The start state for new payloads.
   */
  public final S getStartState ()
  {
    if (_startState == null)
    {
      _startState = _stateType.getEnumConstants()[0];
    }
    return _startState;
  }

  /**
   * Determines the start state for new payloads.  If this isn't set, the first state in the state
   * enum is considered as the start state.
   *
   * @param state
   *     The state to use as the start state for new payloads.
   */
  public final void setStartState (S state)
  {
    _startState = state;
  }

  /**
   * Provides the current thread pool for the state worker threads.
   *
   * @return
   *   The state worker's thread pool.
   */
  public ExecutorService getThreadPool ()
  {
    return _threadPool;
  }

  /**
   * Sets the thread pool for the state worker threads.
   *
   * @param threadPool
   *   The state worker's thread pool.
   */
  public void setThreadPool (ExecutorService threadPool)
  {
    _threadPool = threadPool;
  }

  /**
   * Register a new payload for processing.  This submits a payload to the state machine.  The
   * state machine will the pass this payload to the registered methods for each state it
   * encounters until an end-state condition or an exception condition occurs.
   *
   * Every payload must have a valid id.
   *
   * @param payload
   *     The payload to be processed.
   */
  public final void process (P payload)
  {
    if (payload != null)
    {
      if (payload.getId() == null)
      {
        throw new IllegalArgumentException("Payload type " + payload.getClass().getCanonicalName() +
            " has a null id");
      }
      ProcessInfo<S,P> info = new ProcessInfo<S,P>(payload,getStartState());
      try
      {
        _processingQueue.queue(info);
      }
      catch (ProcessingQueueException e)
      {
        triggerStateCycle(StateCycles.ERROR,info.getState(),info.getPayload(),e);
        triggerNotifyComplete(info.getPayload(),info.getState(),e);
      }
    }
  }

  /**
   * Registers a state with this state machine.  This defines what method gets executed when
   * processing of a payload is in this specific state.
   *
   * If the registered method is null or if no method is defined for a state, that state is
   * considered an end state.  Transition to such a state will signal the end of processing for
   * that payload.
   *
   * The method given must be in this class (sub-class, of course), and the method signature must
   * be:
   * <br/><pre>S method (P payload)</pre><br/>
   *
   * The return value of this method is the state to transition to.  Returning null will also
   * signal the end of processing for that payload.
   *
   * This method may throw any exceptions.  Any exceptions thrown will signal the end of
   * processing for that payload.  The exception will be passed to any completion listeners.
   *
   * @param state
   *     The state to register.
   * @param method
   *     The method to execute for this state.  The return value of this method is the state to
   *     transition to.
   */
  public final void registerState (S state, Method method)
  {
    _stateMap.put(state, method);
  }

  /**
   * Registers a state with this state machine.  This gives a handy way to register methods by
   * name.  Since the signature of the method is fixed (it must be <pre>S method (P payload)</pre>),
   * and the method must be in this class, finding the method by name is easy peasy.
   *
   * This is the same as calling:
   * <br/><pre>registerState(state,getClass().getMethod(methodName,payloadType));</pre><br/>
   *
   * @param state
   *     The state to register.
   * @param methodName
   *     The method to execute for this state.  The return value of this method is the state to
   *     transition to.
   * @throws NoSuchMethodException
   *     If no method can be found with the given name and the correct signature, a big nasty
   *     NoSuchMethodException is thrown.
   */
  public final void registerState (S state, String methodName)
      throws NoSuchMethodException
  {
    registerState(state, getClass().getMethod(methodName, (Class) _payloadType));
  }

  /**
   * Registers a request that this state machine be stopped.  This request is passed to the main
   * execution thread for this state machine.  The state machine will stop on it's next cycle.
   */
  public final void requestStop ()
  {
    _stopRequested = true;
  }

  /**
   * This is the main execution thread for this state machine.  Once started, this will execute
   * until a stop is requested.  This thread will handle one state execution for one payload per
   * cycle.
   */
  @SuppressWarnings({"unchecked"})
  @Override
  public final void run ()
  {
    try
    {
      triggerMachineCycle(MachineCycles.STARTING);
      while (!_stopRequested)
      {
        if (getMachineCycleController().shouldProcess())
        {
          triggerMachineCycle(MachineCycles.BEFORE);
          ProcessInfo<S,P> info = _processingQueue.popReady();
          if (info != null)
          {
            try
            {
              _threadPool.submit(new StateWorker(info));
            }
            catch (RejectedExecutionException cant)
            {
              triggerMachineCycle(MachineCycles.FULL);
              _processingQueue.push(info);
            }
          }
          triggerMachineCycle(MachineCycles.AFTER);
        }

        if (getMachineCycleController().shouldYield())
        {
          triggerMachineCycle(MachineCycles.YIELD);
          Thread.sleep(getMachineCycleController().getYieldTime());
        }
      }
    }
    catch (Throwable fail)
    {
      triggerMachineCycle(MachineCycles.FAILING,fail);
    }
    triggerMachineCycle(MachineCycles.STOPPING);
  }

  /**
   * Automatically registers any methods which are annotated as StateWorkers.
   */
  @SuppressWarnings({"unchecked", "RedundantCast"})
  protected final void registerAnnotatedStates ()
  {
    for (Method method: _machine.getClass().getMethods())
    {
      if (method.isAnnotationPresent(StateMachineState.class))
      {
        StateMachineState ann = method.getAnnotation(StateMachineState.class);
        S state = (S) Enum.valueOf(_stateType,ann.value());
        registerState(state,method);

        if (method.isAnnotationPresent(StartingState.class))
        {
          setStartState(state);
        }
      }
      else if (method.isAnnotationPresent(StartingState.class))
      {
        throw new InvalidStateDeclarationException("Method " + method.getName() + " is " +
            "annotated as a StartingState, but not as a StateMachineState.");
      }
    }
  }

  /**
   * Triggers a notification to each of the registered machine cycle listeners.
   *
   * @param cycle
   *   Identifies the notification to be triggered.
   */
  protected final void triggerMachineCycle (MachineCycles cycle)
  {
    for (MachineCycleListener listener: _machineCycleListeners)
    {
      switch (cycle)
      {
        case AFTER:
          listener.afterCycle();
          break;

        case BEFORE:
          listener.beforeCycle();
          break;

        case STARTING:
          listener.startingMachine();
          break;

        case STOPPING:
          listener.stoppingMachine();
          break;

        case FULL:
          listener.fullMachine();
          break;

        case YIELD:
          listener.yieldCycle();
          break;
      }
    }
  }

  /**
   * Triggers a notification to each of the registered machine cycle listeners.  This is really
   * only useful for the {@link MachineCycleListener#failingMachine(Throwable)} notification,
   * however it will gracefully defer to
   * {@link #triggerMachineCycle(xephyrus.sam.core.StateMachine.MachineCycles)} if it's called for
   * any other notifications.
   *
   * @param cycle
   *   The notification to be triggered.  (Should be {@link MachineCycles#FAILING}.)
   * @param fail
   *   The exception to send with the notification.
   */
  protected final void triggerMachineCycle (MachineCycles cycle, Throwable fail)
  {
    if (cycle == MachineCycles.FAILING)
    {
      for (MachineCycleListener listener: _machineCycleListeners)
      {
        listener.failingMachine(fail);
      }
    }
    else
    {
      triggerMachineCycle(cycle);
    }
  }

  /**
   * Triggers callbacks to notify any completion listeners that a payload has completed.
   *
   * @param payload
   *     The payload which has completed.
   * @param lastState
   *     The last valid state of this payload.  This will be the end-state if an end-state was
   *     reached.  If a state processing method returned null as the next state, it will be the
   *     state which caused the null.  If an exception was encountered, this will be the state
   *     that was being processed when the exception occurred.
   * @param error
   *     The exception that caused processing of this payload to end, if any.
   */
  @SuppressWarnings({"unchecked"})
  protected final void triggerNotifyComplete (P payload, S lastState, Throwable error)
  {
    for (CompletionListener listener: _completionListeners)
    {
      listener.notifyComplete(payload,lastState,error);
    }
  }

  /**
   * Triggers a notification to each of the registered state cycle listeners.
   *
   * @param cycle
   *   The notification to be triggered.
   * @param state
   *   The state for this notification.
   * @param payload
   *   The payload for this notification.
   */
  protected final void triggerStateCycle (StateCycles cycle, S state, P payload)
  {
    for (StateCycleListener<S,P> listener: _stateCycleListeners)
    {
      switch (cycle)
      {
        case AFTER:
          listener.afterStateWork(state,payload);
          break;

        case BEFORE:
          listener.beforeStateWork(state,payload);
          break;
      }
    }
  }

  /**
   * Triggers a notification to each of the registered state cycle listeners.  This is really only
   * useful for the {@link StateCycleListener#errorStateWork(Enum, Payload, Throwable)}
   * notification, however it will gracefully defer to
   * {@link #triggerStateCycle(xephyrus.sam.core.StateMachine.StateCycles, Enum, Payload)} if it's
   * called for any other notifications.
   *
   * @param cycle
   *   The notification to be triggered.
   * @param state
   *   The state for this notification.
   * @param payload
   *   The payload for this notification.
   * @param error
   *   The exception for this notification.
   */
  protected final void triggerStateCycle (StateCycles cycle, S state, P payload, Throwable error)
  {
    if (cycle == StateCycles.ERROR)
    {
      for (StateCycleListener<S,P> listener: _stateCycleListeners)
      {
        listener.errorStateWork(state,payload,error);
      }
    }
    else
    {
      triggerStateCycle(cycle,state,payload);
    }
  }

  private S _startState;
  private Class<S> _stateType;
  private Class<P> _payloadType;
  private Object _machine;
  private Map<S,Method> _stateMap = new HashMap<S,Method>();
  private ProcessingQueue<S,P> _processingQueue;
  private MachineCycleController _cycleController;
  private boolean _stopRequested;
  private List<CompletionListener<S,P>> _completionListeners =
      new LinkedList<CompletionListener<S,P>>();
  private List<MachineCycleListener> _machineCycleListeners =
      new LinkedList<MachineCycleListener>();
  private List<StateCycleListener<S,P>> _stateCycleListeners =
      new LinkedList<StateCycleListener<S, P>>();
  private ExecutorService _threadPool = new ThreadPoolExecutor(10,10,60L,TimeUnit.SECONDS,
      new SynchronousQueue<Runnable>(),new AbortPolicy());
}
