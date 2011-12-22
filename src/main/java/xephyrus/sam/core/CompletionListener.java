package xephyrus.sam.core;

public interface CompletionListener<S extends Enum, P extends Payload>
{
  void notifyComplete (P payload, S lastState, Throwable error);
}
