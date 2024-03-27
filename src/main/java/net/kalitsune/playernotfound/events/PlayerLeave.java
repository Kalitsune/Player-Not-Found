package net.kalitsune.playernotfound.events;

import net.kalitsune.playernotfound.Arena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.kalitsune.playernotfound.Stores.arenas;

public class PlayerLeave implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // add player that leaves the server to the deadPlayers list
        for (Arena arena : arenas.getArenas()) {
            if ((arena.getSeekers() != null && arena.isSeeker(event.getPlayer())) || (arena.getHiders() != null && arena.isHider(event.getPlayer()))) {
                arena.addDeadPlayer(event.getPlayer());
            }
        }
    }
}
