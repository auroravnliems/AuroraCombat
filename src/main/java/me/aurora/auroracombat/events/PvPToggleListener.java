package me.aurora.auroracombat.events;

import me.aurora.auroracombat.AuroraCombat;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.projectiles.ProjectileSource;

public class PvPToggleListener implements Listener {

    private final AuroraCombat plugin;

    public PvPToggleListener(AuroraCombat plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player defender)) return;
        Player attacker = resolve(event);
        if (attacker == null || attacker.equals(defender)) return;
        if (attacker.hasPermission("auroracombat.bypass.pvptoggle")) return;
        if (!plugin.getPvpToggleManager().canAttack(attacker, defender)) {
            event.setCancelled(true);
            if (!plugin.getPvpToggleManager().isPvPEnabled(attacker)) {
                plugin.getMessageManager().send(attacker, "Attack_Denied_You");
            } else {
                plugin.getMessageManager().send(attacker, "Attack_Denied_Other", "player", defender.getName());
                showEffect(defender);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.getConfigManager().isWorldGuardOverrides()) return;
        Player player = event.getPlayer();
        if (plugin.getPvpToggleManager().isPvPEnabled(player)) return;
        if (event.getTo() == null) return;

        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;
        if (!plugin.getBorderManager().isInSafeZone(event.getTo())
                && isWorldGuardEnabled()) {
            plugin.getPvpToggleManager().setPvP(player, true);
            plugin.getMessageManager().send(player, "PvP_Force_Enabled_WorldGuard");
        }
    }

    private boolean isWorldGuardEnabled() {
        return org.bukkit.Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
    }

    private void showEffect(Player player) {
        if (!plugin.getConfigManager().isPvPProtectionEffectEnabled()) return;
        try {
            Particle p = Particle.valueOf(plugin.getConfigManager().getPvPProtectionParticle());
            player.getWorld().spawnParticle(p, player.getLocation().add(0, 1, 0), 25, 0.5, 0.5, 0.5, 0.05);
        } catch (Exception ignored) {}
    }

    private Player resolve(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p) return p;
        if (e.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player p) return p;
        return null;
    }
}