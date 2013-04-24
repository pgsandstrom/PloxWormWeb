package se.persandstrom.ploxworm.web;

import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import se.persandstrom.ploxworm.web.api.ApiObjectFactory;
import se.persandstrom.ploxworm.web.api.objects.MatchRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashSet;
import java.util.Set;

/**
 * Holds the players while the websocket is opened and the player makes a game request
 */
@Named("initHolder")
@ApplicationScoped
public class InitHolder implements PlayerParent {

    static Logger log = Logger.getLogger(InitHolder.class.getName());

    @Inject
    ApiObjectFactory apiObjectFactory;

    @Inject
    MatchMaker matchMaker;

    @Inject
    EternalGameHolder eternalGameHolder;

    Set<Player> playerSet = new HashSet<Player>();

    public void addPlayer(HumanPlayer player) {
        player.setParent(this);
        playerSet.add(player);
    }

    @Override
    public void received(HumanPlayer player, JsonObject message) {
        Class typeClass = apiObjectFactory.getTypeClass(message);

        if (typeClass == MatchRequest.class) {
            MatchRequest matchRequest = apiObjectFactory.getApiObject(message, MatchRequest.class);
            playerSet.remove(player);
            matchMaker.addPlayer(player, matchRequest);
        } else {
            //the client might send a few "direction"-messages after he died, then this will happen
            log.debug("received wrong class: " + apiObjectFactory.getType(message));
        }
    }

    @Override
    public void remove(HumanPlayer player) {
        log.debug("Player removed from InitHolder");
        playerSet.remove(player);

        eternalGameHolder.removeObserver(player);
        //TODO fix the whole issue with adding cpu dynamically and stuff
    }

    @Override
    public void open(HumanPlayer player) {
        eternalGameHolder.addObserver(player);
    }
}
