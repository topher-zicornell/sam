package xephyrus.sam.core.queue;

import xephyrus.sam.core.Payload;
import xephyrus.sam.core.ProcessInfo;

public interface ProcessingQueue<S extends Enum, P extends Payload>
{
  void clear ();
  int getCount ();
  boolean isContained (ProcessInfo<S, P> item);
  boolean isReady ();
  ProcessInfo<S,P> peek ();
  ProcessInfo<S,P> peek (int index)
    throws ProcessingQueueException;
  ProcessInfo<S,P> pop ();
  ProcessInfo<S,P> popReady ();
  void push (ProcessInfo<S,P> item)
    throws ProcessingQueueException;
  void queue (ProcessInfo<S,P> item)
    throws ProcessingQueueException;
  ProcessInfo<S,P> remove (int index);
}
