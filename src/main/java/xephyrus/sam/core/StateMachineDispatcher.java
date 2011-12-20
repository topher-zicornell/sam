package xephyrus.sam.core;

import xephyrus.sam.core.queue.ProcessingQueue;

import java.util.HashMap;
import java.util.Map;

public class StateMachineDispatcher
{
  public void registerStateMachine (Class<? extends Payload> payloadType, StateMachine machine)
  {
    if (_machines.containsKey(payloadType))
    {
      throw new IllegalArgumentException("Tried to add payload " + payloadType.getCanonicalName() +
          " twice");
    }
    _machines.put(payloadType,machine);
  }

  @SuppressWarnings({"unchecked"})
  public void process (Payload payload)
  {
    StateMachine machine = _machines.get(payload.getClass());
    if (machine == null)
    {
      throw new IllegalArgumentException("No machine has been registered for payload " +
          payload.getClass().getCanonicalName());
    }

    machine.process(payload);
  }

  private Map<Class<? extends Payload>,StateMachine> _machines =
      new HashMap<Class<? extends Payload>, StateMachine>();
}
