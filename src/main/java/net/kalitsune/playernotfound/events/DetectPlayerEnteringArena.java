package net.kalitsune.playernotfound.events;

import net.kalitsune.playernotfound.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static net.kalitsune.playernotfound.Stores.arenas;

public class DetectPlayerEnteringArena implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // check if the player is entering the arena
        Player player = event.getPlayer();

        Arena arena = arenas.getArena(player.getLocation());
        if (arena != null) {
            // if the player is neither dead nor already in the arena, make him join

            if (arena.isHider(player) || arena.isSeeker(player)) {
                return;
            }

            if (arena.isDead(player)) {
                // tp them back
                player.teleport(arena.getWaypoint());

                // send a message
                player.sendMessage(ChatColor.RED + "You are dead, you can't enter the arena");
                return;
            }

            //ensure that the player is not already playing in another arena
            if (arenas.getArena(player) != null) {
                // check if the arena is the same as the current one
                if (arenas.getArena(player).getName().equals(arena.getName())) {
                    return;
                }

                // check if the player is dead
                if (arenas.getArena(player).isDead(player)) {
                    // Remove the player from the dead list
                    arenas.getArena(player).removeDeadPlayer(player);
                    // make the player join the seekers
                    arena.addSeeker(player);
                }
            } else {
                // make the player join the seekers
                arena.addSeeker(player);
            }
        } else {
            // the player is not in an arena
            // check if the player is playing in an arena
            if (arenas.getArena(player) != null) {
                // remove the player from the arena
                arenas.getArena(player).addDeadPlayer(player);
                // send a message
                player.sendMessage(ChatColor.RED + "Leaving the arena during a game is prohibited. You are now dead.");
            }
        }
    }
}
