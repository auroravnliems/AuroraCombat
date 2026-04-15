# ⚔️ AuroraCombat

**AuroraCombat** is a comprehensive PvP combat management plugin for **Paper 1.21.1+** with full **Folia** support. It covers everything from combat tagging, PvP toggling, and newbie protection to anti-border hopping, item cooldowns, and a kill reward/punishment system — all deeply configurable through `config.yml` and `messages.properties`.

---

## 📋 Requirements

| Requirement | Version |
|---|---|
| Server | Paper / Folia 1.21.1+ |
| Java | 21+ |
| (Optional) WorldGuard | 7.x+ |
| (Optional) Vault | Any |
| (Optional) PlaceholderAPI | Any |

---

## 🚀 Installation

1. Drop `AuroraCombat.jar` into your `plugins/` folder.
2. Restart the server.
3. Edit `plugins/AuroraCombat/config.yml` to your liking.
4. Use `/ac reload` to apply changes without restarting.

---

## ✨ Features

### 1. 🏷️ Combat Tag

When players attack each other, both are **tagged** for a configurable duration. Several actions are restricted while tagged.

**Visual indicators while tagged:**
- **Action Bar** — countdown timer with enemy name and health (`<time>`, `<enemy>`, `<enemy_health>`)
- **Boss Bar** — progress bar counting down at the top of the screen
- **Nametag Prefix** — a custom prefix rendered above the player's head
- **Glowing Effect** — players glow while in combat (toggleable)

**Actions blocked during combat (all individually configurable):**
- Teleporting
- Entering Nether / End portals
- Using Ender Pearls, Chorus Fruit, Riptide
- Gliding with Elytra / using fireworks
- Eating, placing or breaking blocks, opening inventory
- Any command not on the whitelist

**Tag renewal items:** Ender Pearl, Wind Charge, Mace (1.21+), PvE mobs (optional, hostile-only mode available).

**WorldGuard region exclusions:** Defined regions (e.g. `spawn`, `safezone`) can be excluded from triggering combat tags.

---

### 2. 🚪 Combat Log

Punishes players who disconnect while in combat.

| Feature | Description |
|---|---|
| **Kill on Logout** | Instantly kills the player upon disconnecting while tagged |
| **Drop on Logout** | Configurable drops: inventory, armor, experience |
| **NPC Spawn** | (Optional) Spawns an NPC in place of the logged-out player |
| **Block Login** | Prevents re-login while the NPC is still alive |
| **Money Penalty** | Deducts money on combat log (requires Vault) |
| **Punish on Kick** | Also punishes players kicked by the server (e.g. spam detection) |
| **Broadcast Command** | Runs a console command to announce the combat log to all players |

---

### 3. ⚔️ PvP Toggle

Lets players control their own PvP status.

- `/pvp` — toggle PvP on or off
- Toggle cooldown between state changes (default: 15 seconds)
- State cooldown: extra delay before PvP can be turned off again (optional)
- Particle effect on toggle
- Nametag prefix indicating PvP state
- **PvP Grant** — admins can temporarily force-protect a player from PvP
- **WorldGuard Override** — WorldGuard PvP regions override the player's toggle state

---

### 4. 🛡️ Newbie Protection

Automatically grants a protection shield to first-time players.

- Configurable protection duration (default: 120 seconds)
- Boss Bar countdown visible to the protected player
- Players can manually disable it with `/newbie disable` (optional)
- Command blacklist while under protection
- Option to protect from all damage sources (`ProtectFromEverything`)
- Option to prevent picking up items while protected

---

### 5. 🚧 Anti Border Hopping

Prevents tagged players from escaping combat by running into a WorldGuard safe zone.

**Three layers of protection:**

| Layer | Description |
|---|---|
| **Vulnerable** | Player remains tagged even after entering the safe zone |
| **Barrier** | Displays a fake block wall (client-side only) blocking the entrance |
| **Push Back** | Pushes the player back out with configurable force |

- Blocks Ender Pearl teleports into safe zones
- Integrated with WorldGuard region detection

---

### 6. 🧪 Item Cooldowns

Applies custom cooldowns to specific items.

- **Combat Cooldown** — only active while the player is in combat
- **Global Cooldown** — always active regardless of combat state

Any `Material` name is supported. Defaults include: Ender Pearl, Wind Charge, Mace, Golden Apple, Enchanted Golden Apple.

Players with `auroracombat.bypass.itemcooldown` are exempt.

---

### 7. 💀 Kill System

**Rewards & penalties:**
- Money reward / penalty on kill or death (requires Vault)
- Steal a percentage of the victim's experience
- Steal money from the victim
- Lightning strike death effect
- Console commands on kill (with cooldown) and on respawn

**Drop protection:**
- Items dropped by the victim are protected for X seconds
- Only the killer has loot rights during that window

**Anti Kill Abuse:**
- Limits how many times a player can kill the same target within a time window
- Pre-warn before the punishment threshold is reached
- Fully customizable punishment commands (kick, ban, etc.)
- Post-respawn protection timer
- Post-teleport protection timer

---

### 8. 🚫 Disable on Hit

Automatically removes special states when a player takes damage:

- **Flight** (`/fly`) — optionally restored after combat ends
- **Creative mode** — reverted to Survival
- **God mode** — removed on hit
- **Invisibility** — removed on hit (optional)

---

### 9. 🩸 Miscellaneous

- **PvP Blood** — particle blood effect on hit
- **Auto Respawn** — automatically respawns players on death
- **Auto Soup** — automatically consumes food below a health threshold
- **Show Health Under Name** — displays health below nametags
- **Player Drop Mode** — controls item drop behavior on death (`ALWAYS`, `DROP`, `KEEP`, `TRANSFER`, `CLEAR`)
- **World Exclusions** — exclude entire worlds from all plugin features
- **Harmful Potions** — configurable list of effects that count as combat actions
- **Database** — SQLite (default) and MySQL with connection pooling

---

## 📁 File Structure

```
plugins/AuroraCombat/
├── config.yml           # All plugin settings
└── messages.properties  # All player-facing messages
```

---

## ⚙️ Configuration Reference (`config.yml`)

### General

```yaml
General:
  WorldExclusions:
    - 'example_world'    # Worlds where the plugin is inactive
  AutoRespawn: true
  DeathsWhileTaggedCountPvP: true
```

### Combat Tag

```yaml
CombatTag:
  Enabled: true
  Time: 15               # Combat duration in seconds
  Glowing: true
  UntagOnKill: false     # Remove tag immediately after killing the opponent
  SelfTag: false
  EnderPearlRenewsTag: true
  WindChargeRenewsTag: true
  MaceRenewsTag: true    # 1.21+ Mace attacks renew the tag

  Display:
    ActionBar:
      Enabled: true
      Message: '&8[&c&lCOMBAT&8] &7<time>s &8• &7vs &f<enemy> &8(&c<enemy_health>❤&8)'
    BossBar:
      Enabled: true
      Message: '&8[&c&lCOMBAT&8] &e&l<time> seconds remaining'
      BarColor: RED
    Nametags:
      Enabled: true
      Prefix: '&4⚔ &c'

  BlockedActions:
    Teleport: true
    EnterPortal: true
    EnderPearls: false
    Riptide: true
    # ... see config.yml for the full list

  Commands:
    Enabled: true
    Whitelist: true      # Only allow commands listed below
    List:
      - 'msg'
      - 'combattag'

  WorldGuardExclusions:
    - 'spawn'
    - 'safezone'
```

### PvP Toggle

```yaml
PvPToggle:
  DefaultPvP: true       # Default PvP state on first join
  ToggleCooldown: 15     # Seconds between toggles
  WorldGuardOverrides: true
```

### Newbie Protection

```yaml
NewbieProtection:
  Enabled: true
  Time: 120              # Seconds of protection
  AllowPlayerDisable: true
  BossBar:
    Enabled: true
    Message: '&6🛡 &aNewbie Protection &6🛡 &e&l<time>'
    BarColor: GREEN
```

### Anti Border Hopping

```yaml
AntiBorderHopping:
  Barrier:
    Enabled: true
    Material: RED_STAINED_GLASS
    Radius: 6
  PushBack:
    Enabled: true
    Force: 1.2
    BlockEnderPearl: true
```

### Item Cooldowns

```yaml
ItemCooldowns:
  Combat:                # Only while in combat
    ENDER_PEARL: 15
    GOLDEN_APPLE: 30
  Global:                # Always active
    ENCHANTED_GOLDEN_APPLE: 120
```

### Kill System

```yaml
PlayerKills:
  MoneyReward: 100.0
  MoneyPenalty: 0.0
  ExpSteal: 0.0
  LootProtection:
    Time: 20             # Seconds of loot protection
  AntiKillAbuse:
    Enabled: true
    MaxKills: 5
    TimeLimit: 20        # Minutes
    WarnBefore: true
    CommandsOnAbuse:
      - 'kick {player} Kill abuse detected!'
  CommandsOnKill:
    Cooldown: -1         # -1 = no cooldown
    Commands:
      - 'broadcast {player} killed {victim}!'
```

### Combat Log

```yaml
CombatLog:
  MoneyPenalty: 0.0
  KillOnLogout:
    Enabled: true
    PlayerDrops:
      Inventory: false
      Armor: false
      Experience: false
  CommandsOnCombatLog:
    - 'broadcast &c{player} cowardly logged out during combat!'
```

### Database

```yaml
Database:
  Type: SQLite           # Or MySQL
  MySQL:
    Host: 127.0.0.1
    Port: 3306
    Database: minecraft
    Username: root
    Password: '12345'
    PoolSize: 10
```

---

## 💬 Messages (`messages.properties`)

Supports **legacy color codes** (`&c`, `&a`…), **hex colors** (`&#RRGGBB`), and the `!actionbar` prefix to display a message on the action bar instead of chat.

```properties
# Plugin prefix (gradient)
Prefix=&#FB287AAurora...

# Examples
Tagged_Attacker={prefix} &7You attacked &f{player}&7! You cannot leave combat!
PvP_Disabled={prefix} &aPvP disabled! You are now protected.
PushBack_Message=!actionbar &#FF4444⚠ &cYou cannot enter a safe zone while in combat!
Item_Cooldown=!actionbar {prefix} &cItem on cooldown! &e{time}s remaining
```

**Available placeholders:**

| Placeholder | Used in |
|---|---|
| `{prefix}` | Any message |
| `{player}` | Combat, kill, newbie messages |
| `{victim}` | Kill commands |
| `{time}` | Cooldown, combat, newbie messages |
| `{enemy}` | Combat action bar / boss bar |
| `{enemy_health}` | Combat action bar |
| `{item}` | Kill commands |

---

## 🎮 Commands

### Player Commands

| Command | Description | Permission |
|---|---|---|
| `/pvp` | Toggle your PvP state | `auroracombat.pvp` |
| `/pvp on` | Enable PvP | `auroracombat.pvp` |
| `/pvp off` | Disable PvP | `auroracombat.pvp` |
| `/pvp status` | Check your PvP state | `auroracombat.pvp` |
| `/combattag` | Check your combat status | `auroracombat.combattag` |
| `/newbie` | Check newbie protection status | `auroracombat.newbie` |
| `/newbie disable` | Manually remove newbie protection | `auroracombat.newbie` |
| `/newbie status` | View remaining protection time | `auroracombat.newbie` |

**Aliases:** `/pvptoggle`, `/ct`, `/combat`, `/auroracombat`

### Admin Commands (`/ac`)

| Command | Description |
|---|---|
| `/ac reload` | Reload all configuration files |
| `/ac info` | Show plugin version and number of tagged players |
| `/ac tag <player>` | Force-tag a player |
| `/ac untag <player>` | Force-untag a player |
| `/ac check <player>` | View a player's PvP, combat, and newbie status |
| `/ac pvpon <player>` | Force-enable PvP for a player |
| `/ac pvpoff <player>` | Force-disable PvP for a player |
| `/ac pvpgrant <player> [seconds]` | Grant temporary PvP protection to a player |

---

## 🔑 Permissions

| Permission | Description | Default |
|---|---|---|
| `auroracombat.*` | All permissions | OP |
| `auroracombat.pvp` | Use `/pvp` | true |
| `auroracombat.combattag` | Use `/combattag` | true |
| `auroracombat.newbie` | Use `/newbie` | true |
| `auroracombat.admin` | Use `/ac` | OP |
| `auroracombat.bypass.combattag` | Never gets combat-tagged | OP |
| `auroracombat.bypass.pvptoggle` | Can attack players with PvP off | OP |
| `auroracombat.bypass.newbie` | Can attack newbie-protected players | OP |
| `auroracombat.bypass.itemcooldown` | Immune to item cooldowns | OP |
| `auroracombat.bypass.border` | Immune to anti border hopping | OP |

---

## 🔗 Plugin Integrations

| Plugin | Integration |
|---|---|
| **WorldGuard** | Region exclusions for combat tag; safe zone detection for anti border hopping |
| **Vault** | Money rewards, penalties, and theft on kill/death/combat log |
| **PlaceholderAPI** | Placeholder support in messages and displays |

---

## 🏗️ Architecture

```
me.aurora.auroracombat
├── AuroraCombat.java               # Main plugin class
├── combat/
│   └── CombatManager.java          # Tag state, timers, boss bars, glowing
├── pvptoggle/
│   └── PvPToggleManager.java       # PvP toggle logic, PvP grant
├── newbie/
│   └── NewbieManager.java          # Newbie protection timer, boss bar
├── antiborder/
│   ├── BorderManager.java          # Barrier display, pushback, pearl blocking
│   └── WorldGuardChecker.java      # WorldGuard region queries
├── itemcooldown/
│   └── ItemCooldownManager.java    # Per-player item cooldown tracking
├── killreward/
│   └── KillRewardManager.java      # Kill commands, anti-abuse, loot protection
├── manager/
│   └── PlayerDataManager.java      # PlayerData cache, database save/load
├── player/
│   └── PlayerData.java             # Per-player state (PvP, newbie, cooldowns…)
├── commands/                       # AcCommand, PvPToggleCommand, CombatTagCommand…
├── events/                         # CombatListener, CombatLogListener, KillListener…
├── config/
│   ├── ConfigManager.java          # config.yml reader
│   └── MessageManager.java         # Message parsing and delivery
└── util/
    └── ColorUtil.java              # Legacy + hex color → Adventure Component
```

---

## 🛠️ Building

```bash
cd AuroraCombat

# Build shaded JAR
./gradlew shadowJar

# Output
build/libs/AuroraCombat-<version>.jar
```

**Key dependencies:**
- `io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT`
- `org.xerial:sqlite-jdbc:3.45.1.0`
- ProGuard obfuscation (production build via `proguard-rules.pro`)

---

## 📝 Changelog

### v1.0.0 — Initial Release
- Combat Tag with action bar, boss bar, nametag prefix, and glowing
- PvP Toggle with cooldown, state cooldown, and WorldGuard override
- Newbie Protection with boss bar countdown
- Anti Border Hopping (barrier, pushback, Ender Pearl blocking)
- Item Cooldowns (combat-only and global)
- Kill System with anti-abuse, loot protection, and Vault money support
- Combat Log punishment (kill on logout, NPC spawn, money penalty)
- Disable on Hit (fly, game mode, god mode)
- SQLite and MySQL database support
- Folia support

---

## 👨‍💻 Author

**Aurora_VN** — ASG Network
