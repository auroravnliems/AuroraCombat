package me.aurora.auroracombat.antiborder;

import me.aurora.auroracombat.AuroraCombat;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BorderManager {
    private final AuroraCombat plugin;
    private final Map<UUID, Long> lastPushback = new ConcurrentHashMap<>();
    private boolean worldGuardPresent = false;

    public BorderManager(AuroraCombat plugin) {
        this.plugin = plugin;
        worldGuardPresent = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
        plugin.getLogger().info("WorldGuard: " + (worldGuardPresent ? "found" : "not found") + " — Anti Border Hopping " + (worldGuardPresent ? "fully enabled" : "limited"));
    }

    public void reload() {}

    public void handleBorderAttempt(Player player) {
        if (player.hasPermission("auroracombat.bypass.border")) return;
        if (!plugin.getCombatManager().isTagged(player.getUniqueId())) return;
        if (plugin.getConfigManager().isBarrierEnabled()) showBarrier(player);
        if (plugin.getConfigManager().isPushBackEnabled()) pushBack(player);
    }

    public boolean shouldBlockEnderPearl(Player player, Location dest) {
        if (!plugin.getConfigManager().isPushBackBlockEnderPearl()) return false;
        if (!plugin.getCombatManager().isTagged(player.getUniqueId())) return false;
        if (player.hasPermission("auroracombat.bypass.border")) return false;
        return isInSafeZone(dest);
    }

    public void pushBack(Player player) {
        long now = System.currentTimeMillis();
        if (lastPushback.getOrDefault(player.getUniqueId(), 0L) + 300 > now) return;
        lastPushback.put(player.getUniqueId(), now);
        Vector dir = player.getLocation().getDirection().clone().multiply(-1).normalize();
        dir.setY(0.25);
        dir.multiply(plugin.getConfigManager().getPushBackForce());
        player.setVelocity(dir);
        plugin.getMessageManager().send(player, "PushBack_Message");
    }

    private void showBarrier(Player player) {
        Material mat;
        try { mat = Material.valueOf(plugin.getConfigManager().getBarrierMaterial()); }
        catch (Exception e) { mat = Material.RED_STAINED_GLASS; }
        final Material finalMat = mat;
        final Location loc = player.getLocation().clone();
        List<Location> fakeBlocks = new ArrayList<>();
        Vector facing = player.getLocation().getDirection().normalize();
        for (int y = 0; y <= 2; y++) {
            for (int side = -2; side <= 2; side++) {
                Vector perp = new Vector(-facing.getZ(), 0, facing.getX()).normalize();
                Location bl = loc.clone().add(facing.clone().multiply(1.5)).add(perp.multiply(side)).add(0, y - 1, 0);
                bl = bl.getBlock().getLocation();
                if (bl.getBlock().getType() == Material.AIR) {
                    player.sendBlockChange(bl, finalMat.createBlockData());
                    fakeBlocks.add(bl);
                }
            }
        }
        new BukkitRunnable() {
            @Override public void run() {
                if (!player.isOnline()) return;
                for (Location b : fakeBlocks) player.sendBlockChange(b, b.getBlock().getBlockData());
            }
        }.runTaskLater(plugin, 20L);
    }

    public boolean isInSafeZone(Location location) {
        if (!worldGuardPresent) return false;
        return WorldGuardChecker.isSafeZone(location);
    }
}
