package xephyrus.sam.machines.penneysgame;

import xephyrus.sam.core.StateMachine;
import xephyrus.sam.machines.penneysgame.PenneysPayload.Flip;

import java.util.Random;
import java.util.regex.Pattern;

public class PenneysMachine
  extends StateMachine<PenneysState,PenneysPayload>
{
  static final private Pattern _INVALID_PATTERN = Pattern.compile("[^HT]");
  static final private Random _RANDOM = new Random();
  
  protected PenneysMachine ()
      throws NoSuchMethodException
  {
    super(PenneysState.class,PenneysPayload.class);
    registerState(PenneysState.ValidatePatterns,"validatePatterns");
    registerState(PenneysState.Flip,"flip");
    registerState(PenneysState.CheckVictory,"checkVictory");
  }

  public PenneysState validatePatterns (PenneysPayload payload)
      throws PenneysException
  {
    int i = 1;
    for (String playerPattern: payload.getPlayerPatterns())
    {
      if (playerPattern == null)
      {
        throw new PenneysException("No pattern for player " + i);
      }

      if (_INVALID_PATTERN.matcher(playerPattern).find())
      {
        throw new PenneysException("Player " + i + "'s pattern contains invalid indicators.  " +
            "Are those Heads or Tails?");
      }

      i += 1;
    }

    if (payload.getPlayerOnePattern().length() != payload.getPlayerTwoPattern().length())
    {
      throw new PenneysException("The player's patterns are not the same length!");
    }

    return PenneysState.Flip;
  }

  public PenneysState flip (PenneysPayload payload)
  {
    payload.addFlip((_RANDOM.nextInt() % 2 == 0 ? Flip.Heads : Flip.Tails));
    return PenneysState.CheckVictory;
  }

  public PenneysState checkVictory (PenneysPayload payload)
  {
    int i = 1;
    for (String playerPattern: payload.getPlayerPatterns())
    {
      if (payload.getFlips().endsWith(playerPattern))
      {
        payload.setVictor(i);
        return PenneysState.VictorFound;
      }
      i += 1;
    }
    return PenneysState.Flip;
  }
}
