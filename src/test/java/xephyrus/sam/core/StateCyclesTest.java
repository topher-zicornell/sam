package xephyrus.sam.core;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import xephyrus.sam.core.StateMachine.StateCycles;
import xephyrus.sam.core.queue.MemoryProcessingQueue;
import xephyrus.sam.core.trackers.StateCyclesTracker;
import xephyrus.sam.machines.testcontrol.TestControlExecutorService;
import xephyrus.sam.machines.testcontrol.TestControlMachine;
import xephyrus.sam.machines.testcontrol.TestControlPayload;
import xephyrus.sam.machines.testcontrol.TestControlState;

import static org.testng.Assert.assertEquals;

public class StateCyclesTest
{
  @BeforeClass
  public void prepareLogging ()
  {
    LogManager.getRootLogger().setLevel(Level.ALL);
    BasicConfigurator.configure();
  }

  @DataProvider(name = "StateCycleParameters")
  public Object[][] getStateCycleParameters ()
  {
    return new Object[][]
    {
      { new TestControlPayload(TestControlState.ThreeTimes), StateCycles.BEFORE, 3 },
      { new TestControlPayload(TestControlState.ThreeTimes), StateCycles.AFTER, 3 },
      { new TestControlPayload(TestControlState.WorkError), StateCycles.ERROR, 1 }
    };
  }

  @Test(dataProvider = "StateCycleParameters")
  public void stateCycle (TestControlPayload payload, StateCycles cycle, Integer count)
      throws InterruptedException
  {
    StateCyclesTracker<TestControlState,TestControlPayload> tracker =
        new StateCyclesTracker<TestControlState,TestControlPayload>();
    StateMachine<TestControlState,TestControlPayload> machine = new TestControlMachine();
    machine.setProcessingQueue(new MemoryProcessingQueue<TestControlState,TestControlPayload>());
    machine.setThreadPool(new TestControlExecutorService());
    machine.addStateCycleListener(tracker);

    machine.process(payload);
    machine.start();
    Thread.sleep(1000L);
    machine.requestStop();
    Thread.sleep(500L);

    assertEquals(tracker.getCount(cycle),count,cycle.toString());
  }
}
