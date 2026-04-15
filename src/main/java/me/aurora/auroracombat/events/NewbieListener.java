package me.aurora.auroracombat.events;

import me.aurora.auroracombat.AuroraCombat;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class NewbieListener implements Listener {

    private final AuroraCombat plugin;

    public NewbieListener(AuroraCombat plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player defender)) return;
        Player attacker = resolve(event);
        if (attacker == null || attacker.equals(defender)) return;
        if (attacker.hasPermission("auroracombat.bypass.newbie")) return;

        boolean defProtected = plugin.getNewbieManager().isProtected(defender);
        boolean atkProtected = plugin.getNewbieManager().isProtected(attacker);

        if (defProtected || atkProtected) {
            event.setCancelled(true);
            if (defProtected)
                plugin.getMessageManager().send(attacker, "Newbie_Attack_Denied", "player", defender.getName());
            else
                plugin.getMessageManager().send(attacker, "Newbie_Protection_Active");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getNewbieManager().isProtected(player)) return;
        String cmd = event.getMessage().split(" ")[0].replace("/", "").toLowerCase();
        for (String blocked : plugin.getConfigManager().getNewbieCommandBlacklist()) {
            if (cmd.equalsIgnoreCase(blocked)) {
                event.setCancelled(true);
                plugin.getMessageManager().send(player, "Newbie_Command_Blocked");
                return;
            }
        }
    }

    private Player resolve(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p) return p;
        if (e.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player p) return p;
        return null;
    }
}
