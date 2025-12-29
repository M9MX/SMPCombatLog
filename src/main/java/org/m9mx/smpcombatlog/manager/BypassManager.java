package org.m9mx.smpcombatlog.manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BypassManager {

    private final JavaPlugin plugin;
    private final File bypassFile;
    private FileConfiguration bypassConfig;
    private final Set<UUID> bypassedPlayers = new HashSet<>();

    public BypassManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.bypassFile = new File(plugin.getDataFolder(), "bypass.yml");
    }

    public void loadBypasses() {
        // Create data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        // Load or create bypass.yml
        if (!bypassFile.exists()) {
            bypassConfig = new YamlConfiguration();
            saveBypasses();
        } else {
            bypassConfig = YamlConfiguration.loadConfiguration(bypassFile);
        }

        // Load bypassed players from config
        bypassedPlayers.clear();
        List<String> uuids = bypassConfig.getStringList("bypassed-players");
        for (String uuidStr : uuids) {
            try {
                bypassedPlayers.add(UUID.fromString(uuidStr));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in bypass.yml: " + uuidStr);
            }
        }

        plugin.getLogger().info("Loaded " + bypassedPlayers.size() + " bypassed players");
    }

    public boolean isBypassed(UUID playerUUID) {
        return bypassedPlayers.contains(playerUUID);
    }

    public void addBypass(UUID playerUUID) {
        bypassedPlayers.add(playerUUID);
        saveBypasses();
    }

    public void removeBypass(UUID playerUUID) {
        bypassedPlayers.remove(playerUUID);
        saveBypasses();
    }

    public void toggleBypass(UUID playerUUID) {
        if (bypassedPlayers.contains(playerUUID)) {
            removeBypass(playerUUID);
        } else {
            addBypass(playerUUID);
        }
    }

    private void saveBypasses() {
        List<String> uuids = new java.util.ArrayList<>();
        for (UUID uuid : bypassedPlayers) {
            uuids.add(uuid.toString());
        }
        bypassConfig.set("bypassed-players", uuids);

        try {
            bypassConfig.save(bypassFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save bypass.yml: " + e.getMessage());
        }
    }
}
