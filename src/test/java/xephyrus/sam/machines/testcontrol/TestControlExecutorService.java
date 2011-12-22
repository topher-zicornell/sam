package xephyrus.sam.machines.testcontrol;

import xephyrus.sam.core.ProcessInfo;
import xephyrus.sam.core.StateMachine.StateWorker;

import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestControlExecutorService
  extends ThreadPoolExecutor
{
  public TestControlExecutorService ()
  {
    super(10, 10, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new AbortPolicy());
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public Future<?> submit (Runnable runnable)
  {
    if (runnable instanceof StateWorker)
    {
      StateWorker worker = (StateWorker) runnable;
      ProcessInfo info = worker.getInfo();
      if (info.getState() == TestControlState.RejectFull)
      {
        throw new RejectedExecutionException("TestControlState.RejectFull");
      }
      if (info.getState() == TestControlState.RuntimeFail)
      {
        throw new RuntimeException("TestControlState.Fail");
      }
    }
    return super.submit(runnable);
  }
}
