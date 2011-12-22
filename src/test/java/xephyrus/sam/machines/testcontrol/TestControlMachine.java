package xephyrus.sam.machines.testcontrol;

import xephyrus.sam.core.StateMachine;
import xephyrus.sam.core.annotations.StartingState;
import xephyrus.sam.core.annotations.StateMachineState;

public class TestControlMachine
  extends StateMachine<TestControlState,TestControlPayload>
{
  public TestControlMachine ()
  {
    super(TestControlState.class,TestControlPayload.class);
  }

  @StartingState
  @StateMachineState("SmileAndNod")
  public TestControlState doSmileAndNod (TestControlPayload payload)
  {
    TestControlState nextState = payload.getWorkState();
    payload.setWorkState(TestControlState.Done);
    return nextState;
  }

  @StateMachineState("ThreeTimes")
  public TestControlState doThreeTimes (TestControlPayload payload)
  {
    return TestControlState.SmileAndNod;
  }

  @StateMachineState("RejectFull")
  public TestControlState doRejectFull (TestControlPayload payload)
  {
    return doSmileAndNod(payload);
  }

  @StateMachineState("RuntimeFail")
  public TestControlState doRuntimeFail (TestControlPayload payload)
  {
    return doSmileAndNod(payload);
  }

  @StateMachineState("WorkError")
  public TestControlState doWorkError (TestControlPayload payload)
      throws Exception
  {
    throw new Exception("TestControlState.WorkError");
  }

  @StateMachineState("Yield")
  public TestControlState doYield (TestControlPayload payload)
  {
    return doSmileAndNod(payload);
  }
}
