package xephyrus.sam.core;

/**
 * All payloads must implement this interface.  Besides marking the entity as a payload, it also
 * enforces that every payload must have an Id.
 */
public interface Payload
{
  /**
   * Identifies this payload.
   *
   * @return
   *   The id of this payload.  Note that if this returns null, the
   *   {@link StateMachine#process(Payload)} method will throw an {@link IllegalArgumentException}.
   */
  Long getId ();
}
