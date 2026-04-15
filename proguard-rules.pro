# ============================================================
# ProGuard rules for AuroraCombat (Paper 1.21.1+ / Java 21)
# ============================================================

-verbose
-dontnote
-dontwarn **

# ── Preserve Main Plugin Class ───────────────────────────────
-keep public class me.aurora.auroracombat.AuroraCombat {
    public <init>();
    public void onEnable();
    public void onDisable();
    public void reload();
    public static me.aurora.auroracombat.AuroraCombat getInstance();
}

# ── Event Listeners ──────────────────────────────────────────
-keep @org.bukkit.event.EventHandler class * {
    @org.bukkit.event.EventHandler <methods>;
}
-keepclassmembers class * implements org.bukkit.event.Listener {
    @org.bukkit.event.EventHandler public void *(...);
}

# ── Commands ─────────────────────────────────────────────────
-keepclassmembers class * implements org.bukkit.command.CommandExecutor {
    public boolean onCommand(...);
}
-keepclassmembers class * implements org.bukkit.command.TabCompleter {
    public java.util.List onTabComplete(...);
}

# ── Keep public API trong package ────────────────────────────
-keepclassmembers class me.aurora.auroracombat.** {
    public <methods>;
    public <fields>;
}

# ── PlayerData ───────────────────────────────────────────────
-keepclassmembers class me.aurora.auroracombat.player.PlayerData {
    private <fields>;
    public <methods>;
}

# ── SQLite JDBC (shaded) ─────────────────────────────────────
-keep class me.aurora.auroracombat.libs.sqlite.** { *; }
-keep class me.aurora.auroracombat.libs.xerial.** { *; }
-keepclassmembers class * implements java.sql.Driver { *; }


-keepclassmembers class oshi.** extends com.sun.jna.Structure {
    <fields>;
    <methods>;
}
-keepclassmembers interface oshi.** extends com.sun.jna.Library {
    <methods>;
}
-keeppackagenames oshi

# ── JNA (provided by server) ─────────────────────────────────
-dontwarn com.sun.jna.**

# ── Native methods — giữ nguyên tên ─────────────────────────
-keepclasseswithmembernames class * {
    native <methods>;
}

# ── Adventure / MiniMessage ──────────────────────────────────
-dontwarn net.kyori.**
-keep class net.kyori.** { *; }

# ── Bukkit / Paper (provided by server) ──────────────────────
-dontwarn org.bukkit.**
-dontwarn io.papermc.**
-dontwarn com.destroystokyo.**
-dontwarn org.spigotmc.**
-dontwarn com.sk89q.**
-dontwarn org.slf4j.**

# ── Obfuscation Dictionary ───────────────────────────────────
-obfuscationdictionary      proguard-dict.txt
-classobfuscationdictionary proguard-dict.txt
-packageobfuscationdictionary proguard-dict.txt

-flattenpackagehierarchy 'me.aurora.ac'

# ── Optimization ─────────────────────────────────────────────
-optimizationpasses 1
-allowaccessmodification
-optimizations !code/simplification/*,!code/allocation/variable,!field/removal/writeonly,!class/merging/*,!code/removal/variable

# ── Keep Attributes ──────────────────────────────────────────
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes Exceptions

-renamesourcefileattribute AuroraCombat