package xephyrus.sam.core;

/**
 * <p>
 *   Used to define a waiting state, and provide the amount of time that state should wait.  A
 *   waiting state is a state which should include a delay.  For example, when polling some
 *   external system, it can be destructive to poll that system as fast as possible.  There must
 *   be a wait time between each poll.
 * </p><p>
 *   A wait state is defined at the state by implementing this interface.  For example:
 *   <pre>
 *     public enum MeaningState
 *       implements WaitingState
 *     {
 *       ConnectToPlanetCalculator,
 *       SubmitQuestion,
 *       PollForAnswer(1000),
 *       TryToRememberQuestion;
 *
 *       private MeaningState ()
 *       {
 *         _waitTime = 0L;
 *       }
 *
 *       private MeaningState (long wait)
 *       {
 *         _waitTime = wait;
 *       }
 *
 *       public long getWaitTime ()
 *       {
 *         return _waitTime;
 *       }
 *
 *       private long _waitTime;
 *     }
 *   </pre>
 * </p><p>
 *   The WaitingState interface is handled in the {@link xephyrus.sam.core.queue.ProcessingQueue}.
 *   Support for this depends upon the implementation of the ProcessingQueue.
 * </p>
 */
public interface WaitingState
{
  /**
   * Provides the amount of time to wait before processing this state.
   *
   * @return
   *   The wait time, in milliseconds.
   */
  long getWaitTime ();
}
