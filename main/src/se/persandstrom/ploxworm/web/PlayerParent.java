package se.persandstrom.ploxworm.web;

public interface PlayerParent {

    public void received(Player player, String message);

    public void remove(Player player);

    public void open(Player player);
}
