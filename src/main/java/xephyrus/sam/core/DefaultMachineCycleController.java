package xephyrus.sam.core;

/**
 * A simple, default controller for governing the processing cycle of {@link StateMachine}s.  This
 * implementation will process as long as the {@link xephyrus.sam.core.queue.ProcessingQueue} has
 * something to process.  When there's nothing to process, it will yield for 100 milliseconds at a
 * time, or whatever the yield time has been set to.
 *
 * @param <S>
 *   The states of the {@link StateMachine} for which this controller will work.
 * @param <P>
 *   The {@link Payload} of the {@link StateMachine} for which this controller will work.
 */
public class DefaultMachineCycleController<S extends Enum, P extends Payload>
  implements MachineCycleController
{
  /**
   * Constructs a new one of these for the given {@link StateMachine}.
   *
   * @param machine
   *   The StateMachine for which this controller works.
   */
  public DefaultMachineCycleController (StateMachine<S,P> machine)
  {
    _machine = machine;
  }

  @Override
  public boolean shouldProcess ()
  {
    return _machine.getProcessingQueue().isReady();
  }

  @Override
  public boolean shouldYield ()
  {
    return !shouldProcess();
  }

  @Override
  public long getYieldTime ()
  {
    return (_yieldTime != null ? _yieldTime : 100L);
  }

  /**
   * Sets the amount of time this controller will tell the {@link StateMachine} to sleep when it
   * yields.
   *
   * @param yieldTime
   *   The amount of time to sleep, in milliseconds.
   */
  public void setYieldTime (Long yieldTime)
  {
    _yieldTime = yieldTime;
  }

  private StateMachine<S,P> _machine;
  private Long _yieldTime;
}
