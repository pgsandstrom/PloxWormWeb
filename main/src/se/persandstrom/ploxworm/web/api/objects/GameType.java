package se.persandstrom.ploxworm.web.api.objects;

import se.persandstrom.ploxworm.core.worm.board.BoardType;

public enum GameType {
    //GameType exists to be parsed into an api object, that is why lower case etc
    single, vs_cpu, multi;


    public static GameType getInstace(BoardType boardType) {
        switch (boardType) {
            case SINGLE:
                return single;
            case VS_CPU:
                return vs_cpu;
            case MULTI:
                return multi;
            default:
                throw new IllegalStateException("illegal enum type");
        }
    }
}
