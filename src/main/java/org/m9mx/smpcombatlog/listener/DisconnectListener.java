package org.m9mx.smpcombatlog.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.m9mx.smpcombatlog.manager.CombatManager;

public class DisconnectListener implements Listener {

    private final JavaPlugin plugin;
    private final CombatManager combatManager;

    public DisconnectListener(JavaPlugin plugin, CombatManager combatManager) {
        this.plugin = plugin;
        this.combatManager = combatManager;
    }

    /**
     * Handle player logout during combat
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Check if player is in combat
        if (!combatManager.isInCombat(player.getUniqueId())) {
            return;
        }

        // Kill player for combat logging
        player.setHealth(0);
        
        // End combat
        combatManager.endCombat(player);
    }
}
