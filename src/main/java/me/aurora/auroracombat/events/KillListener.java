package me.aurora.auroracombat.events;

import me.aurora.auroracombat.AuroraCombat;
import me.aurora.auroracombat.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KillListener implements Listener {

    private final AuroraCombat plugin;

    public KillListener(AuroraCombat plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null && !killer.equals(victim)) {
            plugin.getKillRewardManager().recordKill(killer, victim);

            if (plugin.getConfigManager().isDeathLightning())
                victim.getWorld().strikeLightningEffect(victim.getLocation());

            switch (plugin.getConfigManager().getPlayerDropMode()) {

                case "KEEP" -> {
                    event.setKeepInventory(true);
                    event.setKeepLevel(true);
                    event.getDrops().clear();
                    event.setDroppedExp(0);
                }

                case "CLEAR" -> {
                    event.getDrops().clear();
                    event.setDroppedExp(0);
                }

                case "TRANSFER" -> {
                    List<ItemStack> drops = new ArrayList<>(event.getDrops());
                    event.getDrops().clear();
                    event.setDroppedExp(0);
                    for (ItemStack item : drops) {
                        var leftover = killer.getInventory().addItem(item);
                        leftover.values().forEach(left ->
                            killer.getWorld().dropItemNaturally(killer.getLocation(), left)
                        );
                    }
                }
            }
        }

        if (plugin.getConfigManager().isAutoRespawn())
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (victim.isDead()) victim.spigot().respawn();
            }, 1L);

        plugin.getCombatManager().forceUntag(victim.getUniqueId(), null);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        int t = plugin.getConfigManager().getRespawnProtectionTime();
        if (t > 0) {
            PlayerData data = plugin.getPlayerDataManager().getOrCreate(player);
            data.setRespawnProtectionEnd(System.currentTimeMillis() + t * 1000L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline())
                    plugin.getMessageManager().send(player, "Respawn_Protected", "time", t);
            }, 1L);
        }
        for (String cmd : plugin.getConfigManager().getCommandsOnRespawn())
            if (!cmd.isEmpty())
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        cmd.replace("{player}", player.getName()));
    }
}
