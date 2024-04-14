package net.kalitsune.playernotfound.commands;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.NeedsOp;
import dev.jorel.commandapi.annotations.Subcommand;
import dev.jorel.commandapi.annotations.arguments.AIntegerArgument;
import dev.jorel.commandapi.annotations.arguments.AStringArgument;
import net.kalitsune.playernotfound.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static net.kalitsune.playernotfound.Stores.arenas;

@Command("playernotfound")
public class PlayerNotFoundCommand {
    @Default
    public static void playernotfound(Player sender) {
        sender.sendMessage(ChatColor.YELLOW + "playernotfound - Help");
        sender.sendMessage(ChatColor.YELLOW + "/playernotfound play <arena>" + ChatColor.AQUA + " - " + ChatColor.WHITE + "Spawns the NOCs for the arena and enable it");
        sender.sendMessage(ChatColor.YELLOW + "/playernotfound reset <arena>" + ChatColor.AQUA + " - " + ChatColor.WHITE + "removes every NPC for this arena and disable its effects");
        sender.sendMessage(ChatColor.YELLOW + "/playernotfound tp <arena>" + ChatColor.AQUA + " - " + ChatColor.WHITE + "teleport you to the arena waypoint");
        sender.sendMessage(ChatColor.YELLOW + "/playernotfound arena list" + ChatColor.AQUA + " - " + ChatColor.WHITE + "lists every arena configured");

    }

    @Subcommand("play")
    public static void play(Player sender, @AStringArgument String arenaName) {
        play_game(sender, arenaName, null);
    }

    @Subcommand("play")
    public static void play(Player sender, @AStringArgument String arenaName, @AIntegerArgument Integer duration) {
        play_game(sender, arenaName, duration);
    }

    public static void play_game(Player sender, String arenaName, Integer duration) {
        Arena arena = arenas.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "Arena " + arenaName + " not found");
            return;
        }
        if (arena.isActive()) {
            sender.sendMessage(ChatColor.RED + "a game is already running on " + arena.getName() + "!");
            return;
        }

        Player[] players = arena.getPlayers();
        if (players.length == 0) {
            sender.sendMessage(ChatColor.RED + "No players found in the arena " + arena.getName());
            sender.sendMessage(ChatColor.GRAY + "There must be at least one player, the players in the area when the game starts will be the hiders");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "The game is starting on arena " + arena.getName());
        StringBuilder message = new StringBuilder(ChatColor.YELLOW + "Hiders: ");
        for (Player player : players) {
            message.append(ChatColor.AQUA).append(player.getName()).append(ChatColor.YELLOW).append(", ");
            arena.addHider(player);
        }
        // remove the last comma and space and add a dot
        message.delete(message.length() - 2, message.length());
        message.append(".");
        sender.sendMessage(message.toString());

        // enable the arena
        arena.resetCountdown(duration);
        arena.spawnNPCs(players);
        arena.setActive(true);

        // main game loop
        arena.startLoop();
    }

    @Subcommand("reset")
    public static void reset(Player sender, @AStringArgument String arenaName) {
        Arena arena = arenas.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "Arena " + arenaName + " not found");
            return;
        }
        if (!arena.isActive()) {
            sender.sendMessage(ChatColor.RED + arena.getName() + " is already inactive!");
            return;
        }
        sender.sendMessage(ChatColor.DARK_GREEN + "Stopped the game on " + arena.getName());
        arena.reset();
    }


    @Subcommand("tp")
    public static void tp(Player sender, @AStringArgument String arenaName) {
        Arena arena = arenas.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "Arena " + arenaName + " not found");
            return;
        }
        sender.sendMessage(ChatColor.DARK_GREEN + "Teleported to " + arena.getName());
        sender.teleport(arena.getWaypoint());
    }

    @Subcommand("list")
    @NeedsOp
    public static void list(Player sender) {
        StringBuilder message = new StringBuilder(ChatColor.YELLOW + "Arenas: ");
        for (Arena arena : arenas.getArenas()) {
            message.append(ChatColor.AQUA).append(arena.getName()).append(ChatColor.YELLOW).append(", ");
        }
        // remove the last comma and space and add a dot
        message.delete(message.length() - 2, message.length());
        message.append(".");

        sender.sendMessage(message.toString());
    }
}