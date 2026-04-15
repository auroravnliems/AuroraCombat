package me.aurora.auroracombat.pvptoggle;

import me.aurora.auroracombat.AuroraCombat;
import me.aurora.auroracombat.player.PlayerData;
import org.bukkit.entity.Player;

public class PvPToggleManager {
    private final AuroraCombat plugin;
    public PvPToggleManager(AuroraCombat plugin) { this.plugin = plugin; }
    public void reload() {}

    public void togglePvP(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getOrCreate(player);
        if (data.isPvPToggleOnCooldown()) {
            plugin.getMessageManager().send(player, "PvP_Toggle_Cooldown", "time", data.getPvPToggleCooldownLeft());
            return;
        }
        boolean newState = !data.isPvPEnabled();
        if (plugin.getConfigManager().isPvPStateCooldownEnabled() && !newState) {
            if (data.isPvPStateOnCooldown()) {
                plugin.getMessageManager().send(player, "PvP_Toggle_Cooldown", "time", data.getPvPStateCooldownLeft());
                return;
            }
            data.setPvPStateCooldownEnd(System.currentTimeMillis() + plugin.getConfigManager().getPvPStateCooldownTime() * 1000L);
        }
        data.setPvPEnabled(newState);
        data.setPvPToggleCooldownEnd(System.currentTimeMillis() + plugin.getConfigManager().getPvPToggleCooldown() * 1000L);
        plugin.getMessageManager().send(player, newState ? "PvP_Enabled" : "PvP_Disabled");
    }

    public void setPvP(Player player, boolean enabled) {
        plugin.getPlayerDataManager().getOrCreate(player).setPvPEnabled(enabled);
    }

    public boolean isPvPEnabled(Player player) {
        return plugin.getPlayerDataManager().getOrCreate(player).isPvPEnabled();
    }

    public void grantPvP(Player player, int seconds) {
        PlayerData data = plugin.getPlayerDataManager().getOrCreate(player);
        data.setPvPGrant(System.currentTimeMillis() + seconds * 1000L);
        data.setPvPEnabled(false);
    }

    public boolean canAttack(Player attacker, Player defender) {
        if (attacker.hasPermission("auroracombat.bypass.pvptoggle")) return true;
        PlayerData atkData = plugin.getPlayerDataManager().getOrCreate(attacker);
        PlayerData defData = plugin.getPlayerDataManager().getOrCreate(defender);
        if (!atkData.isPvPEnabled()) return false;
        if (!defData.isPvPEnabled() && !defData.isPvPGrantActive()) return false;
        return true;
    }
}
