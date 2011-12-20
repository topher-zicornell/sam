package xephyrus.sam.machines.abc;

import xephyrus.sam.core.StateMachine;
import xephyrus.sam.core.annotations.StartingState;
import xephyrus.sam.core.annotations.StateMachineState;

public class AbcStateMachineAnnotated
    extends StateMachine<AbcState,AbcPayload>
{
  public AbcStateMachineAnnotated ()
      throws NoSuchMethodException
  {
    super(AbcState.class, AbcPayload.class);
  }

  @StartingState
  @StateMachineState("A")
  public AbcState doA (AbcPayload payload)
  {
    int cycle = payload.cycle();
    if (cycle < 3)
    {
      return AbcState.A;
    }
    return AbcState.B;
  }

  @StateMachineState("B")
  public AbcState doB (AbcPayload payload)
  {
    int cycle = payload.cycle();
    if (cycle < 8)
    {
      return AbcState.B;
    }
    return AbcState.C;
  }
}
