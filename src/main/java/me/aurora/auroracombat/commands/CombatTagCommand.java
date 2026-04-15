package me.aurora.auroracombat.commands;

import me.aurora.auroracombat.AuroraCombat;
import me.aurora.auroracombat.util.ColorUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class CombatTagCommand implements CommandExecutor {
    private final AuroraCombat plugin;
    public CombatTagCommand(AuroraCombat plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Players only."); return true; }
        if (!player.hasPermission("auroracombat.combattag")) { plugin.getMessageManager().send(player, "No_Permission"); return true; }
        boolean tagged = plugin.getCombatManager().isTagged(player.getUniqueId());
        if (tagged) {
            int left = plugin.getCombatManager().getTimeLeft(player.getUniqueId());
            String enemy = plugin.getCombatManager().getCurrentEnemyName(player.getUniqueId());
            player.sendMessage(ColorUtil.toComponent(plugin.getMessageManager().getPrefix() + " &7In combat &c" + left + "s &7vs &f" + enemy));
        } else {
            player.sendMessage(ColorUtil.toComponent(plugin.getMessageManager().getPrefix() + " &aYou are not in combat."));
        }
        return true;
    }
}
