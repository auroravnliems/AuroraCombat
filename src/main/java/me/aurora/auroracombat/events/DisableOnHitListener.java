package me.aurora.auroracombat.events;

import me.aurora.auroracombat.AuroraCombat;
import me.aurora.auroracombat.player.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public class DisableOnHitListener implements Listener {
    private final AuroraCombat plugin;
    public DisableOnHitListener(AuroraCombat plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player defender)) return;
        Player attacker = resolve(event);
        if (attacker == null || attacker.equals(defender)) return;
        handle(defender);
        handle(attacker);
    }

    private void handle(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getOrCreate(player);
        if (plugin.getConfigManager().isDisableFlyOnHit() && player.isFlying()) {
            data.setHadFlight(player.getAllowFlight());
            player.setAllowFlight(false);
            player.setFlying(false);
            plugin.getMessageManager().send(player, "Fly_Disabled");
        }
        if (plugin.getConfigManager().isDisableGameModeOnHit()) {
            if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                player.setGameMode(GameMode.SURVIVAL);
                plugin.getMessageManager().send(player, "GameMode_Disabled");
            }
        }
        if (plugin.getConfigManager().isDisableInvisibilityOnHit())
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    private Player resolve(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p) return p;
        if (e.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player p) return p;
        return null;
    }
}
