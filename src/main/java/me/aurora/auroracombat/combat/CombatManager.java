package me.aurora.auroracombat.combat;

import me.aurora.auroracombat.AuroraCombat;
import me.aurora.auroracombat.util.ColorUtil;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManager {

    private final AuroraCombat plugin;

    private final Map<UUID, Long> combatEndTimes = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> enemies = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitTask> tasks = new ConcurrentHashMap<>();
    private final Map<UUID, BossBar> bossBars = new ConcurrentHashMap<>();

    public CombatManager(AuroraCombat plugin) {
        this.plugin = plugin;
    }

    public void reload() {}

    public void shutdown() {
        new ArrayList<>(combatEndTimes.keySet()).forEach(uuid ->
            forceUntag(uuid, Bukkit.getPlayer(uuid))
        );
    }

    public void tag(Player attacker, Player defender) {
        if (!plugin.getConfigManager().isCombatTagEnabled()) return;

        boolean atkNew = !isTagged(attacker.getUniqueId());
        boolean defNew = !isTagged(defender.getUniqueId());

        tagSingle(attacker, defender.getUniqueId());
        tagSingle(defender, attacker.getUniqueId());

        if (atkNew) {
            plugin.getMessageManager().send(attacker, "Tagged_Attacker", "player", defender.getName());
            runCommands(plugin.getConfigManager().getCommandsOnTag(), attacker);
        }
        if (defNew) {
            plugin.getMessageManager().send(defender, "Tagged_Defender", "player", attacker.getName());
            runCommands(plugin.getConfigManager().getCommandsOnTag(), defender);
        }
    }

    private void tagSingle(Player player, UUID enemyUuid) {
        UUID uuid = player.getUniqueId();
        int tagTime = plugin.getConfigManager().getCombatTagTime();
        long newEnd = System.currentTimeMillis() + tagTime * 1000L;

        boolean wasTagged = combatEndTimes.containsKey(uuid);
        combatEndTimes.put(uuid, newEnd);
        enemies.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet()).add(enemyUuid);

        if (!wasTagged) {
            player.setGlowing(plugin.getConfigManager().isCombatGlowing());
            showBossBar(player, tagTime);
        }

        BukkitTask old = tasks.remove(uuid);
        if (old != null) old.cancel();
        tasks.put(uuid, startCountdown(player, tagTime));
        updateBossBar(player, tagTime);
    }

    private BukkitTask startCountdown(Player player, int totalSeconds) {
        UUID uuid = player.getUniqueId();
        return new BukkitRunnable() {
            @Override
            public void run() {
                Player p = Bukkit.getPlayer(uuid);
                if (p == null) { cancel(); return; }

                int left = getTimeLeft(uuid);

                if (plugin.getConfigManager().isCombatActionBarEnabled()) {
                    String msg = plugin.getConfigManager().getCombatActionBarMessage()
                            .replace("<time>", String.valueOf(left))
                            .replace("<enemy>", getCurrentEnemyName(uuid))
                            .replace("<enemy_health>", getCurrentEnemyHealth(uuid));
                    plugin.getMessageManager().sendActionBar(p, msg);
                }
                updateBossBar(p, left);

                if (left <= 0) { untag(uuid, p); cancel(); }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public void untag(UUID uuid, Player player) {
        if (!combatEndTimes.containsKey(uuid)) return;
        combatEndTimes.remove(uuid);
        enemies.remove(uuid);
        cancelTask(uuid);
        hideBossBar(uuid, player);

        if (player != null && player.isOnline()) {
            player.setGlowing(false);
            if (plugin.getConfigManager().isRestoreFly()) {
                var data = plugin.getPlayerDataManager().get(uuid);
                if (data != null && data.hadFlight()) {
                    player.setAllowFlight(true);
                    data.setHadFlight(false);
                }
            }
            plugin.getMessageManager().send(player, "Out_Of_Combat");
            plugin.getMessageManager().send(player, "Out_Of_Combat_ActionBar"); // ← thêm dòng này
            runCommands(plugin.getConfigManager().getCommandsOnUntag(), player);
        }
    }

    public void forceUntag(UUID uuid, Player player) {
        combatEndTimes.remove(uuid);
        enemies.remove(uuid);
        cancelTask(uuid);
        hideBossBar(uuid, player);
        if (player != null) player.setGlowing(false);
    }

    private void cancelTask(UUID uuid) {
        BukkitTask t = tasks.remove(uuid);
        if (t != null) t.cancel();
    }

    private void showBossBar(Player player, int time) {
        if (!plugin.getConfigManager().isCombatBossBarEnabled()) return;
        UUID uuid = player.getUniqueId();
        hideBossBar(uuid, player);

        BossBar.Color color;
        try { color = BossBar.Color.valueOf(plugin.getConfigManager().getCombatBossBarColor()); }
        catch (Exception e) { color = BossBar.Color.RED; }

        BossBar bar = BossBar.bossBar(Component.empty(), 1.0f, color, BossBar.Overlay.PROGRESS);
        bossBars.put(uuid, bar);
        player.showBossBar(bar);
    }

    private void updateBossBar(Player player, int timeLeft) {
        if (!plugin.getConfigManager().isCombatBossBarEnabled()) return;
        BossBar bar = bossBars.get(player.getUniqueId());
        if (bar == null) return;
        int total = plugin.getConfigManager().getCombatTagTime();
        String msg = plugin.getConfigManager().getCombatBossBarMessage()
                .replace("<time>", String.valueOf(timeLeft))
                .replace("<enemy>", getCurrentEnemyName(player.getUniqueId()));
        bar.name(ColorUtil.toComponent(msg));
        bar.progress(Math.max(0f, Math.min(1f, (float) timeLeft / total)));
    }

    private void hideBossBar(UUID uuid, Player player) {
        BossBar bar = bossBars.remove(uuid);
        if (bar != null && player != null) player.hideBossBar(bar);
    }

    public boolean isTagged(UUID uuid) {
        Long end = combatEndTimes.get(uuid);
        if (end == null) return false;
        if (System.currentTimeMillis() > end) {
            untag(uuid, Bukkit.getPlayer(uuid));
            return false;
        }
        return true;
    }

    public int getTimeLeft(UUID uuid) {
        Long end = combatEndTimes.get(uuid);
        if (end == null) return 0;
        return (int) Math.max(0, (end - System.currentTimeMillis()) / 1000);
    }

    public Set<UUID> getEnemies(UUID uuid) {
        return enemies.getOrDefault(uuid, Collections.emptySet());
    }

    public String getCurrentEnemyName(UUID uuid) {
        Set<UUID> set = enemies.get(uuid);
        if (set == null || set.isEmpty()) return "Unknown";
        for (UUID eid : set) {
            Player p = Bukkit.getPlayer(eid);
            if (p != null) return p.getName();
        }
        return "Unknown";
    }

    private String getCurrentEnemyHealth(UUID uuid) {
        Set<UUID> set = enemies.get(uuid);
        if (set == null) return "?";
        for (UUID eid : set) {
            Player p = Bukkit.getPlayer(eid);
            if (p != null) return String.valueOf((int) Math.ceil(p.getHealth() / 2));
        }
        return "?";
    }

    public void removeEnemy(UUID player, UUID enemy) {
        Set<UUID> set = enemies.get(player);
        if (set == null) return;
        set.remove(enemy);
        if (set.isEmpty() && plugin.getConfigManager().isUntagOnKill()) {
            untag(player, Bukkit.getPlayer(player));
        }
    }

    public Collection<UUID> getTaggedPlayers() {
        return Collections.unmodifiableSet(combatEndTimes.keySet());
    }

    public boolean isInWorld(Player player) {
        return !plugin.getConfigManager().getWorldExclusions().contains(player.getWorld().getName());
    }

    private void runCommands(List<String> cmds, Player player) {
        for (String cmd : cmds) {
            if (cmd.isEmpty()) continue;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    cmd.replace("{player}", player.getName())
                       .replace("<player>", player.getName()));
        }
    }
}
