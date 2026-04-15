package me.aurora.auroracombat.commands;

import me.aurora.auroracombat.AuroraCombat;
import me.aurora.auroracombat.newbie.NewbieManager;
import me.aurora.auroracombat.player.PlayerData;
import me.aurora.auroracombat.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class AcCommand implements CommandExecutor, TabCompleter {

    private final AuroraCombat plugin;
    private static final List<String> SUBS = List.of("reload","info","tag","untag","pvpgrant","check","pvpon","pvpoff");

    public AcCommand(AuroraCombat plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("auroracombat.admin")) {
            sender.sendMessage(ColorUtil.toComponent("&cNo permission."));
            return true;
        }
        if (args.length == 0) { sendHelp(sender); return true; }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.reload();
                sender.sendMessage(ColorUtil.toComponent(
                        plugin.getMessageManager().format("Reload_Success")));
            }
            case "info" -> {
                sender.sendMessage(ColorUtil.toComponent(
                        "&5&lAuroraCombat &ev" + plugin.getPluginMeta().getVersion()));
                sender.sendMessage(ColorUtil.toComponent(
                        "  &7Tagged: &f" + plugin.getCombatManager().getTaggedPlayers().size() + " players"));
            }
            case "tag" -> {
                if (args.length < 2) { sender.sendMessage("Usage: /ac tag <player>"); return true; }
                Player t = Bukkit.getPlayer(args[1]);
                if (t == null) { notFound(sender, args[1]); return true; }
                if (!(sender instanceof Player admin)) { sender.sendMessage("Must be a player."); return true; }
                plugin.getCombatManager().tag(admin, t);
                sender.sendMessage(ColorUtil.toComponent(
                        plugin.getMessageManager().getPrefix() + " &aTagged &f" + t.getName()));
            }
            case "untag" -> {
                if (args.length < 2) { sender.sendMessage("Usage: /ac untag <player>"); return true; }
                Player t = Bukkit.getPlayer(args[1]);
                if (t == null) { notFound(sender, args[1]); return true; }
                plugin.getCombatManager().forceUntag(t.getUniqueId(), t);
                sender.sendMessage(ColorUtil.toComponent(
                        plugin.getMessageManager().getPrefix() + " &aUntagged &f" + t.getName()));
            }
            case "pvpgrant" -> {
                if (args.length < 2) { sender.sendMessage("Usage: /ac pvpgrant <player> [seconds]"); return true; }
                Player t = Bukkit.getPlayer(args[1]);
                if (t == null) { notFound(sender, args[1]); return true; }
                int secs = args.length >= 3 ? parseInt(args[2], 60) : 60;
                plugin.getPvpToggleManager().grantPvP(t, secs);
                plugin.getMessageManager().send(t, "PvPGrant_Received");
                sender.sendMessage(ColorUtil.toComponent(
                        plugin.getMessageManager().getPrefix() + " &aGranted &f" + t.getName()
                                + " &aPvP protection for &e" + secs + "s"));
            }
            case "pvpon" -> {
                if (args.length < 2) { sender.sendMessage("Usage: /ac pvpon <player>"); return true; }
                Player t = Bukkit.getPlayer(args[1]);
                if (t == null) { notFound(sender, args[1]); return true; }
                plugin.getPvpToggleManager().setPvP(t, true);
                sender.sendMessage(ColorUtil.toComponent(
                        plugin.getMessageManager().getPrefix() + " &aEnabled PvP for &f" + t.getName()));
            }
            case "pvpoff" -> {
                if (args.length < 2) { sender.sendMessage("Usage: /ac pvpoff <player>"); return true; }
                Player t = Bukkit.getPlayer(args[1]);
                if (t == null) { notFound(sender, args[1]); return true; }
                plugin.getPvpToggleManager().setPvP(t, false);
                sender.sendMessage(ColorUtil.toComponent(
                        plugin.getMessageManager().getPrefix() + " &aDisabled PvP for &f" + t.getName()));
            }
            case "check" -> {
                if (args.length < 2) { sender.sendMessage("Usage: /ac check <player>"); return true; }
                Player t = Bukkit.getPlayer(args[1]);
                if (t == null) { notFound(sender, args[1]); return true; }
                PlayerData data = plugin.getPlayerDataManager().getOrCreate(t);
                boolean tagged = plugin.getCombatManager().isTagged(t.getUniqueId());
                sender.sendMessage(ColorUtil.toComponent("&5&lAuroraCombat &7— &f" + t.getName()));
                sender.sendMessage(ColorUtil.toComponent(
                        "  &7PvP: " + (data.isPvPEnabled() ? "&cON" : "&aOFF")));
                sender.sendMessage(ColorUtil.toComponent(
                        "  &7Combat: " + (tagged
                                ? "&cTagged (" + plugin.getCombatManager().getTimeLeft(t.getUniqueId()) + "s)"
                                : "&aClear")));
                sender.sendMessage(ColorUtil.toComponent(
                        "  &7Newbie: " + (data.isNewbieProtected()
                                ? "&aActive (" + NewbieManager.formatTime(data.getNewbieTimeLeft()) + ")"
                                : "&7None")));
            }
            default -> sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender s) {
        s.sendMessage(ColorUtil.toComponent("&5&lAuroraCombat &7Commands:"));
        s.sendMessage(ColorUtil.toComponent("  &e/ac reload &7- Reload configs"));
        s.sendMessage(ColorUtil.toComponent("  &e/ac info &7- Plugin stats"));
        s.sendMessage(ColorUtil.toComponent("  &e/ac tag/untag &8<player>"));
        s.sendMessage(ColorUtil.toComponent("  &e/ac pvpgrant &8<player> [secs]"));
        s.sendMessage(ColorUtil.toComponent("  &e/ac pvpon/pvpoff &8<player>"));
        s.sendMessage(ColorUtil.toComponent("  &e/ac check &8<player>"));
    }

    private void notFound(CommandSender s, String name) {
        s.sendMessage(ColorUtil.toComponent(
                plugin.getMessageManager().format("Player_Not_Found", "player", name)));
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (args.length == 1) return SUBS;
        if (args.length == 2) return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        return List.of();
    }
}