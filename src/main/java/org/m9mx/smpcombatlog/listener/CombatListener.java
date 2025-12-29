package org.m9mx.smpcombatlog.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.m9mx.smpcombatlog.SMPCombatLog;
import org.m9mx.smpcombatlog.config.ConfigManager;
import org.m9mx.smpcombatlog.manager.CombatManager;

public class CombatListener implements Listener {

    private final JavaPlugin plugin;
    private final CombatManager combatManager;
    private final ConfigManager configManager;

    public CombatListener(JavaPlugin plugin, CombatManager combatManager, ConfigManager configManager) {
        this.plugin = plugin;
        this.combatManager = combatManager;
        this.configManager = configManager;
    }

    /**
     * Handle player damage to initiate combat
     */
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        // Check if damager and damaged are players
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        // Check if combat logging is enabled
        SMPCombatLog plugin = SMPCombatLog.getInstance();
        if (!plugin.getAPI().isCombatLogEnabled()) {
            return;
        }

        Player defender = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        // Start combat
        combatManager.startCombat(attacker, defender);
    }

    /**
     * Handle player death to end combat
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        combatManager.endCombat(player);
    }
}
