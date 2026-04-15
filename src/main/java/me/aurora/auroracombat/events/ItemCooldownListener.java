package me.aurora.auroracombat.events;

import me.aurora.auroracombat.AuroraCombat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

public class ItemCooldownListener implements Listener {
    private final AuroraCombat plugin;
    public ItemCooldownListener(AuroraCombat plugin) { this.plugin = plugin; }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null) return;
        Player player = event.getPlayer();
        if (player.hasPermission("auroracombat.bypass.itemcooldown")) return;
        if (checkAndApply(player, event.getItem().getType())) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("auroracombat.bypass.itemcooldown")) return;
        if (checkAndApply(player, event.getItem().getType())) event.setCancelled(true);
    }

    private boolean checkAndApply(Player player, Material mat) {
        var cdm = plugin.getItemCooldownManager();
        if (cdm.hasCooldown(player, mat)) {
            plugin.getMessageManager().send(player, "Item_Cooldown", "time", cdm.getCooldownLeft(player, mat));
            return true;
        }
        int cd = cdm.getConfiguredCooldown(player, mat);
        if (cd > 0) cdm.setCooldown(player, mat, cd);
        return false;
    }
}
