package xephyrus.sam.core;

public interface MachineCycleController
{
  boolean shouldProcess ();
  boolean shouldYield ();
  long getYieldTime ();
}
