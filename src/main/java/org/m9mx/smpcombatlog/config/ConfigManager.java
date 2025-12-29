package org.m9mx.smpcombatlog.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;
    private final File configFile;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    public void loadConfig() {
        // Create data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        // Create default config if it doesn't exist
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        // Load the config
        reloadConfig();
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        plugin.getLogger().info("Configuration reloaded successfully!");
    }

    public int getCombatDuration() {
        return config.getInt("combat-duration", 10);
    }

    public boolean isBossBarEnabled() {
        return config.getBoolean("bossbar.enabled", true);
    }

    public boolean isGlowEnabled() {
        return config.getBoolean("glow.enabled", true);
    }

    public String getGlowColor() {
        return config.getString("glow.color", "RED");
    }

    public String getPunishmentType() {
        return config.getString("punishment.type", "kill");
    }

    public boolean isPunishmentBroadcastEnabled() {
        return config.getBoolean("punishment.broadcast", true);
    }

    public boolean isCombatLogEnabled() {
        return config.getBoolean("combat-log-enabled", true);
    }

    public String getMessage(String key) {
        return config.getString("messages." + key, "");
    }

    public void setCombatLogEnabled(boolean enabled) {
        config.set("combat-log-enabled", enabled);
        saveConfig();
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (java.io.IOException e) {
            plugin.getLogger().severe("Failed to save config.yml: " + e.getMessage());
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
