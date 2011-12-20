package xephyrus.sam.core.queue;

import xephyrus.sam.core.Payload;
import xephyrus.sam.core.ProcessInfo;

import java.util.LinkedList;

public class MemoryProcessingQueue<S extends Enum, P extends Payload>
  implements ProcessingQueue<S,P>
{
  @Override
  public void clear ()
  {
    _queue.clear();
  }

  @Override
  public int getCount ()
  {
    return _queue.size();
  }

  @Override
  public boolean isContained (ProcessInfo<S, P> item)
  {
    return _queue.contains(item);
  }

  @Override
  public boolean isReady ()
  {
    return ((_queue.size() > 0) && (!ProcessingQueueUtils.isProcessInfoWaiting(_queue.peek())));
  }

  @Override
  public ProcessInfo<S, P> peek ()
  {
    return _queue.peek();
  }

  @Override
  public ProcessInfo<S, P> peek (int index)
  {
    if (index < _queue.size())
    {
      return _queue.get(index);
    }
    return null;
  }

  @Override
  public ProcessInfo<S, P> pop ()
  {
    return _queue.pop();
  }

  @Override
  public ProcessInfo<S, P> popReady ()
  {
    if (isReady())
    {
      return pop();
    }
    return null;
  }

  @Override
  public void push (ProcessInfo<S, P> item)
  {
    _queue.add(item);
  }

  @Override
  public void queue (ProcessInfo<S, P> item)
  {
    _queue.add(item);
  }

  @Override
  public ProcessInfo<S, P> remove (int index)
  {
    if (index < _queue.size())
    {
      return _queue.remove(index);
    }
    return null;
  }

  private LinkedList<ProcessInfo<S,P>> _queue = new LinkedList<ProcessInfo<S,P>>();
}
