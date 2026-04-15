package me.aurora.auroracombat.killreward;

import me.aurora.auroracombat.AuroraCombat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KillRewardManager {
    private final AuroraCombat plugin;
    private final Map<UUID, Map<UUID, List<Long>>> killLog = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lootProtection = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> lootRights = new ConcurrentHashMap<>();
    private final Map<UUID, Long> killCmdCooldown = new ConcurrentHashMap<>();

    public KillRewardManager(AuroraCombat plugin) { this.plugin = plugin; }
    public void reload() {}

    public void recordKill(Player killer, Player victim) {
        UUID ku = killer.getUniqueId(), vu = victim.getUniqueId();
        long now = System.currentTimeMillis();

        runKillCommands(killer, victim);

        if (plugin.getConfigManager().isAntiKillAbuseEnabled()) checkAbuse(killer, victim, now);

        int lootTime = plugin.getConfigManager().getLootProtectionTime();
        if (lootTime > 0) {
            lootProtection.put(vu, now + lootTime * 1000L);
            lootRights.put(ku, vu);
        }

        plugin.getCombatManager().removeEnemy(ku, vu);
        plugin.getCombatManager().removeEnemy(vu, ku);

        int deathCd = plugin.getConfigManager().getCommandCooldownAfterDeath();
        if (deathCd > 0) {
            var data = plugin.getPlayerDataManager().get(vu);
            if (data != null) data.setDeathCommandCooldownEnd(now + deathCd * 1000L);
        }
    }

    private void runKillCommands(Player killer, Player victim) {
        List<String> cmds = plugin.getConfigManager().getCommandsOnKill();
        if (cmds.isEmpty()) return;
        int cdSecs = plugin.getConfigManager().getKillCommandCooldown();
        if (cdSecs >= 0) {
            Long last = killCmdCooldown.get(killer.getUniqueId());
            if (last != null && System.currentTimeMillis() - last < cdSecs * 1000L) return;
            killCmdCooldown.put(killer.getUniqueId(), System.currentTimeMillis());
        }
        String item = killer.getInventory().getItemInMainHand().getType().name();
        for (String cmd : cmds) {
            if (cmd.isEmpty()) continue;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    cmd.replace("{player}", killer.getName()).replace("{victim}", victim.getName())
                       .replace("{item}", item));
        }
    }

    private void checkAbuse(Player killer, Player victim, long now) {
        UUID ku = killer.getUniqueId(), vu = victim.getUniqueId();
        int timeLimit = plugin.getConfigManager().getAntiKillAbuseTimeLimit();
        long cutoff = now - timeLimit * 60L * 1000L;
        Map<UUID, List<Long>> kLog = killLog.computeIfAbsent(ku, k -> new ConcurrentHashMap<>());
        List<Long> ts = kLog.computeIfAbsent(vu, k -> new ArrayList<>());
        ts.removeIf(t -> t < cutoff);
        ts.add(now);
        int max = plugin.getConfigManager().getAntiKillAbuseMaxKills();
        if (plugin.getConfigManager().isAntiKillAbuseWarnBefore() && ts.size() == max)
            plugin.getMessageManager().send(killer, "Kill_Abuse_Warning");
        if (ts.size() > max) {
            plugin.getMessageManager().send(killer, "Kill_Abuse_Punished");
            for (String cmd : plugin.getConfigManager().getAntiKillAbuseCommands())
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        cmd.replace("{player}", killer.getName()).replace("{victim}", victim.getName()));
            ts.clear();
        }
    }

    public boolean hasLootRights(Player player, UUID victimUuid) {
        return victimUuid.equals(lootRights.get(player.getUniqueId())) && isLootProtected(victimUuid);
    }

    public boolean isLootProtected(UUID victimUuid) {
        Long exp = lootProtection.get(victimUuid);
        if (exp == null) return false;
        if (System.currentTimeMillis() > exp) { lootProtection.remove(victimUuid); return false; }
        return true;
    }
}
