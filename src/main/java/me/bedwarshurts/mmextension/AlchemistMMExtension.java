package me.bedwarshurts.mmextension;

import io.lumine.mythic.bukkit.MythicBukkit;
import me.bedwarshurts.mmextension.commands.PlayerSpawnMythicMobCommand;
import me.bedwarshurts.mmextension.comp.MythicMobsHook;
import me.bedwarshurts.mmextension.comp.PlaceholderAPIHook;
import me.bedwarshurts.mmextension.listeners.EntityDamageListener;
import me.bedwarshurts.mmextension.listeners.SkillTriggerListeners;
import me.bedwarshurts.mmextension.listeners.mmocore.SkillCastTriggerListener;
import me.bedwarshurts.mmextension.skills.placeholders.TernaryPlaceholder;
import me.bedwarshurts.mmextension.skills.triggers.MoreSkillTriggers;
import me.bedwarshurts.mmextension.utils.exceptions.DependencyNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mariuszgromada.math.mxparser.License;

import java.util.Objects;

public class AlchemistMMExtension extends JavaPlugin {

    private boolean isMMOItems = true;
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

        getLogger().info("Checking for plugins to hook into");
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") == null) {
            throw new DependencyNotFoundException("xd? you seriously installed an extension for mythicmobs without mythicmobs");
        }

        if (Bukkit.getPluginManager().getPlugin("MMOItems") == null) {
            this.isMMOItems = false;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            this.isPlaceholderAPI = false;
        }

        if (Bukkit.getPluginManager().getPlugin("MMOCore") == null) {
            this.isMMOCore = false;
        }

        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            this.isProtocolLib = false;
        }

        getLogger().info("Registering mythic skills...");
        Bukkit.getPluginManager().registerEvents(new MythicMobsHook(), this);

        getLogger().info("Registering skill triggers...");
        MoreSkillTriggers.registerTriggers();
        MythicBukkit.inst().getMobManager().loadMobs(); // required for the skills related to the triggers to be registered

        getLogger().info("Registering events...");
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new SkillTriggerListeners(), this);
        if (isMMOCore) Bukkit.getPluginManager().registerEvents(new SkillCastTriggerListener(), this);

        getLogger().info("Registering commands...");
        Objects.requireNonNull(this.getCommand("playerspawnmythicmob")).setExecutor(new PlayerSpawnMythicMobCommand());

        getLogger().info("Registering placeholders...");
        MythicBukkit.inst().getPlaceholderManager().register("eval", new TernaryPlaceholder());

        if (isPlaceholderAPI) {
            getLogger().info("Registering PlaceholderAPI Placeholders...");
            new PlaceholderAPIHook().register();
        }

        getLogger().info("AlchemistMMExtension has been enabled! Made by bedwarshurts");
        getLogger().info("Version: " + plugin.getDescription().getVersion());
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
        return isMMOCore;
    }

    public static AlchemistMMExtension inst() {
        return plugin;
    }
}
