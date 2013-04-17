package se.persandstrom.ploxworm.web;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * User: Per Sandstrom
 * Date: 2013-04-17 10:21
 */
public class CpuPlayerGenerator {
    
    private final Set<CpuPlayer> playerPool = new LinkedHashSet<CpuPlayer>();
    {
        CpuPlayer cpu1 = new CpuPlayer();
        cpu1.setName("Bob");
        cpu1.setWinningMessage("Bob is the LORD!");
        playerPool.add(cpu1);

        CpuPlayer cpu2 = new CpuPlayer();
        cpu2.setName("Anny");
        cpu2.setWinningMessage("aaannypanny!");
        playerPool.add(cpu2);

        CpuPlayer cpu3 = new CpuPlayer();
        cpu3.setName("Charles");
        cpu3.setWinningMessage("Sir Charles rules all!");
        playerPool.add(cpu3);
    }

    public CpuPlayer get() {
        CpuPlayer cpuPlayer = playerPool.iterator().next();
        playerPool.remove(cpuPlayer);
        return cpuPlayer;
    }
}
