package xephyrus.sam.core;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *   Provides a repository of {@link StateMachine}s.  This allows state machines to be registered
 *   by their payload type, and payloads to be submitted to the corresponding machine.  This can be
 *   useful when managing several different state machines
 * </p>
 */
public class StateMachineDispatcher
{
  /**
   * Registers into the dispatcher the given state machine for the given payload type.
   *
   * @param payloadType
   *   The payload type.  Note that only one state machine can be registered for any given type.
   * @param machine
   *   The state machine.
   */
  public void registerStateMachine (Class<? extends Payload> payloadType, StateMachine machine)
  {
    if (_machines.containsKey(payloadType))
    {
      throw new IllegalArgumentException("Tried to add payload " + payloadType.getCanonicalName() +
          " twice");
    }
    _machines.put(payloadType,machine);
  }

  /**
   * Submits the specified payload to it's corresponding state machine.  If no state machine could
   * be found to handle this payload, an exception will be thrown.
   *
   * @param payload
   *   The payload to submit.
   * @throws IllegalArgumentException
   *   Thrown when no state machine has been registered to handle the submitted payload.
   */
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
