package xephyrus.sam.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *   Identifies the starting state.  Use this on the state worker method alongside the
 *   {@link StateMachineState} annotation.  For example:
 *   <pre>
 *     &#64;StartingState
 *     &#64;StateMachineState("MyFirstState")
 *     public MyState doWorkForMyFirstState (MyPayload payload)
 *     {
 *       :
 *     }
 *   </pre>
 * </p><p>
 *   If more than one state is marked with this annotation, one will be arbitrarily chosen (probably
 *   the last one).
 *   If this annotation is used on a method that is not marked with the
 *   {@link StateMachineState} annotation, an {@link xephyrus.sam.core.InvalidStateDeclarationException}
 *   will be thrown when the {@link xephyrus.sam.core.StateMachine} is instantiated.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StartingState
{
}
