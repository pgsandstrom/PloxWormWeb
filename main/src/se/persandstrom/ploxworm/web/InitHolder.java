package se.persandstrom.ploxworm.web;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashSet;
import java.util.Set;

/**
 * Holds the websockets while they open
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
    public void received(Player player, String message) {
        //NOT IMPLEMENTED XXX log this
    }

    @Override
    public void remove(Player player) {
        //NOT IMPLEMENTED XXX log this
    }

    @Override
    public void open(Player player) {
        playerSet.remove(player);
        matchMaker.addPlayer(player);
    }
}
