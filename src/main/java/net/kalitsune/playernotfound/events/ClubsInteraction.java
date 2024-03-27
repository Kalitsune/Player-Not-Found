package net.kalitsune.playernotfound.events;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.kalitsune.playernotfound.Arena;
import net.kalitsune.playernotfound.Items;
import net.kalitsune.playernotfound.Stores;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class ClubsInteraction implements Listener {
    public static Entity getNearestEntityInSight(Player player, int range) {
        ArrayList<Entity> entities = (ArrayList<Entity>) player.getNearbyEntities(range, range, range);
        ArrayList<Block> sightBlock = (ArrayList<Block>) player.getLineOfSight(null, range);
        ArrayList<Location> sight = new ArrayList<Location>();
        for (int i = 0; i < sightBlock.size(); i++)
            sight.add(sightBlock.get(i).getLocation());
        for (int i = 0; i < sight.size(); i++) {
            for (int k = 0; k < entities.size(); k++) {
                if (Math.abs(entities.get(k).getLocation().getX() - sight.get(i).getX()) < 1.3) {
                    if (Math.abs(entities.get(k).getLocation().getY() - sight.get(i).getY()) < 1.5) {
                        if (Math.abs(entities.get(k).getLocation().getZ() - sight.get(i).getZ()) < 1.3) {
                            return entities.get(k);
                        }
                    }
                }
            }
        }
        return null;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // ensure that the action is performed on an entity
        Entity entity = getNearestEntityInSight(player, 5);
        if (event.getAction() == Action.RIGHT_CLICK_AIR && entity instanceof Player) {
            Player target = (Player) entity;

            if (target.getName().equals(player.getName())) {
                return;
            }

            ItemStack item = player.getInventory().getItemInMainHand();

            //check if the player is holding the hider club
            if (item.getType() == Items.hiderClub.getType() && item.getItemMeta().getDisplayName().equals(Items.hiderClub.getItemMeta().getDisplayName())) {
                // get the current arena
                Arena arena = Stores.arenas.getArena(player);

                // check if the target is a seeker
                if (arena != null && arena.isSeeker(target)) {
                    // summon particles and sounds here
                    target.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, target.getLocation().add(0, 1.5, 0), 10);
                    target.getWorld().playSound(target.getLocation(), "block.conduit.activate", 1, 1);

                    // blind the seeker for 5 seconds
                    target.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(100, 1));

                    // remove one hider club from the player
                    item.setAmount(item.getAmount() - 1);
                } else {
                    player.sendMessage(ChatColor.RED + "You can only use this club on seekers!");
                }
            }

            //check if the player is holding the seeker club
            if (item.getType() == Items.seekerClub.getType() && item.getItemMeta().getDisplayName().equals(Items.seekerClub.getItemMeta().getDisplayName())) {
                // get the current arena
                Arena arena = Stores.arenas.getArena(player);

                // check if the target is a hider
                if (arena != null && arena.isHider(target)) {
                    // summon particles and sounds here
                    target.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, target.getLocation(), 20);
                    target.getWorld().playSound(target.getLocation(), "item.trident.thunder", 1, 1);

                    // add the player to the dead players list
                    arena.addDeadPlayer(target);

                    // reset the seeker club number
                    item.setAmount(3);
                } else {
                    player.sendMessage(ChatColor.RED + "You can only use this club on hiders and NPCs!");
                }
            }
        }
    }

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        NPC target = event.getNPC();
        ItemStack item = player.getInventory().getItemInMainHand();

        //check if the player is holding the seeker club
        if (item.getType() == Items.seekerClub.getType() && item.getItemMeta().getDisplayName().equals(Items.seekerClub.getItemMeta().getDisplayName())) {
            Arena arena = Stores.arenas.getArena(player);

            // check if the NPC is part of the game
            if (arena == null || !arena.isNPC(target)) {
                return;
            }

            // summon particles and sounds here
            target.getEntity().getWorld().spawnParticle(Particle.PORTAL, target.getEntity().getLocation(), 20);
            target.getEntity().getWorld().playSound(target.getEntity().getLocation(), "block.bell.resonate", 1, 1);

            // remove the npc
            target.destroy();

            // remove one seeker club from the player
            item.setAmount(item.getAmount() - 1);

            // check if the player has no more seeker clubs
            if (item.getAmount() == 0) {
                // summon particles and sounds here
                player.getWorld().playSound(player.getLocation(), "block.fire.extinguish", 1, 1);
                player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 20);

                // add the player to the dead players list
                arena.addDeadPlayer(player);
            }
        }
    }
}
