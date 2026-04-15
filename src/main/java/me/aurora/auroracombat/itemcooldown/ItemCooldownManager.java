package me.aurora.auroracombat.itemcooldown;

import me.aurora.auroracombat.AuroraCombat;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ItemCooldownManager {
    private final AuroraCombat plugin;
    private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    public ItemCooldownManager(AuroraCombat plugin) { this.plugin = plugin; }
    public void reload() {}

    public boolean hasCooldown(Player player, Material mat) {
        Map<String, Long> m = cooldowns.get(player.getUniqueId());
        if (m == null) return false;
        Long exp = m.get(mat.name());
        if (exp == null) return false;
        if (System.currentTimeMillis() > exp) { m.remove(mat.name()); return false; }
        return true;
    }

    public int getCooldownLeft(Player player, Material mat) {
        Map<String, Long> m = cooldowns.get(player.getUniqueId());
        if (m == null) return 0;
        Long exp = m.get(mat.name());
        if (exp == null) return 0;
        return (int) Math.max(0, (exp - System.currentTimeMillis()) / 1000);
    }

    public void setCooldown(Player player, Material mat, int seconds) {
        if (seconds <= 0) return;
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>())
                 .put(mat.name(), System.currentTimeMillis() + seconds * 1000L);
    }

    public void clearAll(UUID uuid) { cooldowns.remove(uuid); }

    public int getConfiguredCooldown(Player player, Material mat) {
        boolean inCombat = plugin.getCombatManager().isTagged(player.getUniqueId());
        if (inCombat) {
            int cd = plugin.getConfigManager().getCombatItemCooldown(mat.name());
            if (cd >= 0) return cd;
        }
        return plugin.getConfigManager().getGlobalItemCooldown(mat.name());
    }
}
