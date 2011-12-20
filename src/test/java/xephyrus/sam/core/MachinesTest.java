package xephyrus.sam.core;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import xephyrus.sam.core.queue.MemoryProcessingQueue;
import xephyrus.sam.machines.abc.AbcPayload;
import xephyrus.sam.machines.abc.AbcState;
import xephyrus.sam.machines.abc.AbcStateMachineAnnotated;
import xephyrus.sam.machines.abc.AbcStateMachineRegistered;

import java.lang.Thread.State;
import java.lang.reflect.InvocationTargetException;

import static org.testng.Assert.assertEquals;

public class MachinesTest
{
  @BeforeClass
  public void prepareLogging ()
  {
    LogManager.getRootLogger().setLevel(Level.ALL);
    BasicConfigurator.configure();
  }

  @DataProvider(name = "MachineParameters")
  public Object[][] getMachineParameters ()
      throws NoSuchMethodException
  {
    return new Object[][] 
    {
      { AbcState.class, AbcPayload.class, new AbcStateMachineRegistered() },
      { AbcState.class, AbcPayload.class, new AbcStateMachineAnnotated() }
    };
  }

  State functionName (Payload payload)
  {
    return null;
  }

  @Test(dataProvider = "MachineParameters")
  public <S extends Enum, P extends Payload> void exerciseMachineMono (Class<S> stateType,
      Class<P> payloadType, StateMachine<S,P> machine)
      throws NoSuchMethodException, InterruptedException, InvocationTargetException,
      IllegalAccessException, InstantiationException
  {
    machine.setProcessingQueue(new MemoryProcessingQueue<S, P>());

    machine.process(payloadType.getConstructor(Long.class).newInstance(1L));
    assertEquals(machine.getProcessingQueue().getCount(), 1);

    machine.start();
    Thread.sleep(1000L);
    machine.requestStop();
    Thread.sleep(500L);

    assertEquals(machine.getProcessingQueue().getCount(),0);
  }

  @Test(dataProvider = "MachineParameters")
  public <S extends Enum, P extends Payload> void exerciseMachinePoly (Class<S> stateType,
      Class<P> payloadType, StateMachine<S,P> machine)
      throws NoSuchMethodException, InterruptedException, InvocationTargetException,
      IllegalAccessException, InstantiationException
  {
    machine.setProcessingQueue(new MemoryProcessingQueue<S, P>());

    for (long i = 0L; i < 50L; i++)
    {
      machine.process(payloadType.getConstructor(Long.class).newInstance(i));
    }
    assertEquals(machine.getProcessingQueue().getCount(),50);

    machine.start();
    Thread.sleep(1000L);
    machine.requestStop();
    Thread.sleep(500L);

    assertEquals(machine.getProcessingQueue().getCount(),0);
  }
}
