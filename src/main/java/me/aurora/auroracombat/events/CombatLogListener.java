package me.aurora.auroracombat.events;

import me.aurora.auroracombat.AuroraCombat;
import me.aurora.auroracombat.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CombatLogListener implements Listener {

    private final AuroraCombat plugin;

    public CombatLogListener(AuroraCombat plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) { handleLeave(event.getPlayer()); }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH)
    public void onKick(PlayerKickEvent event) {
        if (!plugin.getConfigManager().isPunishOnKickEnabled()) return;
        if (plugin.getConfigManager().isMatchKickReason()) {
            String reason = event.getReason();
            boolean match = plugin.getConfigManager().getKickReasons().stream()
                    .anyMatch(r -> reason != null && reason.contains(r));
            if (!match) return;
        }
        handleLeave(event.getPlayer());
    }

    private void handleLeave(Player player) {
        if (!plugin.getCombatManager().isTagged(player.getUniqueId())) return;

        String broadcast = plugin.getMessageManager().format("Combat_Log_Broadcast",
                "player", player.getName());
        Bukkit.broadcast(ColorUtil.toComponent(broadcast));

        for (String cmd : plugin.getConfigManager().getCombatLogCommands()) {
            if (cmd.isEmpty()) continue;
            String fmt = cmd.replace("<player>", player.getName())
                    .replace("{player}", player.getName());
            if (fmt.startsWith("broadcast "))
                Bukkit.broadcast(ColorUtil.toComponent(fmt.substring(10)));
            else
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), fmt);
        }

        for (java.util.UUID enemyUuid : plugin.getCombatManager().getEnemies(player.getUniqueId())) {
            Player enemy = Bukkit.getPlayer(enemyUuid);
            plugin.getCombatManager().removeEnemy(enemyUuid, player.getUniqueId());
            if (plugin.getCombatManager().getEnemies(enemyUuid).isEmpty()) {
                plugin.getCombatManager().untag(enemyUuid, enemy);
                if (enemy != null && enemy.isOnline())
                    plugin.getMessageManager().send(enemy, "Out_Of_Combat");
            }
        }

        if (!plugin.getConfigManager().isKillOnLogoutEnabled()) {
            plugin.getCombatManager().forceUntag(player.getUniqueId(), null);
            return;
        }

        if (plugin.getConfigManager().isKillOnLogoutDropInventory()) {
            List<ItemStack> toDrop = new ArrayList<>();
            for (ItemStack item : player.getInventory().getStorageContents()) {
                if (item != null && item.getType() != Material.AIR)
                    toDrop.add(item.clone());
            }
            for (ItemStack armor : player.getInventory().getArmorContents()) {
                if (armor != null && armor.getType() != Material.AIR)
                    toDrop.add(armor.clone());
            }
            ItemStack offhand = player.getInventory().getItemInOffHand();
            if (offhand.getType() != Material.AIR)
                toDrop.add(offhand.clone());

            player.getInventory().clear();
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
            player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));

            for (ItemStack item : toDrop)
                player.getWorld().dropItemNaturally(player.getLocation(), item);
        }

        if (plugin.getConfigManager().isKillOnLogoutDropExp()) {
            int exp = player.getTotalExperience();
            if (exp > 0) {
                player.getWorld().spawn(player.getLocation(), ExperienceOrb.class).setExperience(exp);
                player.setTotalExperience(0);
                player.setLevel(0);
                player.setExp(0f);
            }
        }

        plugin.getCombatManager().forceUntag(player.getUniqueId(), null);
    }
}