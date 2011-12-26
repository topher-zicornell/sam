package xephyrus.sam.core;

/**
 * <p>
 *   Defines a controller for governing the processing cycle of a {@link StateMachine}.  A
 *   MachineCycleController can be registered with any given StateMachine using the
 *   {@link StateMachine#setMachineCycleController(MachineCycleController)} method.
 * </p><p>
 *   The MachineCycleController registered for a StateMachine determines whether or not the
 *   StateMachine should launch new processing steps, yield control (ie, sleep), and for how long
 *   it should yield.
 * </p>
 */
public interface MachineCycleController
{
  /**
   * Reports whether or not the {@link StateMachine} should launch new processing steps.  When this
   * is true, the StateMachine will grab the next item ready to be processed off the
   * {@link xephyrus.sam.core.queue.ProcessingQueue} and grab a worker thread from the worker
   * thread pool to process it.
   *
   * @return
   *   If it is ok to launch new processing steps, true; otherwise false.
   */
  boolean shouldProcess ();

  /**
   * Reports whether or not the {@link StateMachine} should yield processing to other threads for
   * a time.  When this is true, the StateMachine will go to sleep for the amount of time specified
   * by the {@link #getYieldTime()} method.
   *
   * @return
   *   If the StateMachine should yield, true; otherwise false.
   */
  boolean shouldYield ();

  /**
   * Provides the amount of time for which the {@link StateMachine} should sleep each time
   * it yields.
   *
   * @return
   *   The number of milliseconds for the StateMachine to sleep.
   */
  long getYieldTime ();
}
