package xephyrus.sam.core;

public class ProcessInfo<S extends Enum, P extends Payload>
{
  public ProcessInfo (P payload, S state)
  {
    _payload = payload;
    _state = state;
    _lastCycle = System.currentTimeMillis();
  }

  public P getPayload ()
  {
    return _payload;
  }

  public S getState ()
  {
    return _state;
  }

  public Long getLastCycle ()
  {
    return _lastCycle;
  }

  private P _payload;
  private S _state;
  private Long _lastCycle;
}
