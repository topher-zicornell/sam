package xephyrus.sam.core;

public interface StateCycleListener<S extends Enum, P extends Payload>
{
  void beforeStateWork (S state, P payload);
  void afterStateWork (S state, P payload);
  void errorStateWork (S state, P payload, Throwable error);
}
