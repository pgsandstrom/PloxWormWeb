package se.persandstrom.ploxworm.web;

import org.apache.log4j.Logger;
import se.persandstrom.ploxworm.core.Core;
import se.persandstrom.ploxworm.core.worm.board.BoardType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named("eternalGameHolder")
@ApplicationScoped
public class EternalGameHolder {

    static Logger log = Logger.getLogger(EternalGameHolder.class.getName());

    @Inject
    MatchMaker matchMaker;

    @Inject
    InitHolder initHolder;

    private final Object eternalGameLock = new Object();
    private Game eternalGame;

    public Game getEternalGame(boolean createIfNeeded) {
        synchronized (eternalGameLock) {
            if (eternalGame == null && createIfNeeded) {
                WebGameController gameController = new WebGameController(initHolder);

                Core core = getEternalGameCore(gameController);
                gameController.setCore(core);

                List<HumanPlayer> playerList = new ArrayList<HumanPlayer>();

                eternalGame = new Game(matchMaker, playerList, gameController, core);
                eternalGame.start();

                CpuPlayerGenerator cpuPlayerGenerator = new CpuPlayerGenerator();
                gameController.addPlayer(cpuPlayerGenerator.get());
                gameController.addPlayer(cpuPlayerGenerator.get());
            }

            return eternalGame;
        }
    }

    private Core getEternalGameCore(WebGameController gameController) {
        Core.Builder builder = new Core.Builder(gameController);
        builder.setEternalGame(true);
        builder.setLevel(0);
        builder.setBoardType(BoardType.ETERNAL);

        return builder.build();
    }

    public void gameStopped(Game game) {
        synchronized (eternalGameLock) {
            if (game == eternalGame) {
                log.info("eternal game stopped");
                eternalGame = null;
            } else {
                log.info("non-eternal game reported stop!");
            }
        }
    }

    public void addObserver(HumanPlayer player) {
        synchronized (eternalGameLock) {
            //ensure the game exists:
            getEternalGame(true);
            eternalGame.addObserver(player);
        }
    }

    public void removeObserver(HumanPlayer player) {
        synchronized (eternalGameLock) {
            if (eternalGame != null) {
                eternalGame.removeObserver(player);
            }
        }
    }
}
