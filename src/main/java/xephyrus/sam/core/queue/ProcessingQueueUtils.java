package xephyrus.sam.core.queue;

import xephyrus.sam.core.Payload;
import xephyrus.sam.core.ProcessInfo;
import xephyrus.sam.core.WaitingState;

/**
 * Some utilitarian functions for working with {@link ProcessingQueue}s.
 */
public class ProcessingQueueUtils
{
  /**
   * <p>
   *   Reports whether or not the specified queue item is in a waiting time.  Some
   *   {@link ProcessingQueue} implementations respect a waiting time associated with some states.
   *   This function can be used by those implementations to determine whether a specific item
   *   should currently be considered as <i>waiting</i>.
   * </p><p>
   *   Wait times are defined within the state, by implementing the {@link WaitingState} interface
   *   for the state itself.
   * </p>
   *
   * @param item
   *   The item to check.
   * @param <S>
   *   The state definition for the machine processing this item.
   * @param <P>
   *   The payload definition for the machine processing this item.
   * @return
   *   If the item is in a {@link WaitingState} state, and was last processed less than the wait
   *   time ago, true; otherwise false.
   */
  static public <S extends Enum,P extends Payload> boolean isProcessInfoWaiting (
      ProcessInfo<S,P> item)
  {
    if (item.getState() instanceof WaitingState)
    {
      long waitTill = System.currentTimeMillis() - ((WaitingState) item.getState()).getWaitTime();
      return waitTill < item.getLastCycle();
    }
    return false;
  }
}
