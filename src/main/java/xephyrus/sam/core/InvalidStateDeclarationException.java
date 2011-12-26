package xephyrus.sam.core;

/**
 * This is thrown by the {@link StateMachine} during initialization when a
 * {@link xephyrus.sam.core.annotations.StateMachineState} annotation is defined with a state name
 * that doesn't exist in the corresponding S Enum for that StateMachine.
 */
public class InvalidStateDeclarationException
  extends RuntimeException
{
  public InvalidStateDeclarationException ()
  {
  }

  public InvalidStateDeclarationException (String msg)
  {
    super(msg);
  }

  public InvalidStateDeclarationException (String msg, Throwable cause)
  {
    super(msg,cause);
  }

  public InvalidStateDeclarationException (Throwable cause)
  {
    super(cause);
  }
}
