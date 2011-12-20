package xephyrus.sam.core.queue;

public class ProcessingQueueException
  extends Exception
{
  public ProcessingQueueException ()
  {
  }

  public ProcessingQueueException (String msg)
  {
    super(msg);
  }

  public ProcessingQueueException (String msg, Throwable cause)
  {
    super(msg, cause);
  }

  public ProcessingQueueException (Throwable cause)
  {
    super(cause);
  }
}
