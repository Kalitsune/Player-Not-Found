package net.kalitsune.playernotfound;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ArenaManager {
    public Arena[] arenas = new Arena[0];

    public void add(Arena arena) {
        Arena[] newArenas = new Arena[arenas.length + 1];
        System.arraycopy(arenas, 0, newArenas, 0, arenas.length);
        newArenas[arenas.length] = arena;
        arenas = newArenas;
    }

    public Arena getArena(String name) {
        for (Arena arena : arenas) {
            if (arena.getName().equals(name)) {
                return arena;
            }
        }
        return null;
    }

    public Arena getArena(Location loc) {
        for (Arena arena : arenas) {
            if (arena.isInside(loc) && arena.isActive()) {
                return arena;
            }
        }
        return null;
    }

    public Arena getArena(Player player) {
        for (Arena arena : arenas) {
            if (arena.isActive()) {
                if (arena.getHiders() != null) {
                    for (Player p : arena.getHiders()) {
                        if (p == player) {
                            return arena;
                        }
                    }
                }
                if (arena.getSeekers() != null) {
                    for (Player p : arena.getSeekers()) {
                        if (p == player) {
                            return arena;
                        }
                    }
                }
                if (arena.getDeadPlayers() != null) {
                    for (Player p : arena.getDeadPlayers()) {
                        if (p == player) {
                            return arena;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Arena[] getArenas() {
        return arenas;
    }

    public void setArenas(Arena[] arenas) {
        this.arenas = arenas;
    }

    public int getArenaCount() {
        return arenas.length;
    }

    public void remove(Arena arena) {
        Arena[] newArenas = new Arena[arenas.length - 1];
        int i = 0;
        for (Arena a : arenas) {
            if (a != arena) {
                newArenas[i] = a;
                i++;
            }
        }
        arenas = newArenas;
    }

    public void remove(String name) {
        Arena[] newArenas = new Arena[arenas.length - 1];
        int i = 0;
        for (Arena a : arenas) {
            if (!a.getName().equals(name)) {
                newArenas[i] = a;
                i++;
            }
        }
        arenas = newArenas;
    }
}
