package org.m9mx.smpcombatlog.api;

import org.bukkit.entity.Player;
import org.m9mx.smpcombatlog.SMPCombatLog;
import org.m9mx.smpcombatlog.config.ConfigManager;
import org.m9mx.smpcombatlog.manager.BypassManager;
import org.m9mx.smpcombatlog.manager.CombatManager;

import java.util.UUID;

/**
 * Public API for SMPCombatLog plugin
 */
public class CombatLogAPI {

    private final SMPCombatLog plugin;
    private final CombatManager combatManager;
    private final ConfigManager configManager;
    private final BypassManager bypassManager;

    public CombatLogAPI(SMPCombatLog plugin, CombatManager combatManager) {
        this.plugin = plugin;
        this.combatManager = combatManager;
        this.configManager = plugin.getConfigManager();
        this.bypassManager = plugin.getBypassManager();
    }

    /**
     * Check if combat logging is enabled
     */
    public boolean isCombatLogEnabled() {
        return configManager.isCombatLogEnabled();
    }

    /**
     * Set combat logging enabled/disabled
     */
    public void setCombatLogEnabled(boolean enabled) {
        configManager.setCombatLogEnabled(enabled);
        if (!enabled) {
            combatManager.disableAll();
        }
    }

    /**
     * Check if a player is currently in combat
     */
    public boolean isPlayerInCombat(Player player) {
        return combatManager.isInCombat(player.getUniqueId());
    }

    /**
     * Check if a player is in combat
     */
    public boolean isPlayerInCombat(UUID playerUUID) {
        return combatManager.isInCombat(playerUUID);
    }

    /**
     * Check if a player is bypassed from combat logging punishment
     */
    public boolean isPlayerBypassed(Player player) {
        return bypassManager.isBypassed(player.getUniqueId());
    }

    /**
     * Check if a player is bypassed
     */
    public boolean isPlayerBypassed(UUID playerUUID) {
        return bypassManager.isBypassed(playerUUID);
    }

    /**
     * Add a player to the bypass list
     */
    public void addBypass(Player player) {
        bypassManager.addBypass(player.getUniqueId());
    }

    /**
     * Add a player to the bypass list
     */
    public void addBypass(UUID playerUUID) {
        bypassManager.addBypass(playerUUID);
    }

    /**
     * Remove a player from the bypass list
     */
    public void removeBypass(Player player) {
        bypassManager.removeBypass(player.getUniqueId());
    }

    /**
     * Remove a player from the bypass list
     */
    public void removeBypass(UUID playerUUID) {
        bypassManager.removeBypass(playerUUID);
    }

    /**
     * Get the opponent of a player in combat
     */
    public Player getOpponent(Player player) {
        return combatManager.getOpponent(player.getUniqueId());
    }

    /**
     * Get the opponent of a player in combat
     */
    public Player getOpponent(UUID playerUUID) {
        return combatManager.getOpponent(playerUUID);
    }
}
