package me.aurora.auroracombat.events;

import me.aurora.auroracombat.AuroraCombat;
import me.aurora.auroracombat.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class PlayerSessionListener implements Listener {

    private final AuroraCombat plugin;

    public PlayerSessionListener(AuroraCombat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerData data = plugin.getPlayerDataManager().getOrCreate(player);
        if (data.isNewbieProtected()) {
            plugin.getNewbieManager().startTracking(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getNewbieManager().stopTracking(player.getUniqueId());
        plugin.getItemCooldownManager().clearAll(player.getUniqueId());
        plugin.getPlayerDataManager().invalidate(player.getUniqueId());
    }
}
