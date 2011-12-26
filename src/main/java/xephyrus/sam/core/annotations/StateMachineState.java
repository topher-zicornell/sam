package xephyrus.sam.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *   Identifies a state worker method.  Use this to mark the methods that perform the work for each
 *   state.  For example:
 *   <pre>
 *     &#64;StateMachineState("MyHappyState")
 *     public MyState doWorkForMyHappyState (MyPayload payload)
 *     {
 *       :
 *     }
 *   </pre>
 * </p><p>
 *   If more than one worker is associated with any given state, one will be arbitrarily chosen
 *   (probably the last one).
 *   If the state specified is not a valid state, an {@link IllegalArgumentException} will be
 *   thrown when the {@link xephyrus.sam.core.StateMachine} is instantiated.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StateMachineState
{
  /**
   * Identifies the state for which this method is the worker.
   * @return The state.
   */
  String value();
}
