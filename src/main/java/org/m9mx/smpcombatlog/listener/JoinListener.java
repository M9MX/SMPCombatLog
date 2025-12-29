package org.m9mx.smpcombatlog.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.m9mx.smpcombatlog.manager.CombatManager;

public class JoinListener implements Listener {

    private final JavaPlugin plugin;
    private final CombatManager combatManager;

    public JoinListener(JavaPlugin plugin, CombatManager combatManager) {
        this.plugin = plugin;
        this.combatManager = combatManager;
    }

    /**
     * Clean up combat state when player rejoins
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // End any stale combat sessions
        if (combatManager.isInCombat(player.getUniqueId())) {
            combatManager.endCombat(player);
        }

        // Remove glow effect if present
        player.setGlowing(false);
    }
}
