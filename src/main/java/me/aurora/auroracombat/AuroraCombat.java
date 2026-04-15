package me.aurora.auroracombat;

import me.aurora.auroracombat.antiborder.BorderManager;
import me.aurora.auroracombat.combat.CombatManager;
import me.aurora.auroracombat.commands.*;
import me.aurora.auroracombat.config.ConfigManager;
import me.aurora.auroracombat.config.MessageManager;
import me.aurora.auroracombat.events.*;
import me.aurora.auroracombat.itemcooldown.ItemCooldownManager;
import me.aurora.auroracombat.killreward.KillRewardManager;
import me.aurora.auroracombat.manager.PlayerDataManager;
import me.aurora.auroracombat.newbie.NewbieManager;
import me.aurora.auroracombat.pvptoggle.PvPToggleManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AuroraCombat extends JavaPlugin {

    private static AuroraCombat instance;

    private ConfigManager configManager;
    private MessageManager messageManager;
    private PlayerDataManager playerDataManager;
    private CombatManager combatManager;
    private PvPToggleManager pvpToggleManager;
    private NewbieManager newbieManager;
    private ItemCooldownManager itemCooldownManager;
    private KillRewardManager killRewardManager;
    private BorderManager borderManager;


    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResource("messages.properties", false);

        configManager       = new ConfigManager(this);
        messageManager      = new MessageManager(this);
        playerDataManager   = new PlayerDataManager(this);
        combatManager       = new CombatManager(this);
        pvpToggleManager    = new PvPToggleManager(this);
        newbieManager       = new NewbieManager(this);
        itemCooldownManager = new ItemCooldownManager(this);
        killRewardManager   = new KillRewardManager(this);
        borderManager       = new BorderManager(this);

        registerCommands();
        registerEvents();

        getLogger().info("AuroraCombat v" + getPluginMeta().getVersion() + " enabled on Paper " + Bukkit.getVersion());
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        if (combatManager != null) combatManager.shutdown();
        if (newbieManager != null) newbieManager.shutdown();
        if (playerDataManager != null) playerDataManager.saveAll();
        getLogger().info("AuroraCombat disabled.");
    }



    private void registerCommands() {
        PvPToggleCommand pvpCmd = new PvPToggleCommand(this);
        getCommand("pvp").setExecutor(pvpCmd);
        getCommand("pvp").setTabCompleter(pvpCmd);

        getCommand("combattag").setExecutor(new CombatTagCommand(this));
        getCommand("newbie").setExecutor(new NewbieCommand(this));

        AcCommand acCmd = new AcCommand(this);
        getCommand("ac").setExecutor(acCmd);
        getCommand("ac").setTabCompleter(acCmd);
    }

    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new CombatListener(this), this);
        pm.registerEvents(new PvPToggleListener(this), this);
        pm.registerEvents(new NewbieListener(this), this);
        pm.registerEvents(new CombatLogListener(this), this);
        pm.registerEvents(new ItemCooldownListener(this), this);
        pm.registerEvents(new KillListener(this), this);
        pm.registerEvents(new DisableOnHitListener(this), this);
        pm.registerEvents(new BorderListener(this), this);
        pm.registerEvents(new PlayerSessionListener(this), this);
    }

    public void reload() {
        reloadConfig();
        configManager.reload();
        messageManager.reload();
        combatManager.reload();
        pvpToggleManager.reload();
        newbieManager.reload();
        itemCooldownManager.reload();
        killRewardManager.reload();
        borderManager.reload();
    }

    public static AuroraCombat getInstance()             { return instance; }
    public ConfigManager getConfigManager()              { return configManager; }
    public MessageManager getMessageManager()            { return messageManager; }
    public PlayerDataManager getPlayerDataManager()      { return playerDataManager; }
    public CombatManager getCombatManager()              { return combatManager; }
    public PvPToggleManager getPvpToggleManager()        { return pvpToggleManager; }
    public NewbieManager getNewbieManager()              { return newbieManager; }
    public ItemCooldownManager getItemCooldownManager()  { return itemCooldownManager; }
    public KillRewardManager getKillRewardManager()      { return killRewardManager; }
    public BorderManager getBorderManager()              { return borderManager; }
}