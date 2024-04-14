package net.kalitsune.playernotfound;

import dev.jorel.commandapi.CommandAPI;
import net.kalitsune.playernotfound.commands.ClonePlayerCommand;
import net.kalitsune.playernotfound.commands.PlayerNotFoundCommand;
import net.kalitsune.playernotfound.events.ClubsInteraction;
import net.kalitsune.playernotfound.events.DetectPlayerEnteringArena;
import net.kalitsune.playernotfound.events.PlayerLeave;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class PlayerNotFound extends JavaPlugin {

    @Override
    public void onEnable() {
        // Store the plugin instance
        Stores.plugin = this;

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

                    // convert cfgSpawns to Location
                    Location[] spawns = new Location[cfgSpawns.size()];
                    for (Map<?, ?> spawn : cfgSpawns) {
                        double x = Double.parseDouble(spawn.get("x").toString());
                        double y = Double.parseDouble(spawn.get("y").toString());
                        double z = Double.parseDouble(spawn.get("z").toString());
                        float yaw = spawn.containsKey("yaw") ? (float) spawn.get("yaw") : 0;
                        float pitch = spawn.containsKey("pitch") ? (float) spawn.get("pitch") : 0;

                        spawns[cfgSpawns.indexOf(spawn)] = new Location(getServer().getWorld(cfgWorld), x, y, z, yaw, pitch);
                    }

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

                    // create the arena
                    Arena arena = new Arena(arenaName, from, to, waypoint, spawns);

                    // add the arena to the store
                    Stores.arenas.add(arena);
                }
            }
        }
        this.getLogger().log(Level.INFO, "Plugin loaded!");
        this.getLogger().log(Level.INFO, "Arena count: " + Stores.arenas.getArenaCount());

        //register the commands
        CommandAPI.registerCommand(ClonePlayerCommand.class);
        CommandAPI.registerCommand(PlayerNotFoundCommand.class);
        this.getLogger().log(Level.INFO, "Commands registered!");

        // register the events
        getServer().getPluginManager().registerEvents(new ClubsInteraction(), this);
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
                "\n       - arena1: # The id of the arena you want to define" +
                "\n         world: world # not required, default: world" +
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
                "\n             pitch: 0 # optional"
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
