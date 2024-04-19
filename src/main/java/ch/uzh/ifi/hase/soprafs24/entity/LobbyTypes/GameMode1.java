package ch.uzh.ifi.hase.soprafs24.entity.LobbyTypes;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import ch.uzh.ifi.hase.constants.GameModes;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


@Entity
public class GameMode1 extends Lobby {
  private static final long serialVersionUID = 1L;

  
  {
    // Initializer block to set default values
    super.playerLimit = 3;
    super.rounds = 5;
    super.gamemode = GameModes.Gamemode1;
  }

  
}
