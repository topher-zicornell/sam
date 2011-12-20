package xephyrus.sam.machines.abc;

import xephyrus.sam.core.StateMachine;

public class AbcStateMachineRegistered
  extends StateMachine<AbcState,AbcPayload>
{
  public AbcStateMachineRegistered ()
      throws NoSuchMethodException
  {
    super(AbcState.class,AbcPayload.class);
    registerState(AbcState.A,getClass().getMethod("doA",AbcPayload.class));
    registerState(AbcState.B,getClass().getMethod("doB",AbcPayload.class));
  }

  public AbcState doA (AbcPayload payload)
  {
    int cycle = payload.cycle();
    if (cycle < 3)
    {
      return AbcState.A;
    }
    return AbcState.B;
  }

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
