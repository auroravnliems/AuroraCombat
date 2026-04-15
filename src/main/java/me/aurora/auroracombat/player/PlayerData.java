package me.aurora.auroracombat.player;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private final String name;

    private boolean pvpEnabled;
    private long pvpToggleCooldownEnd;
    private long pvpStateCooldownEnd;
    private boolean pvpGranted;
    private long pvpGrantEnd;

    private boolean newbieProtected;
    private long newbieProtectionEnd;

    private long respawnProtectionEnd;
    private long teleportProtectionEnd;
    private long deathCommandCooldownEnd;

    private boolean hadFlight;
    private boolean hadGodMode;
    private boolean firstJoin;

    public PlayerData(UUID uuid, String name, boolean defaultPvP, boolean firstJoin) {
        this.uuid = uuid;
        this.name = name;
        this.pvpEnabled = defaultPvP;
        this.firstJoin = firstJoin;
    }

    public UUID getUuid()         { return uuid; }
    public String getName()       { return name; }
    public boolean isFirstJoin()  { return firstJoin; }
    public void setFirstJoin(boolean b) { firstJoin = b; }

    public boolean isPvPEnabled()                 { return pvpEnabled; }
    public void setPvPEnabled(boolean b)          { pvpEnabled = b; }

    public long getPvPToggleCooldownEnd()          { return pvpToggleCooldownEnd; }
    public void setPvPToggleCooldownEnd(long t)    { pvpToggleCooldownEnd = t; }
    public boolean isPvPToggleOnCooldown()         { return System.currentTimeMillis() < pvpToggleCooldownEnd; }
    public int getPvPToggleCooldownLeft() {
        return (int) Math.max(0, (pvpToggleCooldownEnd - System.currentTimeMillis()) / 1000) + 1;
    }

    public void setPvPStateCooldownEnd(long t)     { pvpStateCooldownEnd = t; }
    public boolean isPvPStateOnCooldown()          { return System.currentTimeMillis() < pvpStateCooldownEnd; }
    public int getPvPStateCooldownLeft() {
        return (int) Math.max(0, (pvpStateCooldownEnd - System.currentTimeMillis()) / 1000) + 1;
    }

    public boolean isPvPGrantActive() {
        if (!pvpGranted) return false;
        if (System.currentTimeMillis() > pvpGrantEnd) { pvpGranted = false; return false; }
        return true;
    }
    public void setPvPGrant(long end) { pvpGranted = true; pvpGrantEnd = end; }
    public void clearPvPGrant()       { pvpGranted = false; pvpGrantEnd = 0; }
    public boolean isNewbieProtected() {
        if (!newbieProtected) return false;
        if (newbieProtectionEnd > 0 && System.currentTimeMillis() > newbieProtectionEnd) {
            newbieProtected = false;
            return false;
        }
        return true;
    }

    public boolean isNewbieProtectedRaw()          { return newbieProtected; }

    public void setNewbieProtected(boolean b)      { newbieProtected = b; }
    public long getNewbieProtectionEnd()           { return newbieProtectionEnd; }
    public void setNewbieProtectionEnd(long t)     { newbieProtectionEnd = t; }
    public int getNewbieTimeLeft() {
        if (!newbieProtected || newbieProtectionEnd <= 0) return 0;
        return (int) Math.max(0, (newbieProtectionEnd - System.currentTimeMillis()) / 1000);
    }

    public boolean hasRespawnProtection()          { return System.currentTimeMillis() < respawnProtectionEnd; }
    public void setRespawnProtectionEnd(long t)    { respawnProtectionEnd = t; }
    public boolean hasTeleportProtection()         { return System.currentTimeMillis() < teleportProtectionEnd; }
    public void setTeleportProtectionEnd(long t)   { teleportProtectionEnd = t; }
    public boolean isDeathCommandOnCooldown()      { return System.currentTimeMillis() < deathCommandCooldownEnd; }
    public void setDeathCommandCooldownEnd(long t) { deathCommandCooldownEnd = t; }

    public boolean hadFlight()            { return hadFlight; }
    public void setHadFlight(boolean b)   { hadFlight = b; }
    public boolean hadGodMode()           { return hadGodMode; }
    public void setHadGodMode(boolean b)  { hadGodMode = b; }
}
