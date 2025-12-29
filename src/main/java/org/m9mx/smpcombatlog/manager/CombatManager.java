package org.m9mx.smpcombatlog.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.m9mx.smpcombatlog.config.ConfigManager;
import org.m9mx.smpcombatlog.model.CombatSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final BossBarManager bossBarManager;
    private final Map<UUID, CombatSession> activeCombats = new HashMap<>();
    private final Map<UUID, Integer> combatTimers = new HashMap<>();

    public CombatManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.bossBarManager = new BossBarManager(plugin, configManager);
    }

    /**
     * Start combat between two players
     */
    public void startCombat(Player attacker, Player defender) {
        // Check if either player has bypass permission
        if (attacker.hasPermission("smpcombatlog.bypass") || defender.hasPermission("smpcombatlog.bypass")) {
            return;
        }

        UUID attackerUUID = attacker.getUniqueId();
        UUID defenderUUID = defender.getUniqueId();

        boolean attackerInCombat = isInCombat(attackerUUID);
        boolean defenderInCombat = isInCombat(defenderUUID);

        // If attacker not in combat, start new session
        if (!attackerInCombat) {
            activeCombats.put(attackerUUID, new CombatSession(attacker, defender));
        }

        // If defender not in combat, start new session
        if (!defenderInCombat) {
            activeCombats.put(defenderUUID, new CombatSession(defender, attacker));
        }

        // Send combat start messages if this is a new combat
        if (!attackerInCombat || !defenderInCombat) {
            sendCombatStartMessage(attacker, defender);
            sendCombatStartMessage(defender, attacker);
        }

        // Refresh timers
        int duration = configManager.getCombatDuration();
        refreshCombatTimer(attacker, duration);
        refreshCombatTimer(defender, duration);

        // Apply glow if enabled
        if (configManager.isGlowEnabled()) {
            applyGlow(attacker, defender);
            applyGlow(defender, attacker);
        }

        // Show BossBar if enabled
        if (configManager.isBossBarEnabled()) {
            bossBarManager.showBossBar(attacker, duration);
            bossBarManager.showBossBar(defender, duration);
        }
    }

    /**
     * Refresh combat timer for a player
     */
    private void refreshCombatTimer(Player player, int duration) {
        UUID uuid = player.getUniqueId();

        // Cancel existing timer
        if (combatTimers.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(combatTimers.get(uuid));
        }

        // Create new timer
        int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            endCombat(player);
        }, duration * 20L);

        combatTimers.put(uuid, taskId);
    }

    /**
     * End combat for a player
     */
    public void endCombat(Player player) {
        UUID uuid = player.getUniqueId();

        if (!isInCombat(uuid)) {
            return;
        }

        CombatSession session = activeCombats.remove(uuid);

        // Cancel timer
        if (combatTimers.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(combatTimers.get(uuid));
            combatTimers.remove(uuid);
        }

        // Remove glow
        if (configManager.isGlowEnabled() && session.getOpponent() != null && session.getOpponent().isOnline()) {
            Player opponent = session.getOpponent();
            opponent.setGlowing(false);
            
            // Remove from glow team
            try {
                org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                String colorName = configManager.getGlowColor().toUpperCase();
                String teamName = "glow_" + colorName.toLowerCase();
                org.bukkit.scoreboard.Team team = scoreboard.getTeam(teamName);
                if (team != null) {
                    team.removeEntry(opponent.getName());
                }
            } catch (Exception e) {
                plugin.getLogger().fine("Could not remove player from glow team: " + e.getMessage());
            }
        }

        // Remove BossBar
        bossBarManager.hideBossBar(player);

        // Send combat end message
        sendCombatEndMessage(player);
    }

    /**
     * Check if a player is in combat
     */
    public boolean isInCombat(UUID uuid) {
        return activeCombats.containsKey(uuid);
    }

    /**
     * Get opponent of a player in combat
     */
    public Player getOpponent(UUID uuid) {
        CombatSession session = activeCombats.get(uuid);
        return session != null ? session.getOpponent() : null;
    }

    /**
     * Apply glow effect to opponent
     */
    private void applyGlow(Player player, Player opponent) {
        opponent.setGlowing(true);
        
        // Set glow color via team (doesn't change name color, only glow)
        try {
            org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            String colorName = configManager.getGlowColor().toUpperCase();
            String teamName = "glow_" + colorName.toLowerCase();
            
            org.bukkit.scoreboard.Team team = scoreboard.getTeam(teamName);
            if (team == null) {
                team = scoreboard.registerNewTeam(teamName);
                try {
                    org.bukkit.ChatColor color = org.bukkit.ChatColor.valueOf(colorName);
                    team.setColor(color);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().fine("Unknown glow color: " + colorName + ", using default");
                }
            }
            team.addEntry(opponent.getName());
        } catch (Exception e) {
            plugin.getLogger().fine("Could not apply glow color: " + e.getMessage());
        }
    }

    /**
     * Remove BossBar for player
     */
    private void removeBossBar(Player player) {
        // Implementation depends on BossBar manager
    }



    /**
     * Send combat start message
     */
    private void sendCombatStartMessage(Player player, Player opponent) {
        String messageTemplate = configManager.getMessage("combat-start");
        if (messageTemplate == null || messageTemplate.isEmpty()) {
            return;
        }
        String message = messageTemplate.replace("%opponent%", opponent.getName());
        // Parse as MiniMessage and send
        net.kyori.adventure.text.Component component = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(message);
        player.sendMessage(component);
    }

    /**
     * Send combat end message
     */
    private void sendCombatEndMessage(Player player) {
        String message = configManager.getMessage("combat-end");
        if (message == null || message.isEmpty()) {
            return;
        }
        // Parse as MiniMessage and send
        net.kyori.adventure.text.Component component = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(message);
        player.sendMessage(component);
    }

    /**
     * Disable all active combats
     */
    public void disableAll() {
        activeCombats.keySet().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                endCombat(player);
            }
        });
        combatTimers.values().forEach(taskId -> Bukkit.getScheduler().cancelTask(taskId));
        combatTimers.clear();
        bossBarManager.hideAll();
    }
}
