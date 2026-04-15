package me.aurora.auroracombat.commands;

import me.aurora.auroracombat.AuroraCombat;
import me.aurora.auroracombat.player.PlayerData;
import me.aurora.auroracombat.util.ColorUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class PvPToggleCommand implements CommandExecutor, TabCompleter {
    private final AuroraCombat plugin;
    public PvPToggleCommand(AuroraCombat plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Players only."); return true; }
        if (!player.hasPermission("auroracombat.pvp")) { plugin.getMessageManager().send(player, "No_Permission"); return true; }

        if (args.length == 0) { plugin.getPvpToggleManager().togglePvP(player); return true; }

        switch (args[0].toLowerCase()) {
            case "on" -> {
                if (plugin.getPvpToggleManager().isPvPEnabled(player)) { plugin.getMessageManager().send(player, "PvP_Already_Enabled"); return true; }
                plugin.getPvpToggleManager().setPvP(player, true);
                plugin.getMessageManager().send(player, "PvP_Enabled");
            }
            case "off" -> {
                if (!plugin.getPvpToggleManager().isPvPEnabled(player)) { plugin.getMessageManager().send(player, "PvP_Already_Disabled"); return true; }
                plugin.getPvpToggleManager().setPvP(player, false);
                plugin.getMessageManager().send(player, "PvP_Disabled");
            }
            case "status" -> {
                boolean on = plugin.getPvpToggleManager().isPvPEnabled(player);
                player.sendMessage(ColorUtil.toComponent(plugin.getMessageManager().getPrefix() + " &7PvP: " + (on ? "&cEnabled" : "&aDisabled")));
            }
            default -> plugin.getPvpToggleManager().togglePvP(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        return args.length == 1 ? Arrays.asList("on", "off", "status") : List.of();
    }
}
