package net.kalitsune.playernotfound.commands;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.arguments.AFloatArgument;
import dev.jorel.commandapi.annotations.arguments.ALocationArgument;
import dev.jorel.commandapi.annotations.arguments.APlayerArgument;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.waypoint.Waypoints;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;


@Command("cloneplayer")
public class ClonePlayerCommand {
    @Default
    public static void cloneplayer(Player sender) {
        sender.sendMessage(ChatColor.GREEN + "Successfully cloned you!");
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, sender.getName());
        npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
        npc.spawn(sender.getLocation());
    }

    @Default
    public static void cloneplayer(Player sender, @APlayerArgument Player target_player) {
        sender.sendMessage(String.format(ChatColor.GREEN + "Successfully cloned %s!", target_player.getName()));
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, target_player.getName());
        npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
        npc.spawn(sender.getLocation());

    }

    @Default
    public static void cloneplayer(Player sender, @APlayerArgument Player target_player, @ALocationArgument Location location) {
        sender.sendMessage(String.format(ChatColor.GREEN + "Successfully cloned %s!", target_player.getName()));
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, target_player.getName());
        npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
        npc.spawn(location);

    }

    @Default
    public static void cloneplayer(Player sender, @APlayerArgument Player target_player, @ALocationArgument Location location, @AFloatArgument Float yaw, @AFloatArgument Float pitch) {
        sender.sendMessage(String.format(ChatColor.GREEN + "Successfully cloned %s!", target_player.getName()));
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, target_player.getName());
        npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");

        location.setYaw(yaw);
        location.setPitch(pitch);
        npc.spawn(location);

    }
}