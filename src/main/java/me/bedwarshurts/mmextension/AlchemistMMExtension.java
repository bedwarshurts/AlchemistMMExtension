package me.bedwarshurts.mmextension;

import me.bedwarshurts.mmextension.commands.PlayerSpawnMythicMobCommand;
import me.bedwarshurts.mmextension.comp.MythicMobsHook;
import me.bedwarshurts.mmextension.comp.PlaceholderAPIHook;
import me.bedwarshurts.mmextension.listeners.EntityDamageListener;
import me.bedwarshurts.mmextension.listeners.PlayerChangeSlotListener;
import me.bedwarshurts.mmextension.listeners.PlayerSkillCastListener;
import me.bedwarshurts.mmextension.utils.exceptions.DependencyNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mariuszgromada.math.mxparser.License;

import java.util.Objects;

public class AlchemistMMExtension extends JavaPlugin {
    private boolean isMMOItems = true;
    private boolean isMythicLib = true;
    private boolean isMMOCore = true;
    private boolean isPlaceholderAPI = true;
    private boolean isProtocolLib = true;

    private static AlchemistMMExtension plugin;

    static {
        License.iConfirmCommercialUse("bedwarshurts@alchemistnetwork.org");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onEnable() {
        plugin = this;

        if (Bukkit.getPluginManager().getPlugin("MythicMobs") == null) {
            throw new DependencyNotFoundException("xd? you seriously installed an extension for mythicmobs without mythicmobs");
        }

        if (Bukkit.getPluginManager().getPlugin("MMOItems") == null) {
            this.isMMOItems = false;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            this.isPlaceholderAPI = false;
        }

        if (Bukkit.getPluginManager().getPlugin("MythicLib") == null) {
            this.isMythicLib = false;
        }

        if (Bukkit.getPluginManager().getPlugin("MMOCore") == null) {
            this.isMMOCore = false;
        }

        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            this.isProtocolLib = false;
        }

        Bukkit.getPluginManager().registerEvents(new MythicMobsHook(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), this);
        if (isMMOCore && isMythicLib) Bukkit.getPluginManager().registerEvents(new PlayerSkillCastListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChangeSlotListener(), this);

        Objects.requireNonNull(this.getCommand("playerspawnmythicmob")).setExecutor(new PlayerSpawnMythicMobCommand());

        if (isPlaceholderAPI) new PlaceholderAPIHook().register();

        getLogger().info("AlchemistMMExtension has been enabled! Made by bedwarshurts");
        getLogger().info("Version: " + plugin.getDescription());
    }

    @Override
    public void onDisable() {
        getLogger().info("AlchemistMMExtension has been disabled!");
    }

    public boolean isMMOItems() {
        return isMMOItems;
    }

    public boolean isProtocolLib() {
        return isProtocolLib;
    }

    public boolean isMMOCore() {
        return isProtocolLib;
    }

    public static AlchemistMMExtension inst() {
        return plugin;
    }
}
