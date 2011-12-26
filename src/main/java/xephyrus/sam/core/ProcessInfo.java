package xephyrus.sam.core;

/**
 * A wrapper to associate a payload with it's current state.
 *
 * @param <S>
 *   The states of the {@link xephyrus.sam.core.StateMachine} for this set of info.
 * @param <P>
 *   The {@link Payload} of this {@link xephyrus.sam.core.StateMachine} for this set of info.
 */
public class ProcessInfo<S extends Enum, P extends Payload>
{
  /**
   * Creates a new one of these with the given payload and state.  And tracks when it was created
   * so things that need to track such things can.
   *
   * @param payload
   *   The payload.
   * @param state
   *   The state.
   */
  public ProcessInfo (P payload, S state)
  {
    _payload = payload;
    _state = state;
    _lastCycle = System.currentTimeMillis();
  }

  /**
   * Provides the payload for this set of info.
   *
   * @return
   *   The payload.
   */
  public P getPayload ()
  {
    return _payload;
  }

  /**
   * Provides the state for this set of info.
   *
   * @return
   *   The state.
   */
  public S getState ()
  {
    return _state;
  }

  /**
   * Reports when this set of info was created, which indicates when this payload was last
   * processed.
   *
   * @return
   *   The millisecond timestamp of when this payload was last processed.
   */
  public Long getLastCycle ()
  {
    return _lastCycle;
  }

  private P _payload;
  private S _state;
  private Long _lastCycle;
}
