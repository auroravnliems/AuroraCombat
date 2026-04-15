package me.aurora.auroracombat.config;

import me.aurora.auroracombat.AuroraCombat;
import me.aurora.auroracombat.util.ColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;

public class MessageManager {

    private final AuroraCombat plugin;
    private final Properties messages = new Properties();
    private String rawPrefix;

    public MessageManager(AuroraCombat plugin) {
        this.plugin = plugin;
        load();
    }

    public void reload() {
        messages.clear();
        load();
    }

    private void load() {
        File file = new File(plugin.getDataFolder(), "messages.properties");
        if (!file.exists()) plugin.saveResource("messages.properties", false);
        try (InputStream in = new FileInputStream(file)) {
            messages.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load messages.properties: " + e.getMessage());
        }
        rawPrefix = messages.getProperty("Prefix", "&6[AuroraCombat]");
    }

    public String getRaw(String key) {
        return messages.getProperty(key, "&c[Missing: " + key + "]");
    }

    public String format(String key, Object... replacements) {
        String msg = getRaw(key);
        msg = msg.replace("{prefix}", rawPrefix).replace("<prefix>", rawPrefix);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            String k = String.valueOf(replacements[i]);
            String v = String.valueOf(replacements[i + 1]);
            msg = msg.replace("{" + k + "}", v).replace("<" + k + ">", v);
        }
        return msg;
    }

    public void send(Player player, String key, Object... replacements) {
        String msg = format(key, replacements);
        if (msg.isEmpty()) return;
        dispatch(player, msg);
    }

    public void sendRaw(Player player, String raw) {
        if (raw == null || raw.isEmpty()) return;
        String msg = raw.replace("{prefix}", rawPrefix);
        dispatch(player, msg);
    }

    private void dispatch(Player player, String msg) {
        if (msg.startsWith("!actionbar ")) {
            String text = msg.substring("!actionbar ".length());
            sendActionBar(player, text);
        } else {
            player.sendMessage(ColorUtil.toComponent(msg));
        }
    }

    public void sendActionBar(Player player, String message) {
        player.sendActionBar(ColorUtil.toComponent(message));
    }

    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.showTitle(Title.title(
                ColorUtil.toComponent(title),
                ColorUtil.toComponent(subtitle),
                Title.Times.times(
                        Duration.ofMillis(fadeIn * 50L),
                        Duration.ofMillis(stay * 50L),
                        Duration.ofMillis(fadeOut * 50L)
                )
        ));
    }

    public String getPrefix() { return rawPrefix; }

    public Component getPrefixComponent() { return ColorUtil.toComponent(rawPrefix); }

    public void debug(String msg) {
        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("[DEBUG] " + msg);
        }
    }
}
