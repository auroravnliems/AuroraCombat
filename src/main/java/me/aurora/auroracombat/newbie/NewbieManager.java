package me.aurora.auroracombat.newbie;

import me.aurora.auroracombat.AuroraCombat;
import me.aurora.auroracombat.player.PlayerData;
import me.aurora.auroracombat.util.ColorUtil;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NewbieManager {

    private final AuroraCombat plugin;
    private final ConcurrentHashMap<UUID, BossBar> bossBars = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, BukkitTask> tasks = new ConcurrentHashMap<>();

    public NewbieManager(AuroraCombat plugin) { this.plugin = plugin; }
    public void reload() {}

    public void shutdown() {
        tasks.values().forEach(BukkitTask::cancel);
        tasks.clear();
        bossBars.clear();
    }

    public boolean isProtected(Player player) {
        if (!plugin.getConfigManager().isNewbieProtectionEnabled()) return false;
        return plugin.getPlayerDataManager().getOrCreate(player).isNewbieProtected();
    }

    public void startTracking(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getOrCreate(player);
        if (!data.isNewbieProtected()) return;
        showBossBar(player, data);
        scheduleTask(player);
    }

    public void stopTracking(UUID uuid) {
        BukkitTask t = tasks.remove(uuid);
        if (t != null) t.cancel();
        Player p = Bukkit.getPlayer(uuid);
        BossBar bar = bossBars.remove(uuid);
        if (bar != null && p != null) p.hideBossBar(bar);
    }

    public void disableProtection(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getOrCreate(player);
        data.setNewbieProtected(false);
        data.setNewbieProtectionEnd(0);
        stopTracking(player.getUniqueId());
        plugin.getMessageManager().send(player, "Newbie_Disabled");
    }

    private void showBossBar(Player player, PlayerData data) {
        if (!plugin.getConfigManager().isNewbieBossBarEnabled()) return;
        UUID uuid = player.getUniqueId();
        BossBar old = bossBars.remove(uuid);
        if (old != null) player.hideBossBar(old);

        BossBar.Color color;
        try { color = BossBar.Color.valueOf(plugin.getConfigManager().getNewbieBossBarColor()); }
        catch (Exception e) { color = BossBar.Color.GREEN; }

        BossBar bar = BossBar.bossBar(buildTitle(data), 1f, color, BossBar.Overlay.PROGRESS);
        bossBars.put(uuid, bar);
        player.showBossBar(bar);
    }

    private void updateBossBar(Player player, PlayerData data) {
        if (!plugin.getConfigManager().isNewbieBossBarEnabled()) return;
        BossBar bar = bossBars.get(player.getUniqueId());
        if (bar == null) return;
        int total = plugin.getConfigManager().getNewbieProtectionTime();
        int left = data.getNewbieTimeLeft();
        bar.name(buildTitle(data));
        bar.progress(Math.max(0f, Math.min(1f, (float) left / total)));
    }

    private net.kyori.adventure.text.Component buildTitle(PlayerData data) {
        String msg = plugin.getConfigManager().getNewbieBossBarMessage()
                .replace("<time>", formatTime(data.getNewbieTimeLeft()));
        return ColorUtil.toComponent(msg);
    }

    private void scheduleTask(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitTask old = tasks.remove(uuid);
        if (old != null) old.cancel();

        tasks.put(uuid, new BukkitRunnable() {
            @Override
            public void run() {
                Player p = Bukkit.getPlayer(uuid);
                if (p == null) { cancel(); return; }
                PlayerData data = plugin.getPlayerDataManager().get(uuid);
                if (data == null || !data.isNewbieProtected()) { stopTracking(uuid); cancel(); return; }
                if (data.getNewbieTimeLeft() <= 0) {
                    data.setNewbieProtected(false);
                    stopTracking(uuid);
                    plugin.getMessageManager().send(p, "Newbie_Expired");
                    cancel();
                    return;
                }
                updateBossBar(p, data);
            }
        }.runTaskTimer(plugin, 20L, 20L));
    }

    public static String formatTime(int totalSeconds) {
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return mins > 0 ? mins + "m " + secs + "s" : secs + "s";
    }
}
