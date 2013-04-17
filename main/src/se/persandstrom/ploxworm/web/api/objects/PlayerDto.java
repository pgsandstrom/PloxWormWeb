package se.persandstrom.ploxworm.web.api.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import se.persandstrom.ploxworm.web.Player;

/**
 * User: Per Sandstrom
 * Date: 2013-04-17 10:44
 */
public class PlayerDto { //Dto = data transfer object

    private String name;
    private String winning_message;
    private int player_number;

    public PlayerDto(Player player) {
        this.name = player.getName();
        this.winning_message = player.getWinningMessage();
        this.player_number = player.getPlayerNumber();
    }
}
