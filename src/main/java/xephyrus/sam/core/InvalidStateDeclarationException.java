package xephyrus.sam.core;

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
