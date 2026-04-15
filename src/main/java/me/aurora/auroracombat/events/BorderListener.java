package me.aurora.auroracombat.events;

import me.aurora.auroracombat.AuroraCombat;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.projectiles.ProjectileSource;

public class BorderListener implements Listener {

    private final AuroraCombat plugin;

    public BorderListener(AuroraCombat plugin) { this.plugin = plugin; }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getCombatManager().isTagged(player.getUniqueId())) return;
        if (player.hasPermission("auroracombat.bypass.border")) return;
        Location to = event.getTo();
        if (to == null) return;
        Location from = event.getFrom();
        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) return;
        if (plugin.getBorderManager().isInSafeZone(to)) {
            plugin.getBorderManager().handleBorderAttempt(player);
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnderPearl(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl pearl)) return;
        if (!(pearl.getShooter() instanceof Player player)) return;
        if (!plugin.getCombatManager().isTagged(player.getUniqueId())) return;
        if (player.hasPermission("auroracombat.bypass.border")) return;
        if (plugin.getBorderManager().shouldBlockEnderPearl(player, pearl.getLocation())) {
            event.setCancelled(true);
            plugin.getMessageManager().send(player, "Border_EnderPearl_Blocked");
        }
    }
}