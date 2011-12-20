package xephyrus.sam.support;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import xephyrus.sam.core.MachineCycleListener;

public class MachineCycleLogger
  implements MachineCycleListener
{
  @Override
  public void beforeCycle ()
  {
    getLog().log(getBeforeLevel(),"StateMachine processing");
  }

  @Override
  public void afterCycle ()
  {
    getLog().log(getAfterLevel(),"StateMachine done processing");
  }

  @Override
  public void failingMachine (Throwable error)
  {
    getLog().log(getFailingLevel(),"StateMachine failed",error);
  }

  @Override
  public void fullMachine ()
  {
    getLog().log(getFullLevel(),"StateMachine thread pool is full");
  }

  @Override
  public void startingMachine ()
  {
    getLog().log(getStartingLevel(),"StateMachine starting");
  }

  @Override
  public void stoppingMachine ()
  {
    getLog().log(getStoppingLevel(),"StateMachine stopping");
  }

  @Override
  public void yieldCycle ()
  {
    getLog().log(getYieldLevel(),"StateMachine yielding");
  }

  public Level getBeforeLevel ()
  {
    return _beforeLevel;
  }

  public void setBeforeLevel (Level beforeLevel)
  {
    _beforeLevel = beforeLevel;
  }

  public Level getAfterLevel ()
  {
    return _afterLevel;
  }

  public void setAfterLevel (Level afterLevel)
  {
    _afterLevel = afterLevel;
  }

  public Level getFailingLevel ()
  {
    return _failingLevel;
  }

  public void setFailingLevel (Level failingLevel)
  {
    _failingLevel = failingLevel;
  }

  public Level getStartingLevel ()
  {
    return _startingLevel;
  }

  public void setStartingLevel (Level startingLevel)
  {
    _startingLevel = startingLevel;
  }

  public Level getStoppingLevel ()
  {
    return _stoppingLevel;
  }

  public void setStoppingLevel (Level stoppingLevel)
  {
    _stoppingLevel = stoppingLevel;
  }

  public Level getYieldLevel ()
  {
    return _yieldLevel;
  }

  public void setYieldLevel (Level yieldLevel)
  {
    _yieldLevel = yieldLevel;
  }

  public Level getFullLevel ()
  {
    return _fullLevel;
  }

  public void setFullLevel (Level fullLevel)
  {
    _fullLevel = fullLevel;
  }

  public void setAllLevels (Level level)
  {
    setAfterLevel(level);
    setBeforeLevel(level);
    setStartingLevel(level);
    setStoppingLevel(level);
    setYieldLevel(level);
    setFailingLevel(level);
    setFullLevel(level);
  }

  public Logger getLog ()
  {
    return _log;
  }

  public void setLog (Logger log)
  {
    _log = log;
  }

  private Logger _log = LogManager.getLogger(getClass());
  private Level _beforeLevel = Level.TRACE;
  private Level _afterLevel = Level.TRACE;
  private Level _failingLevel = Level.ERROR;
  private Level _fullLevel = Level.TRACE;
  private Level _startingLevel = Level.TRACE;
  private Level _stoppingLevel = Level.TRACE;
  private Level _yieldLevel = Level.TRACE;
}
