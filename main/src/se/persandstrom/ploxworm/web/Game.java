package se.persandstrom.ploxworm.web;

/**
 * User: pesandst
 * Date: 2013-03-15
 * Time: 12:42
 */
public class Game extends Thread {

    Player p1;
    Player p2;

    public Game(Player p1, Player p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            p1.send("time " + i);
            p2.send("time " + i);
        }
    }
}
