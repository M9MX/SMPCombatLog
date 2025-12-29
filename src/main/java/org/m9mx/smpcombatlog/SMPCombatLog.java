package org.m9mx.smpcombatlog;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.m9mx.smpcombatlog.api.CombatLogAPI;
import org.m9mx.smpcombatlog.config.ConfigManager;
import org.m9mx.smpcombatlog.listener.CombatListener;
import org.m9mx.smpcombatlog.listener.DisconnectListener;
import org.m9mx.smpcombatlog.manager.CombatManager;
import org.m9mx.smpcombatlog.command.SMPCombatLogCommands;

public final class SMPCombatLog extends JavaPlugin {

    private static SMPCombatLog instance;
    private ConfigManager configManager;
    private CombatManager combatManager;
    private CombatLogAPI api;
    private SMPCombatLogCommands smpcCommands;
    private org.m9mx.smpcombatlog.manager.BypassManager bypassManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize configuration
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Initialize bypass manager
        bypassManager = new org.m9mx.smpcombatlog.manager.BypassManager(this);
        bypassManager.loadBypasses();

        // Initialize combat manager
        combatManager = new CombatManager(this, configManager);

        // Initialize API
        api = new CombatLogAPI(this, combatManager);

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new CombatListener(this, combatManager, configManager), this);
        Bukkit.getPluginManager().registerEvents(new DisconnectListener(this, combatManager), this);
        Bukkit.getPluginManager().registerEvents(new org.m9mx.smpcombatlog.listener.JoinListener(this, combatManager), this);

        // Register commands
        smpcCommands = new SMPCombatLogCommands(this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            smpcCommands.register(event.registrar());
        });

        getLogger().info("SMPCombatLog enabled!");
    }

    @Override
    public void onDisable() {
        // Cancel all timers
        if (combatManager != null) {
            combatManager.disableAll();
        }
        getLogger().info("SMPCombatLog disabled!");
    }

    public static SMPCombatLog getInstance() {
        return instance;
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CombatLogAPI getAPI() {
        return api;
    }

    public SMPCombatLogCommands getSMPCCommands() {
        return smpcCommands;
    }

    public org.m9mx.smpcombatlog.manager.BypassManager getBypassManager() {
        return bypassManager;
    }
}
