package me.aurora.auroracombat.events;

import me.aurora.auroracombat.AuroraCombat;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;

public class CombatListener implements Listener {

    private final AuroraCombat plugin;

    public CombatListener(AuroraCombat plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player defender)) return;
        Player attacker = resolveAttacker(event);
        if (attacker == null) return;
        if (!plugin.getConfigManager().isSelfTag() && attacker.equals(defender)) return;
        if (!plugin.getCombatManager().isInWorld(attacker)) return;
        if (plugin.getConfigManager().isIgnoreNoDamageHits() && event.getFinalDamage() <= 0) return;

        if (plugin.getConfigManager().isPvPBlood()) spawnBlood(defender);
        plugin.getCombatManager().tag(attacker, defender);

        if (plugin.getConfigManager().isMaceRenewsTag()) {
            ItemStack mainHand = attacker.getInventory().getItemInMainHand();
            if (mainHand.getType() == Material.MACE)
                plugin.getMessageManager().debug("Mace attack by " + attacker.getName() + " on " + defender.getName());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        if (!(event.getPotion().getShooter() instanceof Player attacker)) return;
        for (LivingEntity le : event.getAffectedEntities()) {
            if (!(le instanceof Player defender)) continue;
            if (!plugin.getConfigManager().isSelfTag() && attacker.equals(defender)) continue;
            if (!isHarmfulPotion(event.getPotion().getEffects())) continue;
            plugin.getCombatManager().tag(attacker, defender);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getCombatManager().isTagged(player.getUniqueId())) return;
        if (player.hasPermission("auroracombat.bypass.combattag")) return;
        if (!plugin.getConfigManager().isCommandFilterEnabled()) return;

        String cmd = event.getMessage().split(" ")[0].replace("/", "").toLowerCase();

        var data = plugin.getPlayerDataManager().get(player.getUniqueId());
        if (data != null && data.isDeathCommandOnCooldown()) {
            for (String blocked : plugin.getConfigManager().getCommandsBlockedAfterDeath()) {
                if (cmd.equalsIgnoreCase(blocked)) {
                    event.setCancelled(true);
                    plugin.getMessageManager().send(player, "Command_Denied_InCombat");
                    return;
                }
            }
        }

        boolean whitelist = plugin.getConfigManager().isCommandsWhitelist();
        boolean inList = plugin.getConfigManager().getCommandFilterList().stream()
                .anyMatch(c -> cmd.equalsIgnoreCase(c));

        if ((whitelist && !inList) || (!whitelist && inList)) {
            event.setCancelled(true);
            plugin.getMessageManager().send(player, "Command_Denied_InCombat");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getCombatManager().isTagged(player.getUniqueId())) return;
        if (player.hasPermission("auroracombat.bypass.combattag")) return;

        var cause = event.getCause();
        if (plugin.getConfigManager().isBlockTeleportInCombat()
                && (cause == PlayerTeleportEvent.TeleportCause.COMMAND
                || cause == PlayerTeleportEvent.TeleportCause.PLUGIN)) {
            event.setCancelled(true);
            plugin.getMessageManager().send(player, "Teleport_Blocked_InCombat");
            return;
        }
        if (plugin.getConfigManager().isBlockPortalInCombat()
                && (cause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL
                || cause == PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
            event.setCancelled(true);
            plugin.getMessageManager().send(player, "Portal_Blocked_InCombat");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getCombatManager().isTagged(player.getUniqueId())) return;
        if (plugin.getConfigManager().isBlockPlaceBlocksInCombat()) {
            event.setCancelled(true);
            plugin.getMessageManager().send(player, "Block_Place_Blocked_InCombat");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getCombatManager().isTagged(player.getUniqueId())) return;
        if (plugin.getConfigManager().isBlockBreakBlocksInCombat()) {
            event.setCancelled(true);
            plugin.getMessageManager().send(player, "Block_Break_Blocked_InCombat");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getCombatManager().isTagged(player.getUniqueId())) return;
        Material mat = event.getItem().getType();

        if (mat == Material.CHORUS_FRUIT && plugin.getConfigManager().isBlockChorusFruitInCombat()) {
            event.setCancelled(true);
            plugin.getMessageManager().send(player, "ChorusFruit_Blocked_InCombat");
            return;
        }
        if (plugin.getConfigManager().isBlockEatInCombat()) {
            event.setCancelled(true);
            plugin.getMessageManager().send(player, "Eating_Blocked_InCombat");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(org.bukkit.event.inventory.InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!plugin.getCombatManager().isTagged(player.getUniqueId())) return;
        if (plugin.getConfigManager().isBlockOpenInventoryInCombat()) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnderPearlThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl)) return;
        if (!(event.getEntity().getShooter() instanceof Player player)) return;
        if (!plugin.getCombatManager().isTagged(player.getUniqueId())) return;
        if (player.hasPermission("auroracombat.bypass.combattag")) return;
        if (plugin.getConfigManager().isBlockEnderPearlInCombat()) {
            event.setCancelled(true);
            plugin.getMessageManager().send(player, "EnderPearl_Blocked_InCombat");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRiptide(PlayerRiptideEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getCombatManager().isTagged(player.getUniqueId())) return;
        if (player.hasPermission("auroracombat.bypass.combattag")) return;
        if (plugin.getConfigManager().isBlockRiptideInCombat()) {
            player.setVelocity(new org.bukkit.util.Vector(0, 0, 0));
            plugin.getMessageManager().send(player, "Riptide_Blocked_InCombat");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onElytra(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!event.isGliding()) return;
        if (!plugin.getCombatManager().isTagged(player.getUniqueId())) return;
        if (player.hasPermission("auroracombat.bypass.combattag")) return;
        if (plugin.getConfigManager().isBlockElytraGliding()) {
            event.setCancelled(true);
            plugin.getMessageManager().send(player, "Elytra_Blocked_InCombat");
        }
    }

    private Player resolveAttacker(EntityDamageByEntityEvent event) {
        Entity d = event.getDamager();
        if (d instanceof Player p) return p;
        if (d instanceof Projectile proj && proj.getShooter() instanceof Player p) return p;
        return null;
    }

    private boolean isHarmfulPotion(java.util.Collection<PotionEffect> effects) {
        for (PotionEffect e : effects) {
            String name = e.getType().getKey().getKey().toUpperCase();
            if (plugin.getConfigManager().getHarmfulPotions().stream()
                    .anyMatch(h -> h.equalsIgnoreCase(name))) return true;
        }
        return false;
    }

    private void spawnBlood(Player player) {
        try {
            player.getWorld().spawnParticle(Particle.BLOCK,
                    player.getLocation().add(0, 1, 0),
                    12, 0.3, 0.3, 0.3,
                    Material.REDSTONE_BLOCK.createBlockData());
        } catch (Exception ignored) {}
    }
}