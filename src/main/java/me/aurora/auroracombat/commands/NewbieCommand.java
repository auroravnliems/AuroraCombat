package me.aurora.auroracombat.commands;

import me.aurora.auroracombat.AuroraCombat;
import me.aurora.auroracombat.newbie.NewbieManager;
import me.aurora.auroracombat.player.PlayerData;
import me.aurora.auroracombat.util.ColorUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class NewbieCommand implements CommandExecutor {
    private final AuroraCombat plugin;
    public NewbieCommand(AuroraCombat plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Players only."); return true; }
        if (!player.hasPermission("auroracombat.newbie")) { plugin.getMessageManager().send(player, "No_Permission"); return true; }

        PlayerData data = plugin.getPlayerDataManager().getOrCreate(player);
        if (args.length > 0 && args[0].equalsIgnoreCase("disable")) {
            if (!plugin.getConfigManager().isNewbieAllowDisable()) { plugin.getMessageManager().send(player, "Newbie_Cannot_Disable"); return true; }
            if (!data.isNewbieProtected()) { plugin.getMessageManager().send(player, "Newbie_Not_Protected"); return true; }
            plugin.getNewbieManager().disableProtection(player);
        } else {
            if (data.isNewbieProtected()) {
                String time = NewbieManager.formatTime(data.getNewbieTimeLeft());
                player.sendMessage(ColorUtil.toComponent(plugin.getMessageManager().getPrefix() + " &aProtected for &e" + time + "&a. Use &f/newbie disable &ato remove."));
            } else {
                plugin.getMessageManager().send(player, "Newbie_Not_Protected");
            }
        }
        return true;
    }
}
