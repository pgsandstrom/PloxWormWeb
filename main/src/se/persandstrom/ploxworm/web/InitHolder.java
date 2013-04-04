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
            //the client might send a few "direction"-messages after he died, then this will happen
            System.out.println("received wrong class: " + apiObjectFactory.getType(message));
        }
    }

    @Override
    public void remove(Player player) {
        System.out.println("Player removed from InitHolder");
        playerSet.remove(player);
    }

    @Override
    public void open(Player player) {
        //do nothing until they request a game
//        playerSet.remove(player);
//        matchMaker.addPlayer(player);
    }
}
