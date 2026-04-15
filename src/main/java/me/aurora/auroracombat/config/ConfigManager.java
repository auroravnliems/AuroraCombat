package me.aurora.auroracombat.config;

import me.aurora.auroracombat.AuroraCombat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    private final AuroraCombat plugin;
    private FileConfiguration cfg;

    public ConfigManager(AuroraCombat plugin) {
        this.plugin = plugin;
        this.cfg = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        this.cfg = plugin.getConfig();
    }

    public List<String> getWorldExclusions()        { return cfg.getStringList("General.WorldExclusions"); }
    public boolean isAutoRespawn()                   { return cfg.getBoolean("General.AutoRespawn", true); }
    public boolean isDeathsWhileTaggedCountPvP()     { return cfg.getBoolean("General.DeathsWhileTaggedCountPvP", true); }

    public boolean isCombatTagEnabled()              { return cfg.getBoolean("CombatTag.Enabled", true); }
    public int getCombatTagTime()                    { return cfg.getInt("CombatTag.Time", 15); }
    public boolean isCombatGlowing()                 { return cfg.getBoolean("CombatTag.Glowing", true); }
    public boolean isUntagOnKill()                   { return cfg.getBoolean("CombatTag.UntagOnKill", false); }
    public boolean isSelfTag()                       { return cfg.getBoolean("CombatTag.SelfTag", false); }
    public boolean isEnderPearlRenewsTag()           { return cfg.getBoolean("CombatTag.EnderPearlRenewsTag", true); }
    public boolean isWindChargeRenewsTag()           { return cfg.getBoolean("CombatTag.WindChargeRenewsTag", true); }
    public boolean isMaceRenewsTag()                 { return cfg.getBoolean("CombatTag.MaceRenewsTag", true); }
    public boolean isPvETagEnabled()                 { return cfg.getBoolean("CombatTag.PvETag.Enabled", false); }
    public boolean isPvETagOnlyHostile()             { return cfg.getBoolean("CombatTag.PvETag.OnlyHostile", true); }

    public boolean isCombatActionBarEnabled()        { return cfg.getBoolean("CombatTag.Display.ActionBar.Enabled", true); }
    public String getCombatActionBarMessage()        { return cfg.getString("CombatTag.Display.ActionBar.Message", "&c<time>s"); }
    public boolean isCombatBossBarEnabled()          { return cfg.getBoolean("CombatTag.Display.BossBar.Enabled", true); }
    public String getCombatBossBarMessage()          { return cfg.getString("CombatTag.Display.BossBar.Message", "&e<time>s"); }
    public String getCombatBossBarColor()            { return cfg.getString("CombatTag.Display.BossBar.BarColor", "RED"); }
    public boolean isCombatNametagEnabled()          { return cfg.getBoolean("CombatTag.Display.Nametags.Enabled", true); }
    public String getCombatNametagPrefix()           { return cfg.getString("CombatTag.Display.Nametags.Prefix", "&4⚔ &c"); }

    public boolean isBlockEnderPearlInCombat()       { return cfg.getBoolean("CombatTag.BlockedActions.EnderPearls", false); }
    public boolean isBlockChorusFruitInCombat()      { return cfg.getBoolean("CombatTag.BlockedActions.ChorusFruits", false); }
    public boolean isBlockTeleportInCombat()         { return cfg.getBoolean("CombatTag.BlockedActions.Teleport", true); }
    public boolean isBlockRiptideInCombat()          { return cfg.getBoolean("CombatTag.BlockedActions.Riptide", true); }
    public boolean isBlockEatInCombat()              { return cfg.getBoolean("CombatTag.BlockedActions.Eat", false); }
    public boolean isBlockPlaceBlocksInCombat()      { return cfg.getBoolean("CombatTag.BlockedActions.PlaceBlocks", false); }
    public boolean isBlockBreakBlocksInCombat()      { return cfg.getBoolean("CombatTag.BlockedActions.BreakBlocks", false); }
    public boolean isBlockOpenInventoryInCombat()    { return cfg.getBoolean("CombatTag.BlockedActions.OpenInventory", false); }
    public boolean isBlockPortalInCombat()           { return cfg.getBoolean("CombatTag.BlockedActions.EnterPortal", true); }
    public boolean isBlockElytraGliding()            { return cfg.getBoolean("CombatTag.BlockedActions.Elytra.BlockGliding", false); }
    public boolean isBlockFireworksInCombat()        { return cfg.getBoolean("CombatTag.BlockedActions.Elytra.BlockFireworks", false); }

    public boolean isCommandFilterEnabled()          { return cfg.getBoolean("CombatTag.Commands.Enabled", true); }
    public boolean isCommandsWhitelist()             { return cfg.getBoolean("CombatTag.Commands.Whitelist", true); }
    public List<String> getCommandFilterList()       { return cfg.getStringList("CombatTag.Commands.List"); }
    public List<String> getCommandsOnTag()           { return cfg.getStringList("CombatTag.CommandsOnTag"); }
    public List<String> getCommandsOnUntag()         { return cfg.getStringList("CombatTag.CommandsOnUntag"); }
    public List<String> getCombatTagWGExclusions()   { return cfg.getStringList("CombatTag.WorldGuardExclusions"); }

    public boolean isKillOnLogoutEnabled()           { return cfg.getBoolean("CombatLog.KillOnLogout.Enabled", true); }
    public boolean isKillOnLogoutDropInventory()     { return cfg.getBoolean("CombatLog.KillOnLogout.PlayerDrops.Inventory", true); }
    public boolean isKillOnLogoutDropExp()           { return cfg.getBoolean("CombatLog.KillOnLogout.PlayerDrops.Experience", true); }
    public boolean isPunishOnKickEnabled()           { return cfg.getBoolean("CombatLog.PunishOnKick.Enabled", true); }
    public boolean isMatchKickReason()               { return cfg.getBoolean("CombatLog.PunishOnKick.MatchKickReason", false); }
    public List<String> getKickReasons()             { return cfg.getStringList("CombatLog.PunishOnKick.KickReasons"); }
    public List<String> getCombatLogCommands()       { return cfg.getStringList("CombatLog.CommandsOnCombatLog"); }

    public boolean isDefaultPvPOn()                  { return cfg.getBoolean("PvPToggle.DefaultPvP", true); }
    public int getPvPToggleCooldown()                { return cfg.getInt("PvPToggle.ToggleCooldown", 15); }
    public boolean isPvPProtectionEffectEnabled()    { return cfg.getBoolean("PvPToggle.ProtectionEffect.Enabled", true); }
    public String getPvPProtectionParticle()         { return cfg.getString("PvPToggle.ProtectionEffect.Particle", "ENCHANT"); }
    public boolean isPvPStateCooldownEnabled()       { return cfg.getBoolean("PvPToggle.StateCooldown.Enabled", false); }
    public int getPvPStateCooldownTime()             { return cfg.getInt("PvPToggle.StateCooldown.Time", 30); }
    public boolean isPvPNameTagEnabled()             { return cfg.getBoolean("PvPToggle.NameTags.Enabled", false); }
    public String getPvPOnPrefix()                   { return cfg.getString("PvPToggle.NameTags.PrefixOn", "&4⚔ "); }
    public String getPvPOffPrefix()                  { return cfg.getString("PvPToggle.NameTags.PrefixOff", "&2✦ "); }
    public boolean isWorldGuardOverrides()           { return cfg.getBoolean("PvPToggle.WorldGuardOverrides", true); }

    public boolean isNewbieProtectionEnabled()       { return cfg.getBoolean("NewbieProtection.Enabled", true); }
    public int getNewbieProtectionTime()             { return cfg.getInt("NewbieProtection.Time", 600); }
    public boolean isNewbieAllowDisable()            { return cfg.getBoolean("NewbieProtection.AllowPlayerDisable", true); }
    public boolean isNewbieProtectFromEverything()   { return cfg.getBoolean("NewbieProtection.ProtectFromEverything", false); }
    public boolean isNewbieBossBarEnabled()          { return cfg.getBoolean("NewbieProtection.BossBar.Enabled", true); }
    public String getNewbieBossBarMessage()          { return cfg.getString("NewbieProtection.BossBar.Message", "&aNewbie &e<time>"); }
    public String getNewbieBossBarColor()            { return cfg.getString("NewbieProtection.BossBar.BarColor", "GREEN"); }
    public List<String> getNewbieCommandBlacklist()  { return cfg.getStringList("NewbieProtection.CommandBlacklist"); }

    public boolean isVulnerableEnabled()             { return cfg.getBoolean("AntiBorderHopping.Vulnerable.Enabled", true); }
    public boolean isVulnerableRenewTag()            { return cfg.getBoolean("AntiBorderHopping.Vulnerable.RenewCombatTag", true); }
    public boolean isBarrierEnabled()                { return cfg.getBoolean("AntiBorderHopping.Barrier.Enabled", true); }
    public String getBarrierMaterial()               { return cfg.getString("AntiBorderHopping.Barrier.Material", "RED_STAINED_GLASS"); }
    public int getBarrierRadius()                    { return cfg.getInt("AntiBorderHopping.Barrier.Radius", 6); }
    public boolean isPushBackEnabled()               { return cfg.getBoolean("AntiBorderHopping.PushBack.Enabled", true); }
    public double getPushBackForce()                 { return cfg.getDouble("AntiBorderHopping.PushBack.Force", 1.2); }
    public boolean isPushBackBlockEnderPearl()       { return cfg.getBoolean("AntiBorderHopping.PushBack.BlockEnderPearl", true); }

    public int getCombatItemCooldown(String mat)     { return cfg.getInt("ItemCooldowns.Combat." + mat, -1); }
    public int getGlobalItemCooldown(String mat)     { return cfg.getInt("ItemCooldowns.Global." + mat, -1); }

    public double getKillMoneyReward()               { return cfg.getDouble("PlayerKills.MoneyReward", 0.0); }
    public double getDeathMoneyPenalty()             { return cfg.getDouble("PlayerKills.MoneyPenalty", 0.0); }
    public boolean isDeathLightning()                { return cfg.getBoolean("PlayerKills.DeathEffects.Lightning", false); }
    public int getKillCommandCooldown()              { return cfg.getInt("PlayerKills.CommandsOnKill.Cooldown", -1); }
    public List<String> getCommandsOnKill()          { return cfg.getStringList("PlayerKills.CommandsOnKill.Commands"); }
    public List<String> getCommandsOnRespawn()       { return cfg.getStringList("PlayerKills.CommandsOnRespawn"); }
    public int getLootProtectionTime()               { return cfg.getInt("PlayerKills.LootProtection.Time", 20); }
    public boolean isAntiKillAbuseEnabled()          { return cfg.getBoolean("PlayerKills.AntiKillAbuse.Enabled", true); }
    public int getAntiKillAbuseMaxKills()            { return cfg.getInt("PlayerKills.AntiKillAbuse.MaxKills", 5); }
    public int getAntiKillAbuseTimeLimit()           { return cfg.getInt("PlayerKills.AntiKillAbuse.TimeLimit", 20); }
    public boolean isAntiKillAbuseWarnBefore()       { return cfg.getBoolean("PlayerKills.AntiKillAbuse.WarnBefore", true); }
    public List<String> getAntiKillAbuseCommands()   { return cfg.getStringList("PlayerKills.AntiKillAbuse.CommandsOnAbuse"); }
    public int getRespawnProtectionTime()            { return cfg.getInt("PlayerKills.AntiKillAbuse.RespawnProtection", 3); }
    public int getCommandCooldownAfterDeath()        { return cfg.getInt("PlayerKills.AntiKillAbuse.CommandCooldownAfterDeath.Time", 3); }
    public List<String> getCommandsBlockedAfterDeath(){ return cfg.getStringList("PlayerKills.AntiKillAbuse.CommandCooldownAfterDeath.Commands"); }

    public boolean isPvPBlood()                      { return cfg.getBoolean("OtherSettings.PvPBlood", true); }
    public boolean isIgnoreNoDamageHits()            { return cfg.getBoolean("OtherSettings.IgnoreNoDamageHits", true); }
    public boolean isProtectionMessagesToActionBar() { return cfg.getBoolean("OtherSettings.ProtectionMessagesToActionBar", true); }
    public String getPlayerDropMode()                { return cfg.getString("OtherSettings.PlayerDropMode", "ALWAYS").toUpperCase(); }
    public boolean isShowHealthUnderName()           { return cfg.getBoolean("OtherSettings.ShowHealthUnderName.Enabled", true); }

    public boolean isDisableFlyOnHit()               { return cfg.getBoolean("DisableOnHit.Fly", true); }
    public boolean isRestoreFly()                    { return cfg.getBoolean("DisableOnHit.RestoreFly", true); }
    public boolean isDisableGameModeOnHit()          { return cfg.getBoolean("DisableOnHit.GameMode", true); }
    public boolean isDisableGodModeOnHit()           { return cfg.getBoolean("DisableOnHit.GodMode", true); }
    public boolean isDisableInvisibilityOnHit()      { return cfg.getBoolean("DisableOnHit.Invisibility", false); }

    public List<String> getHarmfulPotions()          { return cfg.getStringList("HarmfulPotions"); }
    public boolean isDebugMode()                     { return cfg.getBoolean("DebugMode", false); }
}
