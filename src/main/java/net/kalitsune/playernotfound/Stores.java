package net.kalitsune.playernotfound;

import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.Scoreboard;

public class Stores {
    public static PlayerNotFound plugin;
    public static FileConfiguration config;
    public static Scoreboard scoreboard;
    public static NPCRegistry npcRegistry;
    public static ArenaManager arenas = new ArenaManager();

}
