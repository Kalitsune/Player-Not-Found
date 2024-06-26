package net.kalitsune.playernotfound;

import dev.jorel.commandapi.CommandAPI;
import net.kalitsune.playernotfound.commands.ClonePlayerCommand;
import net.kalitsune.playernotfound.commands.PlayerNotFoundCommand;
import net.kalitsune.playernotfound.events.ClubsInteraction;
import net.kalitsune.playernotfound.events.DetectPlayerEnteringArena;
import net.kalitsune.playernotfound.events.DisablePlayerPunching;
import net.kalitsune.playernotfound.events.PlayerLeave;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

public final class PlayerNotFound extends JavaPlugin {

    @Override
    public void onEnable() {
        // Store the plugin instance
        Stores.plugin = this;

        // Create the scoreboards
        Stores.scoreboard = Objects.requireNonNull(getServer().getScoreboardManager()).getMainScoreboard();
        if (Stores.scoreboard.getObjective("pnf_countdown") == null) {
            Stores.scoreboard.registerNewObjective("pnf_countdown", "dummy", "Countdown");
        }

        if (Stores.scoreboard.getObjective("pnf_wins") == null) {
            Stores.scoreboard.registerNewObjective("pnf_wins", "dummy", "Total victory");
        }

        if (Stores.scoreboard.getObjective("pnf_seeker_wins") == null) {
            Stores.scoreboard.registerNewObjective("pnf_seeker_wins", "dummy", "Seeker victory");
        }

        if (Stores.scoreboard.getObjective("pnf_hider_wins") == null) {
            Stores.scoreboard.registerNewObjective("pnf_hider_wins", "dummy", "Hider victory");
        }

        // Plugin startup logic
        // get the config
        FileConfiguration config = getDefaultConfig();
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();
        Stores.config = config;

        // init the items
        Items.init();

        // Load the arenas
        ConfigurationSection arenasSection = getConfig().getConfigurationSection("arenas");
        if (arenasSection != null) {
            for (String arenaName : arenasSection.getKeys(false)) {
                ConfigurationSection arenaSection = arenasSection.getConfigurationSection(arenaName);
                if (arenaSection != null) {
                    // get the values
                    List<Map<?, ?>> cfgSpawns = arenaSection.getMapList("spawns");

                    ConfigurationSection cfgFrom = arenaSection.getConfigurationSection("area.from");
                    ConfigurationSection cfgTo = arenaSection.getConfigurationSection("area.to");
                    ConfigurationSection cfgWaypoint = arenaSection.getConfigurationSection("area.waypoint");
                    String cfgWorld = arenaSection.getString("world", "world");
                    Integer duration = arenaSection.getInt("duration", 300);

                    // convert cfgSpawns to Location
                    List<Location> spawns = new ArrayList<>();
                    for (Map<?, ?> spawn : cfgSpawns) {
                        double x = Double.parseDouble(spawn.get("x").toString());
                        double y = Double.parseDouble(spawn.get("y").toString());
                        double z = Double.parseDouble(spawn.get("z").toString());
                        float yaw = spawn.containsKey("yaw") ? (float) spawn.get("yaw") : 0;
                        float pitch = spawn.containsKey("pitch") ? (float) spawn.get("pitch") : 0;
                        int count = spawn.containsKey("count") ? (int) spawn.get("count") : 1;

                        for (int j = 0; j < count; j++) {
                            spawns.add(new Location(getServer().getWorld(cfgWorld), x, y, z, yaw, pitch));
                        }
                    }
                    // convert the List to location[]
                    Location[] spawnsArray = new Location[spawns.size()];
                    spawns.toArray(spawnsArray);

                    // convert cfgFrom, cfgTo and cfgWaypoint to Location
                    assert cfgFrom != null;
                    double fromX = cfgFrom.getDouble("x");
                    double fromY = cfgFrom.getDouble("y");
                    double fromZ = cfgFrom.getDouble("z");
                    Location from = new Location(getServer().getWorld(cfgWorld), fromX, fromY, fromZ);

                    assert cfgTo != null;
                    double toX = cfgTo.getDouble("x");
                    double toY = cfgTo.getDouble("y");
                    double toZ = cfgTo.getDouble("z");
                    Location to = new Location(getServer().getWorld(cfgWorld), toX, toY, toZ);

                    assert cfgWaypoint != null;
                    double waypointX = cfgWaypoint.getDouble("x");
                    double waypointY = cfgWaypoint.getDouble("y");
                    double waypointZ = cfgWaypoint.getDouble("z");
                    float waypointYaw = (float) cfgWaypoint.getDouble("yaw", 0);
                    float waypointPitch = (float) cfgWaypoint.getDouble("pitch", 0);
                    Location waypoint = new Location(getServer().getWorld(cfgWorld), waypointX, waypointY, waypointZ, waypointYaw, waypointPitch);

                    // get the amount of clubs
                    int seekerClubAmount = arenaSection.getInt("seekerClubAmount", 3);
                    int hiderClubAmount = arenaSection.getInt("hiderClubAmount", 1);

                    // create the arena
                    Arena arena = new Arena(arenaName, from, to, waypoint, spawnsArray, duration, seekerClubAmount, hiderClubAmount);

                    // add the arena to the store
                    Stores.arenas.add(arena);
                }
            }
        }
        this.getLogger().log(Level.INFO, "Plugin loaded!");
        this.getLogger().log(Level.INFO, "Arena count: " + Stores.arenas.getArenaCount());

        // create the NPC registry
        Stores.npcRegistry = net.citizensnpcs.api.CitizensAPI.createInMemoryNPCRegistry("PlayerNotFound");

        //register the commands
        CommandAPI.registerCommand(ClonePlayerCommand.class);
        CommandAPI.registerCommand(PlayerNotFoundCommand.class);
        this.getLogger().log(Level.INFO, "Commands registered!");

        // register the events
        getServer().getPluginManager().registerEvents(new ClubsInteraction(), this);
        getServer().getPluginManager().registerEvents(new DisablePlayerPunching(), this);
        getServer().getPluginManager().registerEvents(new DetectPlayerEnteringArena(), this);
        getServer().getPluginManager().registerEvents(new PlayerLeave(), this);
    }

    @Override
    public void onDisable() {
        // Remove the npcs
        for (Arena arena : Stores.arenas.getArenas()) {
            arena.removeNPCs();
        }
    }

    public FileConfiguration getDefaultConfig() {
        FileConfiguration config = this.getConfig();

        config.options().header("PlayerNotFound - a fun mini game consisting on finding players among bots." +
                "\n ===== Config =====" +
                "\n   items:" +
                "\n     hider_club:" +
                "\n         name: §8Hider Club # optional, defaults to §8Hider Club" +
                "\n         material: STICK # optional, defaults to Stick" +
                "\n     seeker_club:" +
                "\n         name: §6Seeker Club # optional, defaults to §6Seeker Club" +
                "\n         name: BLAZE_ROD # optional, defaults to BLAZE_ROD" +
                "\n   arenas:" +
                "\n       - arena1: # The name of the arena you want to define" +
                "\n         world: world # not required, default: world" +
                "\n         duration: 300 # not required, default: 300 (5min), the duration of a game in seconds. 0 to disable." +
                "\n         seekerClubAmount: 3 # not required, default: 3, the amount of seeker club the seeker has" +
                "\n         hiderClubAmount: 1 # not required, default: 1, the amount of hider club the hider has" +
                "\n         area:" +
                "\n           from: # Pos 1" +
                "\n              x: 0" +
                "\n              y: 0" +
                "\n              z: 0" +
                "\n           to: # Pos 2, the game area is between the pos1 and pos2" +
                "\n             x: 200" +
                "\n             y: 200" +
                "\n             z: 200" +
                "\n           waypoint: # The coordinates ti which everyone gets teleported once the game ends" +
                "\n              x: 0" +
                "\n              y: 0" +
                "\n              z: 0" +
                "\n              yaw: 0 # optional" +
                "\n              pitch: 0 # optional" +
                "\n         spawns: # the spawn location of each npc, put as many as you'd like." +
                "\n           - x: 0" +
                "\n             y: 0" +
                "\n             z: 0" +
                "\n             yaw: 0 # optional" +
                "\n             pitch: 0 # optional" +
                "\n             count: 1 # optional"
        );

        config.addDefault("items.hider_club.name", "§8Hider Club");
        config.addDefault("items.hider_club.material", "STICK");
        config.addDefault("items.hider_club.lore", List.of("Right click to blind the seekers!"));

        config.addDefault("items.seeker_club.name", "§6Seeker Club");
        config.addDefault("items.seeker_club.material", "BLAZE_ROD");
        config.addDefault("items.seeker_club.lore", List.of("Right click to capture the hiders!"));

        Map<String, Object> from = new HashMap<>();
        from.put("x", 0);
        from.put("y", 0);
        from.put("z", 0);
        config.addDefault("arenas.arena1.area.from", from);

        Map<String, Object> to = new HashMap<>();
        to.put("x", 200);
        to.put("y", 200);
        to.put("z", 200);
        config.addDefault("arenas.arena1.area.to", to);

        Map<String, Object> waypoint = new HashMap<>();
        waypoint.put("x", 0);
        waypoint.put("y", 0);
        waypoint.put("z", 0);
        config.addDefault("arenas.arena1.area.waypoint", waypoint);


        Map<String, Object> spawn = new HashMap<>();
        spawn.put("x", 0);
        spawn.put("y", 100);
        spawn.put("z", 0);
        config.addDefault("arenas.arena1.spawns", new Object[]{spawn});

        return config;
    }
}
