package xephyrus.sam.core;

public interface MachineCycleListener
{
  void beforeCycle ();
  void afterCycle ();
  void yieldCycle ();
  void startingMachine ();
  void stoppingMachine ();
  void failingMachine (Throwable error);
  void fullMachine ();
}
