package se.persandstrom.ploxworm.web;

import com.google.gson.JsonObject;
import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.web.api.ApiObjectFactory;
import se.persandstrom.ploxworm.web.api.objects.MatchRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Named("matchMaker")
@ApplicationScoped
public class MatchMaker implements Serializable, PlayerParent {

    @Inject
    InitHolder initHolder;

    @Inject
    ApiObjectFactory apiObjectFactory;

    private final Random random = new Random();

    Player waitingPlayer;
    int waitingPlayerLevel;

    public void addPlayer(Player player, MatchRequest matchRequest) {
        player.setParent(this);

        if (!player.isConnected()) {
            //TODO
            System.out.println("wtf player not connected");
        }

        switch (matchRequest.getGameType()) {
            case single:
                startSinglePlayer(player, false, matchRequest.getLevel());
                break;
            case vs_cpu:
                startSinglePlayer(player, true, matchRequest.getLevel());
                break;
            case multi:
                arrangeMultiPlayer(player, matchRequest.getLevel());
                break;
        }
    }

    private void startSinglePlayer(Player player, boolean withCpu, int level) {
        System.out.println("startSinglePlayer: " + level);
        WebGameController gameController = new WebGameController(initHolder, player);
        Core.Builder builder = new Core.Builder(gameController);
//        builder.setEternalGame(false);
        builder.setLevel(level);
//        builder.setMakePlayersToAi(false);
        builder.setScore(0);

        List<Player> playerList = new ArrayList<>();
        playerList.add(player);
        //TODO add cpu if u know

        Core core = builder.build();
        gameController.setCore(core);

        //when a match has been made:
        Game game = new Game(playerList, gameController, core);
        game.start();
    }

    private void arrangeMultiPlayer(Player player, int level) {

        synchronized (this) {
            if (waitingPlayer == null) {
                waitingPlayer = player;
                waitingPlayerLevel = level;
                JsonObject putInUeueObject = apiObjectFactory.createApiObject(ApiObjectFactory.TYPE_PUT_IN_QUEUE);
                player.send(putInUeueObject.toString()); //TODO renme
            } else {
                ArrayList<Player> playerList = new ArrayList<Player>();
                playerList.add(waitingPlayer);
                playerList.add(player);

                WebGameController gameController = new WebGameController(initHolder, new Player[]{waitingPlayer,
                        player});
                Core.Builder builder = new Core.Builder(gameController);
                builder.setLevel(random.nextInt(2) == 0 ? level : waitingPlayerLevel);  //TODO should it be 2? TEST
                builder.setScore(0);
                Core core = builder.build();
                gameController.setCore(core);

                Game game = new Game(playerList, gameController, core);
                game.start();
                waitingPlayer = null;
            }
        }
    }

    @Override
    public void received(Player player, JsonObject message) {
        //NOT IMPLEMENTED
    }

    @Override
    public void remove(Player player) {
        //NOT IMPLEMENTED
    }

    @Override
    public void open(Player player) {
        throw new UnsupportedOperationException("you should already be open you doofus!");
    }
}
