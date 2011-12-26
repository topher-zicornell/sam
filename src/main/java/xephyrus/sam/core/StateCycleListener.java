package xephyrus.sam.core;

/**
 * <p>
 *   Defines a listener which will be notified at various stages through the processing of each
 *   state.  A state listener can be registered for a StateMachine using the
 *   {@link StateMachine#addStateCycleListener(StateCycleListener)} method.
 * </p>
 *
 * @param <S>
 *   The states of the {@link StateMachine} to which this listener will listen.
 * @param <P>
 *   The {@link Payload} of the {@link StateMachine} to which this listener will listen.
 */
public interface StateCycleListener<S extends Enum, P extends Payload>
{
  /**
   * Called immediately before a state worker is executed.
   *
   * @param state
   *   The state of the worker being executed.
   * @param payload
   *   The payload being processed.
   */
  void beforeStateWork (S state, P payload);

  /**
   * Called immediately after a state worker completes it's execution.  If the worker throws an
   * exception, {@link #errorStateWork(Enum, Payload, Throwable)} is called instead of this method.
   *
   * @param state
   *   The new state, after the worker execution has completed.
   * @param payload
   *   The payload being processed.
   */
  void afterStateWork (S state, P payload);

  /**
   * Called when a state worker method throws an exception.
   *
   * @param state
   *   The state of the worker which threw the exception.
   * @param payload
   *   The payload being processed.
   * @param error
   *   The exception that was thrown.
   */
  void errorStateWork (S state, P payload, Throwable error);
}
