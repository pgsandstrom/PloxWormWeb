package se.persandstrom.ploxworm.web;

import com.google.gson.JsonObject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashSet;
import java.util.Set;

/**
 * Holds the websockets while they open and make a game request
 */
@Named("initHolder")
@ApplicationScoped
public class InitHolder implements PlayerParent {

    @Inject
    MatchMaker matchMaker;

    Set<Player> playerSet = new HashSet<Player>();

    public void addPlayer(Player player) {
        player.setParent(this);
        playerSet.add(player);
    }

    @Override
    public void received(Player player, JsonObject message) {
        //NOT IMPLEMENTED XXX log this
    }

    @Override
    public void remove(Player player) {
        //NOT IMPLEMENTED XXX log this
    }

    @Override
    public void open(Player player) {
        //do nothing until they request a game
//        playerSet.remove(player);
//        matchMaker.addPlayer(player);
    }
}
