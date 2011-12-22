package xephyrus.sam.core.trackers;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import xephyrus.sam.core.MachineCycleListener;
import xephyrus.sam.core.StateMachine.MachineCycles;

import java.util.HashMap;
import java.util.Map;

public class MachineCyclesTracker
  implements MachineCycleListener
{
  @Override
  public void beforeCycle ()
  {
    incCount(MachineCycles.BEFORE);
  }

  @Override
  public void afterCycle ()
  {
    incCount(MachineCycles.AFTER);
  }

  @Override
  public void yieldCycle ()
  {
    incCount(MachineCycles.YIELD);
  }

  @Override
  public void startingMachine ()
  {
    incCount(MachineCycles.STARTING);
  }

  @Override
  public void stoppingMachine ()
  {
    incCount(MachineCycles.STOPPING);
  }

  @Override
  public void failingMachine (Throwable error)
  {
    incCount(MachineCycles.FAILING);
    _log.error("Received " + error,error);
  }

  @Override
  public void fullMachine ()
  {
    incCount(MachineCycles.FULL);
  }

  public Integer getCount (MachineCycles cycle)
  {
    Integer count = _counts.get(cycle);
    return (count != null ? count : 0);
  }

  public void incCount (MachineCycles cycle)
  {
    Integer count = _counts.get(cycle);
    count = (count != null ? count + 1 : 1);
    _counts.put(cycle,count);
    _log.info(cycle + " count is now " + count);
  }

  private Map<MachineCycles,Integer> _counts = new HashMap<MachineCycles,Integer>();
  private Logger _log = LogManager.getLogger(getClass());
}
