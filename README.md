# SMPCombatLog

A comprehensive PvP combat logging system for Minecraft servers. Tracks player combat, provides visual feedback, and handles combat log violations with customizable punishments.

## Features

- **Global PvP Combat Tracking**: Automatically detects and tracks combat between players
- **Visual Feedback**: 
  - Boss bar timer showing remaining combat duration
  - Glow effect on opponents (customizable color)
- **Combat Logging Protection**: Automatically punishes players who disconnect or die while in combat
- **Configurable Punishments**: Choose between killing or damaging combat loggers
- **Bypass System**: Grant players immunity from combat logging via permission
- **MiniMessage Support**: Fully customizable messages with color and formatting
- **Commands**: Easy-to-use admin commands to manage combat logging
- **Public API**: Integrate combat logging checks into your own plugins

## Installation

1. Download the plugin JAR file
2. Place it in your server's `plugins/` directory
3. Restart the server
4. Configure `plugins/SMPCombatLog/config.yml` as needed

## Configuration

Edit `plugins/SMPCombatLog/config.yml`:

```yaml
# Enable or disable combat logging system
combat-log-enabled: true

# Combat duration in seconds
combat-duration: 10

# BossBar settings
bossbar:
  enabled: true
  title: "Combat Active"
  color: "RED"

# Glow effect settings
glow:
  enabled: true
  color: "RED"

# Punishment settings
punishment:
  type: "kill" # Options: kill, damage
  broadcast: true

# Messages (MiniMessage formatting)
messages:
  combat-start: "<red>⚔ <bold>COMBAT STARTED</bold> ⚔<reset>\n<red>You are fighting <yellow>%opponent%<reset>"
  combat-end: "<green>✓ <bold>Combat Ended<reset>"
  # ... more messages
```

## Commands

### `/smpc state [on|off]`
Manage the combat logging system.

- `/smpc state` - Show current status
- `/smpc state on` - Enable combat logging
- `/smpc state off` - Disable combat logging

**Permissions:**
- `smpcombatlog.state` - View status
- `smpcombatlog.state.enable` - Enable combat logging
- `smpcombatlog.state.disable` - Disable combat logging

### `/smpc reload`
Reload the configuration file.

**Permission:** `smpcombatlog.reload`

### Bypass Permission
Grant the `smpcombatlog.bypass` permission to a player to prevent them from entering combat entirely. Players with this permission:
- Cannot initiate combat with other players
- Cannot be put into combat by other players
- Are unaffected by the combat logging system

## Permissions

| Permission | Description | Default |
|-----------|-----------|---------|
| `smpcombatlog.state` | View combat logging status | Op |
| `smpcombatlog.state.enable` | Enable combat logging | Op |
| `smpcombatlog.state.disable` | Disable combat logging | Op |
| `smpcombatlog.reload` | Reload configuration | Op |
| `smpcombatlog.bypass` | Bypass combat logging entirely | None |

## API Usage

### Getting the API Instance

```java
import org.m9mx.smpcombatlog.api.CombatLogAPI;
import org.m9mx.smpcombatlog.SMPCombatLog;

// Get the plugin instance
SMPCombatLog plugin = (SMPCombatLog) Bukkit.getPluginManager().getPlugin("SMPCombatLog");

// Get the API
CombatLogAPI api = plugin.getAPI();
```

### API Methods

#### Check if Combat Logging is Enabled
```java
boolean enabled = api.isCombatLogEnabled();
```

#### Enable/Disable Combat Logging
```java
api.setCombatLogEnabled(true);  // Enable
api.setCombatLogEnabled(false); // Disable
```

#### Check if Player is in Combat
```java
Player player = Bukkit.getPlayer("PlayerName");
boolean inCombat = api.isPlayerInCombat(player);

// Or by UUID
UUID playerUUID = UUID.fromString("...");
boolean inCombat = api.isPlayerInCombat(playerUUID);
```

#### Get Combat Opponent
```java
Player player = Bukkit.getPlayer("PlayerName");
Player opponent = api.getOpponent(player);

// Or by UUID
Player opponent = api.getOpponent(playerUUID);
```

#### Check if Player is Bypassed
```java
Player player = Bukkit.getPlayer("PlayerName");
boolean bypassed = api.isPlayerBypassed(player);

// Or by UUID
boolean bypassed = api.isPlayerBypassed(playerUUID);
```

#### Manage Bypass List
```java
Player player = Bukkit.getPlayer("PlayerName");

// Add bypass
api.addBypass(player);

// Or by UUID
api.addBypass(playerUUID);

// Remove bypass
api.removeBypass(player);

// Or by UUID
api.removeBypass(playerUUID);
```

### Example: Custom Combat Check

```java
public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    SMPCombatLog plugin = (SMPCombatLog) Bukkit.getPluginManager().getPlugin("SMPCombatLog");
    CombatLogAPI api = plugin.getAPI();
    
    if (api.isPlayerInCombat(player)) {
        player.sendMessage("You cannot do this while in combat!");
        event.setCancelled(true);
        return;
    }
    
    // Allow action
}
```

### Example: Preventing Specific Players from Combat

```java
public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    
    if (player.hasMetadata("protect-from-pvp")) {
        SMPCombatLog plugin = (SMPCombatLog) Bukkit.getPluginManager().getPlugin("SMPCombatLog");
        CombatLogAPI api = plugin.getAPI();
        
        // Add to bypass - they won't enter combat
        api.addBypass(player.getUniqueId());
    }
}
```

## How Combat Works

1. **Combat Initiation**: When a player hits another player, both players enter combat
2. **Combat Timer**: A configurable timer starts (default: 10 seconds)
3. **Visual Effects**: Boss bar and glow effect display (if enabled)
4. **Combat Extension**: Any hit during combat extends the timer for both players
5. **Combat End**: When the timer expires, both players exit combat
6. **Combat Logging**: If a player disconnects or dies while in combat, they receive a punishment (kill or damage)

## Message Formatting

Messages use MiniMessage format for colors and styling:

- `<red>` - Red text
- `<green>` - Green text
- `<yellow>` - Yellow text
- `<bold>` - Bold text
- `<reset>` - Reset formatting
- `<dark_red>` - Dark red text
- `<aqua>` - Aqua text

See [MiniMessage Documentation](https://docs.advntr.dev/minimessage/format.html) for more options.

## Support & Issues

For bug reports and feature requests, visit the [GitHub Repository](https://github.com/M9MX/SMPCombatLog).

## Author

M9MX

## License

All rights reserved.
