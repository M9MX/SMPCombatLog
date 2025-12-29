package org.m9mx.smpcombatlog.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.m9mx.smpcombatlog.SMPCombatLog;
import org.m9mx.smpcombatlog.api.CombatLogAPI;
import org.m9mx.smpcombatlog.manager.CombatManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class SMPCombatLogCommands {

    private final SMPCombatLog plugin;
    private final CombatLogAPI api;
    private final CombatManager combatManager;

    public SMPCombatLogCommands(SMPCombatLog plugin) {
        this.plugin = plugin;
        this.api = plugin.getAPI();
        this.combatManager = plugin.getCombatManager();
    }

    public void register(Commands commands) {
        // /smpc state [on|off]
        LiteralArgumentBuilder<CommandSourceStack> stateBuilder = Commands.literal("state")
                .requires(source -> source.getSender().hasPermission("smpcombatlog.state"))
                .executes(this::handleStateShow)
                .then(Commands.literal("on")
                        .requires(source -> source.getSender().hasPermission("smpcombatlog.state.enable"))
                        .executes(this::handleStateOn))
                .then(Commands.literal("off")
                        .requires(source -> source.getSender().hasPermission("smpcombatlog.state.disable"))
                        .executes(this::handleStateOff));

        // /smpc reload
        LiteralArgumentBuilder<CommandSourceStack> reloadBuilder = Commands.literal("reload")
                .requires(source -> source.getSender().hasPermission("smpcombatlog.reload"))
                .executes(this::handleReload);

        // Main command /smpc
         LiteralArgumentBuilder<CommandSourceStack> smpcBuilder = Commands.literal("smpc")
                 .then(stateBuilder)
                 .then(reloadBuilder);

         commands.register(smpcBuilder.build());
         commands.register(Commands.literal("smpcombatlog")
                 .then(stateBuilder)
                 .then(reloadBuilder)
                 .build());
    }

    private int handleStateShow(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        boolean combatLogEnabled = api.isCombatLogEnabled();
        
        sender.sendMessage(Component.text("=== SMPCombatLog Status ===", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("Combat Log: " + (combatLogEnabled ? "ENABLED" : "DISABLED"), 
                combatLogEnabled ? NamedTextColor.GREEN : NamedTextColor.RED));
        return 1;
    }

    private int handleStateOn(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        plugin.getConfigManager().setCombatLogEnabled(true);
        sender.sendMessage(Component.text("✓ Combat logging is now ENABLED", NamedTextColor.GREEN));
        broadcastMessage(Component.text("Combat logging has been enabled!", NamedTextColor.GREEN));
        return 1;
    }

    private int handleStateOff(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        plugin.getConfigManager().setCombatLogEnabled(false);
        sender.sendMessage(Component.text("✓ Combat logging is now DISABLED", NamedTextColor.RED));
        broadcastMessage(Component.text("Combat logging has been disabled!", NamedTextColor.RED));
        return 1;
    }


    private int handleReload(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        plugin.getConfigManager().reloadConfig();
        String message = plugin.getConfigManager().getMessage("command-reload");
        net.kyori.adventure.text.Component component = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(message);
        sender.sendMessage(component);
        return 1;
    }

    private void broadcastMessage(Component message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }
}
