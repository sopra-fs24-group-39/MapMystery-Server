package ch.uzh.ifi.hase.soprafs24.entity.LobbyTypes;
import ch.uzh.ifi.hase.constants.GameModes;
import javax.persistence.Entity;

@Entity
public class GameMode3 extends GameMode1{
  {
    // Initializer block to set default values
    super.playerLimit = 1;
    super.rounds = 5;
    super.gamemode = GameModes.Gamemode3;
  }
}
