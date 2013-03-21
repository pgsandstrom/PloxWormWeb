package se.persandstrom.ploxworm.web;

import com.google.gson.JsonObject;
import se.persandstrom.ploxworm.web.api.ApiObjectFactory;
import se.persandstrom.ploxworm.web.api.objects.MatchRequest;

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
    ApiObjectFactory apiObjectFactory;

    @Inject
    MatchMaker matchMaker;

    Set<Player> playerSet = new HashSet<Player>();

    public void addPlayer(Player player) {
        player.setParent(this);
        playerSet.add(player);
    }

    @Override
    public void received(Player player, JsonObject message) {
        Class typeClass = apiObjectFactory.getTypeClass(message);

        if (typeClass == MatchRequest.class) {
            MatchRequest matchRequest = apiObjectFactory.getApiObject(message, MatchRequest.class);
            //TODO synchronize?
            playerSet.remove(player);
            matchMaker.addPlayer(player, matchRequest);
        } else {
            throw new IllegalStateException("received wrong class: " + apiObjectFactory.getType(message));
        }
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
