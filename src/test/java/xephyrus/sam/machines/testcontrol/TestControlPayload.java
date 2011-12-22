package xephyrus.sam.machines.testcontrol;

import xephyrus.sam.core.Payload;

public class TestControlPayload
  implements Payload
{
  public TestControlPayload (TestControlState workState)
  {
    _workState = workState;
  }

  @Override
  public Long getId ()
  {
    return 0L;
  }

  public TestControlState getWorkState ()
  {
    return _workState;
  }

  public void setWorkState (TestControlState workState)
  {
    _workState = workState;
  }

  private TestControlState _workState;
}
