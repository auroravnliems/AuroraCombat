package me.aurora.auroracombat.manager;

import me.aurora.auroracombat.AuroraCombat;
import me.aurora.auroracombat.player.PlayerData;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    private final AuroraCombat plugin;
    private final ConcurrentHashMap<UUID, PlayerData> cache = new ConcurrentHashMap<>();
    private Connection connection;

    public PlayerDataManager(AuroraCombat plugin) {
        this.plugin = plugin;
        initDatabase();
    }

    private void initDatabase() {
        try {
            File dir = plugin.getDataFolder();
            if (!dir.exists()) dir.mkdirs();
            File dbFile = new File(dir, "playerdata.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS players (
                        uuid        TEXT PRIMARY KEY,
                        name        TEXT,
                        pvp_enabled INTEGER DEFAULT 1,
                        newbie      INTEGER DEFAULT 0,
                        newbie_end  INTEGER DEFAULT 0,
                        first_join  INTEGER DEFAULT 1
                    )
                """);
            }
            plugin.getLogger().info("Database initialised.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Database init failed: " + e.getMessage());
        }
    }

    public PlayerData getOrCreate(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData existing = cache.get(uuid);
        if (existing != null) return existing;
        PlayerData loaded = loadOrCreate(player);
        PlayerData previous = cache.putIfAbsent(uuid, loaded);
        return previous != null ? previous : loaded;
    }

    public PlayerData get(UUID uuid) {
        return cache.get(uuid);
    }

    public void invalidate(UUID uuid) {
        PlayerData data = cache.remove(uuid);
        if (data != null) save(data);
    }

    public void saveAll() {
        cache.values().forEach(this::save);
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException ignored) {}
    }

    private PlayerData loadOrCreate(Player player) {
        UUID uuid = player.getUniqueId();
        boolean defaultPvP = plugin.getConfigManager().isDefaultPvPOn();

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM players WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                PlayerData data = new PlayerData(
                        uuid,
                        player.getName(),
                        rs.getInt("pvp_enabled") == 1,
                        false
                );

                long newbieEnd = rs.getLong("newbie_end");
                boolean newbieFlag = rs.getInt("newbie") == 1;

                boolean stillProtected = newbieFlag
                        && (newbieEnd <= 0 || System.currentTimeMillis() < newbieEnd);

                data.setNewbieProtected(stillProtected);
                data.setNewbieProtectionEnd(newbieEnd);

                if (newbieFlag && !stillProtected) {
                    clearNewbieInDB(uuid);
                }

                if (!player.getName().equals(rs.getString("name"))) {
                    updateName(uuid, player.getName());
                }
                return data;
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to load player data: " + e.getMessage());
        }

        PlayerData data = new PlayerData(uuid, player.getName(), defaultPvP, true);

        if (plugin.getConfigManager().isNewbieProtectionEnabled()) {
            long duration = plugin.getConfigManager().getNewbieProtectionTime() * 1000L;
            data.setNewbieProtected(true);
            data.setNewbieProtectionEnd(System.currentTimeMillis() + duration);
        }

        insertPlayer(data);
        return data;
    }

    private void insertPlayer(PlayerData data) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT OR IGNORE INTO players (uuid, name, pvp_enabled, newbie, newbie_end, first_join) " +
                        "VALUES (?,?,?,?,?,?)")) {
            ps.setString(1, data.getUuid().toString());
            ps.setString(2, data.getName());
            ps.setInt(3, data.isPvPEnabled() ? 1 : 0);
            ps.setInt(4, data.isNewbieProtectedRaw() ? 1 : 0);
            ps.setLong(5, data.getNewbieProtectionEnd());
            ps.setInt(6, 1);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to insert player: " + e.getMessage());
        }
    }

    private void save(PlayerData data) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT OR REPLACE INTO players (uuid, name, pvp_enabled, newbie, newbie_end, first_join) " +
                        "VALUES (?,?,?,?,?,0)")) {
            ps.setString(1, data.getUuid().toString());
            ps.setString(2, data.getName());
            ps.setInt(3, data.isPvPEnabled() ? 1 : 0);
            ps.setInt(4, data.isNewbieProtectedRaw() ? 1 : 0);
            ps.setLong(5, data.getNewbieProtectionEnd());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to save player: " + e.getMessage());
        }
    }

    private void clearNewbieInDB(UUID uuid) {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE players SET newbie = 0, newbie_end = 0 WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    private void updateName(UUID uuid, String name) {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE players SET name = ? WHERE uuid = ?")) {
            ps.setString(1, name);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }
}