package xephyrus.sam.core.queue;

import xephyrus.sam.core.Payload;
import xephyrus.sam.core.ProcessInfo;
import xephyrus.sam.core.WaitingState;

public class ProcessingQueueUtils
{
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
