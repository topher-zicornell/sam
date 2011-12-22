package xephyrus.sam.core;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import xephyrus.sam.core.StateMachine.MachineCycles;
import xephyrus.sam.core.queue.MemoryProcessingQueue;
import xephyrus.sam.core.trackers.MachineCyclesTracker;
import xephyrus.sam.machines.testcontrol.TestControlExecutorService;
import xephyrus.sam.machines.testcontrol.TestControlMachine;
import xephyrus.sam.machines.testcontrol.TestControlPayload;
import xephyrus.sam.machines.testcontrol.TestControlState;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MachineCyclesTest
{
  @BeforeClass
  public void prepareLogging ()
  {
    LogManager.getRootLogger().setLevel(Level.ALL);
    BasicConfigurator.configure();
  }

  @DataProvider(name = "MachineCycleParameters")
  public Object[][] getMachineCycleParameters ()
  {
    return new Object[][]
    {
      { new TestControlPayload(TestControlState.Done), MachineCycles.STARTING, 1 },
      { new TestControlPayload(TestControlState.Done), MachineCycles.STOPPING, 1 },
      { new TestControlPayload(TestControlState.ThreeTimes), MachineCycles.BEFORE, 4 },
      { new TestControlPayload(TestControlState.ThreeTimes), MachineCycles.AFTER, 4 },
      { new TestControlPayload(TestControlState.SmileAndNod), MachineCycles.FULL, 0 },
      { new TestControlPayload(TestControlState.RejectFull), MachineCycles.FULL, -1 },
      { new TestControlPayload(TestControlState.SmileAndNod), MachineCycles.FAILING, 0 },
      { new TestControlPayload(TestControlState.RuntimeFail), MachineCycles.FAILING, 1 },
      { new TestControlPayload(TestControlState.SmileAndNod), MachineCycles.YIELD, -1 }
    };
  }

  @Test(dataProvider = "MachineCycleParameters")
  public void machineCycle (TestControlPayload payload, MachineCycles cycle, Integer count)
      throws InterruptedException
  {
    MachineCyclesTracker tracker = new MachineCyclesTracker();
    StateMachine<TestControlState,TestControlPayload> machine = new TestControlMachine();
    machine.setProcessingQueue(new MemoryProcessingQueue<TestControlState,TestControlPayload>());
    machine.setThreadPool(new TestControlExecutorService());
    machine.addMachineCycleListener(tracker);

    machine.process(payload);
    machine.start();
    Thread.sleep(1000L);
    machine.requestStop();
    Thread.sleep(500L);

    if (count < 0)
    {
      assertTrue(tracker.getCount(cycle) >= (-1 * count),cycle.toString());
    }
    else
    {
      assertEquals(tracker.getCount(cycle),count,cycle.toString());
    }
  }
}
