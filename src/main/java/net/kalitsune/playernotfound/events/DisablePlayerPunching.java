package net.kalitsune.playernotfound.events;

import net.kalitsune.playernotfound.Arena;
import net.kalitsune.playernotfound.Stores;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DisablePlayerPunching implements Listener {
    @EventHandler
    public void onDamaged(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Arena arena = Stores.arenas.getArena(player);

            // check if the player is in an arena and is a hider
            if (arena != null && arena.isHider(player)) {
                event.setCancelled(true);
            }
        }
    }
}
