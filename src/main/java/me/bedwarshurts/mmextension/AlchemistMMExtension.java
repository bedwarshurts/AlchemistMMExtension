package me.bedwarshurts.mmextension;

import me.bedwarshurts.mmextension.commands.PlayerSpawnMythicMobCommand;
import me.bedwarshurts.mmextension.comp.MythicMobsHook;
import me.bedwarshurts.mmextension.comp.PlaceholderAPIHook;
import me.bedwarshurts.mmextension.listeners.EntityDamageListener;
import me.bedwarshurts.mmextension.listeners.PlayerChangeSlotListener;
import me.bedwarshurts.mmextension.listeners.PlayerSkillCastListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mariuszgromada.math.mxparser.License;

import java.util.Objects;

public class AlchemistMMExtension extends JavaPlugin {
    public static AlchemistMMExtension plugin;

    static {
        License.iConfirmCommercialUse("bedwarshurts@alchemistnetwork.org");
    }

    @Override
    public void onEnable() {
        plugin = this;

        if (Bukkit.getPluginManager().getPlugin("MythicMobs") == null) {
            getLogger().severe("MythicMobs is not installed! Disabling AlchemistMMExtension...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("MMOItems") == null) {
            getLogger().warning("MMOItems is not installed! Some features may not work.");
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("PlaceholderAPI is not installed! Placeholder features will not work.");
        }

        if (Bukkit.getPluginManager().getPlugin("MythicLib") == null) {
            getLogger().warning("MythicLib is not installed! Some features may not work.");
        }

        if (Bukkit.getPluginManager().getPlugin("MMOCore") == null) {
            getLogger().warning("MMOCore is not installed! Some features may not work.");
        }

        Bukkit.getPluginManager().registerEvents(new MythicMobsHook(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerSkillCastListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChangeSlotListener(), this);

        Objects.requireNonNull(this.getCommand("playerspawnmythicmob")).setExecutor(new PlayerSpawnMythicMobCommand());

        new PlaceholderAPIHook().register();

        getLogger().info("AlchemistMMExtension has been enabled! Made by bedwarshurts");
    }

    @Override
    public void onDisable() {
        getLogger().info("AlchemistMMExtension has been disabled!");
    }
}
