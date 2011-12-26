package xephyrus.sam.core;

/**
 * <p>
 *   Defines a listener which will be notified at various stages throughout the main processing
 *   cycle of the {@link StateMachine}.  A cycle listener can be registered for a StateMachine
 *   using the {@link StateMachine#addMachineCycleListener(MachineCycleListener)} method.
 * </p><p>
 *   Figuring out where each individual call fits in the full cycle can be confusing, so here's
 *   a quick annotated psuedo-code outline of the processing cycle:
 *   <pre>
 *     try
 *     {
 *       machineCycleListener.{@link #startingMachine()};
 *       while (!stopping)
 *       {
 *         if (machineController.shouldProcess())
 *         {
 *           machineCycleListener.{@link #beforeCycle()};
 *           Item item = queue.popReady();
 *           if (item != null)
 *           {
 *             try
 *             {
 *               submitWorker(info);
 *             }
 *             catch (RejectedExecutionException cant)
 *             {
 *               machineCycleListener.{@link #fullMachine()};
 *               queue.push(item);
 *             }
 *           }
 *           machineCycleListener.{@link #afterCycle()};
 *         }
 *
 *         if (machineController.shouldYield())
 *         {
 *           machineCycleListener.{@link #yieldCycle()};
 *           sleep(machineController.getYieldTime());
 *         }
 *       }
 *     }
 *     catch (Throwable error)
 *     {
 *       machineCycleListener.{@link #failingMachine(Throwable)};
 *     }
 *     machineCycleListener.{@link #stoppingMachine()};
 *   </pre>
 * </p>
 */
public interface MachineCycleListener
{
  /**
   * Called immediately before the queue is checked for an item to process.
   */
  void beforeCycle ();

  /**
   * Called after the item processing thread has been launched, or if there is no item to process,
   * after the queue has been checked.
   */
  void afterCycle ();

  /**
   * Called immediately before the cycle yields due to {@link MachineCycleController#shouldYield()}
   * returning true.
   */
  void yieldCycle ();

  /**
   * Called as the {@link StateMachine} thread is starting up.
   */
  void startingMachine ();

  /**
   * Called as the {@link StateMachine} thread is shutting down.
   */
  void stoppingMachine ();

  /**
   * Called when a throwable error occurs anywhere in the {@link StateMachine} processing cycle.
   * Note that throwables that happen within the state worker methods are handled distinctly.
   *
   * @param error
   *   The error.
   */
  void failingMachine (Throwable error);

  /**
   * Called when an attempt is made to start a new state worker, but is rejected by the worker
   * thread manager.  Usually this indicates that the thread pool is currently full and unable
   * to handle new threads.
   */
  void fullMachine ();
}
