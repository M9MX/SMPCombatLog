package org.m9mx.smpcombatlog.manager;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.m9mx.smpcombatlog.config.ConfigManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarManager {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final Map<UUID, BossBar> activeBars = new HashMap<>();
    private final Map<UUID, Integer> timerTasks = new HashMap<>();

    public BossBarManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    /**
     * Show BossBar for a player in combat with remaining time
     */
    public void showBossBar(Player player, int durationSeconds) {
        if (!configManager.isBossBarEnabled()) {
            return;
        }

        UUID uuid = player.getUniqueId();

        // Remove existing bar if present
        if (activeBars.containsKey(uuid)) {
            player.hideBossBar(activeBars.get(uuid));
            cancelTimer(uuid);
        }

        // Create new bar
        Component title = Component.text("Combat Active", NamedTextColor.RED);
        BossBar bossBar = BossBar.bossBar(title, 1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        activeBars.put(uuid, bossBar);

        // Show bar
        player.showBossBar(bossBar);

        // Update timer to decrease progress
        startTimerTask(player, bossBar, durationSeconds);
    }

    /**
     * Update BossBar progress
     */
    private void startTimerTask(Player player, BossBar bossBar, int durationSeconds) {
        UUID uuid = player.getUniqueId();

        // Cancel existing task
        if (timerTasks.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(timerTasks.get(uuid));
        }

        final int[] remainingTicks = {durationSeconds * 20};

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (!player.isOnline()) {
                hideBossBar(player);
                return;
            }

            remainingTicks[0] -= 1;
            float progress = Math.max(0, (float) remainingTicks[0] / (durationSeconds * 20));
            bossBar.progress(progress);

            if (remainingTicks[0] <= 0) {
                hideBossBar(player);
            }
        }, 0L, 1L);

        timerTasks.put(uuid, taskId);
    }

    /**
     * Hide BossBar for a player
     */
    public void hideBossBar(Player player) {
        UUID uuid = player.getUniqueId();

        if (activeBars.containsKey(uuid)) {
            player.hideBossBar(activeBars.get(uuid));
            activeBars.remove(uuid);
        }

        cancelTimer(uuid);
    }

    /**
     * Cancel timer task
     */
    private void cancelTimer(UUID uuid) {
        if (timerTasks.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(timerTasks.get(uuid));
            timerTasks.remove(uuid);
        }
    }

    /**
     * Hide all active boss bars
     */
    public void hideAll() {
        for (UUID uuid : new java.util.ArrayList<>(activeBars.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                hideBossBar(player);
            }
        }
    }
}
