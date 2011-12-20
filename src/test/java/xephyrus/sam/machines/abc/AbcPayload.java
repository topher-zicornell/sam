package xephyrus.sam.machines.abc;

import xephyrus.sam.core.Payload;

public class AbcPayload
  implements Payload
{
  public AbcPayload (Long id)
  {
    _id = id;
    _cycle = 0;
  }

  public Long getId ()
  {
    return _id;
  }

  public int cycle ()
  {
    return _cycle++;
  }
  
  private Long _id;
  private Integer _cycle;
}
