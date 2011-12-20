package xephyrus.sam.machines.penneysgame;

import xephyrus.sam.core.Payload;

public class PenneysPayload
  implements Payload
{
  public enum Flip
  {
    Heads,
    Tails
  }

  public PenneysPayload (String playerOnePattern, String playerTwoPattern)
  {
    _playerPatterns = new String[] { playerOnePattern, playerTwoPattern };
    _flips = new StringBuilder();
    _id = System.currentTimeMillis();
  }

  public void addFlip (Flip flip)
  {
    _flips.append(flip.name().charAt(0));
  }

  public String getFlips ()
  {
    return _flips.toString();
  }

  @Override
  public Long getId ()
  {
    return _id;
  }

  public String getPlayerOnePattern ()
  {
    return _playerPatterns[0];
  }

  public String getPlayerTwoPattern ()
  {
    return _playerPatterns[1];
  }

  public String[] getPlayerPatterns ()
  {
    return _playerPatterns;
  }

  public Integer getVictor ()
  {
    return _victor;
  }

  public void setVictor (Integer victor)
  {
    _victor = victor;
  }

  private Long _id;
  private StringBuilder _flips;
  private String[] _playerPatterns;
  private Integer _victor;
}
