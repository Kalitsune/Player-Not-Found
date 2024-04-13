package net.kalitsune.playernotfound;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.waypoint.Waypoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;


public class Arena {
    private String name;
    private boolean active = false;
    private Location from;
    private Location to;
    private Location waypoint;
    private Location[] spawns;

    private NPC[] npcs = new NPC[0];

    private Player[] hiders;

    private Player[] seekers;
    private Player[] deadPlayers;

    public Arena(String name, Location from, Location to, Location waypoint, Location[] spawns) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.waypoint = waypoint;
        this.spawns = spawns;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Location[] getLocations() {
        return this.spawns;
    }

    public void setLocation(Location[] locs) {
        this.spawns = locs;
    }

    public Location getFrom() {
        return this.from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public Location getTo() {
        return this.to;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    public Location getWaypoint() {
        return this.waypoint;
    }

    public void setWaypoint(Location waypoint) {
        this.waypoint = waypoint;
    }

    public Player[] getHiders() {
        return this.hiders;
    }

    public void setHiders(Player[] hiders) {
        this.hiders = hiders;
    }

    public boolean isHider(Player player) {
        if (hiders != null) {
            for (Player hider : hiders) {
                if (hider.equals(player)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addHider(Player player) {
        Player[] hiders = this.hiders;
        if (hiders == null) {
            hiders = new Player[1];
            hiders[0] = player;
            this.hiders = hiders;
        } else {
            Player[] newHiders = new Player[hiders.length + 1];
            System.arraycopy(hiders, 0, newHiders, 0, hiders.length);
            newHiders[hiders.length] = player;
            this.hiders = newHiders;
        }

        // give the player the hider club
        player.getInventory().addItem(Items.hiderClub);
    }

    public void removeHider(Player player) {
        if (isHider(player)) {
            // remove the player from the hiders
            Player[] hiders = this.hiders;
            Player[] newHiders = new Player[hiders.length - 1];
            int i = 0;
            for (Player hider : hiders) {
                if (!hider.equals(player)) {
                    newHiders[i] = hider;
                    i++;
                }
            }
            this.hiders = newHiders;

            // remove the hider club from the player
            player.getInventory().remove(Items.hiderClub);

            // check if there are no more seekers
            if (this.hiders == null || this.hiders.length == 0) {
                // inform people and reset the game
                if (this.seekers != null) {
                    for (Player seeker : this.seekers) {
                        seeker.sendTitle(ChatColor.GOLD + "VICTORY!", "Every hiders were found.", 10, 70, 20);
                    }
                }
                if (this.deadPlayers != null) {
                    for (Player deadPlayer : this.deadPlayers) {
                        deadPlayer.sendTitle(ChatColor.RED + "DEFEAT!", ChatColor.GRAY + "You died.", 10, 70, 20);
                    }
                }
                reset();
            }
        }
    }

    public Player[] getSeekers() {
        return this.seekers;
    }

    public void setSeekers(Player[] seekers) {
        this.seekers = seekers;
    }

    public boolean isSeeker(Player player) {
        if (seekers != null) {
            for (Player seeker : seekers) {
                if (seeker.equals(player)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addSeeker(Player player) {
        Player[] seekers = this.seekers;

        if (seekers == null) {
            seekers = new Player[1];
            seekers[0] = player;
            this.seekers = seekers;
        } else {
            Player[] newSeekers = new Player[seekers.length + 1];
            System.arraycopy(seekers, 0, newSeekers, 0, seekers.length);
            newSeekers[seekers.length] = player;
            this.seekers = newSeekers;
        }

        // give the player the seeker club (each seeker gets 3 seekerClub)
        ItemStack seekerClub = Items.seekerClub.clone();
        seekerClub.setAmount(3);
        player.getInventory().addItem(seekerClub);
    }

    public void removeSeeker(Player player) {
        if (isSeeker(player)) {
            // remove the player from the seekers
            Player[] seekers = this.seekers;
            Player[] newSeekers = new Player[seekers.length - 1];
            int i = 0;
            for (Player seeker : seekers) {
                if (!seeker.equals(player)) {
                    newSeekers[i] = seeker;
                    i++;
                }
            }
            this.seekers = newSeekers;

            // remove the seeker club from the player
            player.getInventory().remove(Items.seekerClub);

            // check if there are no more seekers
            if (this.seekers == null || this.seekers.length == 0) {
                // inform people and reset the game
                if (this.hiders != null) {
                    for (Player hider : this.hiders) {
                        hider.sendTitle(ChatColor.GOLD + "VICTORY!", ChatColor.GRAY + "Every seekers died.", 10, 70, 20);
                    }
                }
                if (this.deadPlayers != null) {
                    for (Player deadPlayer : this.deadPlayers) {
                        deadPlayer.sendTitle(ChatColor.RED + "DEFEAT!", ChatColor.GRAY + "You died.", 10, 70, 20);
                    }
                }
                reset();
            }
        }
    }

    public Player[] getDeadPlayers() {
        return this.deadPlayers;
    }

    public void setDeadPlayers(Player[] deadPlayers) {
        this.deadPlayers = deadPlayers;
    }

    public boolean isDead(Player player) {
        if (deadPlayers != null) {
            for (Player deadPlayer : deadPlayers) {
                if (deadPlayer.equals(player)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addDeadPlayer(Player player) {
        Player[] deadPlayers = this.deadPlayers;

        if (deadPlayers == null) {
            deadPlayers = new Player[1];
            deadPlayers[0] = player;
            this.deadPlayers = deadPlayers;
        } else {
            Player[] newDeadPlayers = new Player[deadPlayers.length + 1];
            System.arraycopy(deadPlayers, 0, newDeadPlayers, 0, deadPlayers.length);
            newDeadPlayers[deadPlayers.length] = player;
            this.deadPlayers = newDeadPlayers;
        }

        // remove the player from any existing team
        removeSeeker(player);
        removeHider(player);

        // tp them back
        player.teleport(getWaypoint());

        player.sendMessage(ChatColor.GOLD + "You are " + ChatColor.GRAY + "DEAD" + ChatColor.GOLD + ", better luck next time!");
        player.getWorld().playSound(player.getLocation(), "block.beacon.deactivate", 1, 1);
    }

    public void removeDeadPlayer(Player player) {
        if (isDead(player)) {
            // remove the player from the dead players
            Player[] deadPlayers = this.deadPlayers;
            Player[] newDeadPlayers = new Player[deadPlayers.length - 1];
            int i = 0;
            for (Player deadPlayer : deadPlayers) {
                if (!deadPlayer.equals(player)) {
                    newDeadPlayers[i] = deadPlayer;
                    i++;
                }
            }
            this.deadPlayers = newDeadPlayers;
        }
    }

    public Player[] getPlayers() {
        // get all players in the arena using Bukkit.getOnlinePlayers();
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        // create an array to store the players in the arena
        Player[] playersInArena = new Player[players.size()];
        int i = 0;
        for (Player player : players) {
            if (isInside(player.getLocation())) {
                // ensure that the player is not playing in another arena
                if (Stores.arenas.getArena(player) != null) {
                    // check if the player is dead
                    if (Stores.arenas.getArena(player).isDead(player)) {
                        // remove the player from the dead list
                        Stores.arenas.getArena(player).removeDeadPlayer(player);

                        // return the player
                        playersInArena[i] = player;
                        i++;

                    }
                } else {
                    // return the player
                    playersInArena[i] = player;
                    i++;
                }
            }
        }

        //filter the null values
        playersInArena = Arrays.stream(playersInArena)
                .filter(Objects::nonNull)
                .toArray(Player[]::new);

        // return the array
        return playersInArena;
    }

    public boolean isInside(Location loc) {
        return ((loc.getX() >= from.getX() && loc.getX() <= to.getX()) || (loc.getX() <= from.getX() && loc.getX() >= to.getX())) &&
                ((loc.getY() >= from.getY() && loc.getY() <= to.getY()) || (loc.getY() <= from.getY() && loc.getY() >= to.getY())) &&
                ((loc.getZ() >= from.getZ() && loc.getZ() <= to.getZ()) || (loc.getZ() <= from.getZ() && loc.getZ() >= to.getZ()));
    }

    public void reset() {
        // remove all players from the arena
        if (getHiders() != null) {
            for (Player player : getHiders()) {
                // remove the hider club from the player
                player.getInventory().remove(Items.hiderClub.getType());
                // tp them back
                player.teleport(getWaypoint());
                // send them a message
                player.sendMessage(ChatColor.RED + "The game has ended, you have been teleported back to arena waypoint.");
            }
        }

        if (getSeekers() != null) {
            for (Player player : getSeekers()) {
                // remove the seeker club from the player
                player.getInventory().remove(Items.seekerClub.getType());
                // tp them back
                player.teleport(getWaypoint());
                // send them a message
                player.sendMessage(ChatColor.RED + "The game has ended, you have been teleported back to arena waypoint.");
            }
        }

        if (getDeadPlayers() != null) {
            for (Player player : getDeadPlayers()) {
                // send them a message
                player.sendMessage(ChatColor.RED + "The game has ended.");
            }
        }

        // remove all npcs
        removeNPCs();

        // reset the teams
        hiders = null;
        seekers = null;
        deadPlayers = null;

        // set the arena as inactive
        setActive(false);
    }

    public void spawnNPCs(String[] playerName) {
        // ensure that the npcs are removed
        removeNPCs();

        // create the npcs
        npcs = new NPC[playerName.length * spawns.length];

        int i = 0;
        for (String name : playerName) {
            for (Location loc : spawns) {
                // spawn the npc
                NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
                npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
                npc.spawn(loc);

                // save it so we can remove it later
                npcs[i] = npc;
                i++;
            }
        }

        //filter the null values
        npcs = Arrays.stream(npcs)
                .filter(Objects::nonNull)
                .toArray(NPC[]::new);
    }

    public void spawnNPCs(Player[] players) {
        // ensure that the npcs are removed
        removeNPCs();

        // create the npcs
        npcs = new NPC[players.length * spawns.length];

        int i = 0;
        for (Player player : players) {
            for (Location loc : spawns) {
                if (loc == null) {
                    continue;
                }
                // spawn the npc
                NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, player.getName());
                npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
                npc.spawn(loc);

                // save it so we can remove it later
                npcs[i] = npc;
                i++;
            }
        }

        //filter the null values
        npcs = Arrays.stream(npcs)
                .filter(Objects::nonNull)
                .toArray(NPC[]::new);
    }

    public boolean isNPC(NPC npc) {
        for (NPC n : npcs) {
            if (n.equals(npc)) {
                return true;
            }
        }
        return false;
    }

    public void removeNPCs() {
        for (NPC npc : npcs) {
            // destroy the npc
            npc.destroy();
            // remove the npc from the array
            npc = null;
        }
    }
}

