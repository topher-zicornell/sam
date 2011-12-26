package xephyrus.sam.core;

/**
 * Defines a callback which can be registered with a {@link StateMachine} to be called when
 * that StateMachine stops processing any item.  Implementations of this listener can be registered
 * with a StateMachine of choice using the
 * {@link StateMachine#addCompletionListener(CompletionListener)} method.
 *
 * @param <S>
 *   The states of the {@link StateMachine} to which this listener will listen.
 * @param <P>
 *   The {@link Payload} of the {@link StateMachine} to which this listener will listen.
 */
public interface CompletionListener<S extends Enum, P extends Payload>
{
  /**
   * This method will be called whenever a payload which was set to be processed by the
   * {@link StateMachine} is no longer being processed, whether that's due to completion or error.
   *
   * @param payload
   *   The payload which is no longer being processed.
   * @param lastState
   *   The last state the StateMachine had for this payload.
   * @param error
   *   If this payload is no longer being processed because of an error, this is the error.
   *   Otherwise this will be null.
   */
  void notifyComplete (P payload, S lastState, Throwable error);
}
