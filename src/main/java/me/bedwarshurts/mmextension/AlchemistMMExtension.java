package me.bedwarshurts.mmextension;

import io.lumine.mythic.bukkit.MythicBukkit;
import lombok.Getter;
import me.bedwarshurts.mmextension.commands.PlayerSpawnMythicMobCommand;
import me.bedwarshurts.mmextension.comp.AlchemistSkillManager;
import me.bedwarshurts.mmextension.comp.MythicMobsHook;
import me.bedwarshurts.mmextension.comp.PlaceholderAPIHook;
import me.bedwarshurts.mmextension.listeners.EntityDamageListener;
import me.bedwarshurts.mmextension.listeners.SkillTriggerListeners;
import me.bedwarshurts.mmextension.listeners.mmocore.SkillCastTriggerListener;
import me.bedwarshurts.mmextension.skills.placeholders.RandomColorPlaceholder;
import me.bedwarshurts.mmextension.skills.placeholders.TernaryPlaceholder;
import me.bedwarshurts.mmextension.skills.triggers.MoreSkillTriggers;
import me.bedwarshurts.mmextension.utils.exceptions.DependencyNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mariuszgromada.math.mxparser.License;

import java.lang.reflect.Field;
import java.util.Objects;

public class AlchemistMMExtension extends JavaPlugin {

    @Getter private boolean isMMOItems = true;
    @Getter private boolean isMMOCore = true;
    @Getter private boolean isPlaceholderAPI = true;
    @Getter private boolean isProtocolLib = true;
    @Getter private boolean isOverriden = false;

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

        getLogger().info("Registering events...");
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new SkillTriggerListeners(), this);
        if (isMMOCore) Bukkit.getPluginManager().registerEvents(new SkillCastTriggerListener(), this);

        getLogger().info("Registering commands...");
        Objects.requireNonNull(this.getCommand("playerspawnmythicmob")).setExecutor(new PlayerSpawnMythicMobCommand());

        getLogger().info("Registering placeholders...");
        MythicBukkit.inst().getPlaceholderManager().register("eval", new TernaryPlaceholder());
        MythicBukkit.inst().getPlaceholderManager().register("random.color", new RandomColorPlaceholder());

        Bukkit.getScheduler().runTaskLater(this, () -> {
            getLogger().info("Overriding Mythic's Skill Executor...");
            try {
                MythicBukkit mythicInst = MythicBukkit.inst();

                Field field = MythicBukkit.class.getDeclaredField("skillManager");
                field.setAccessible(true);

                field.set(mythicInst, new AlchemistSkillManager(mythicInst));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to override Mythic's Skill Executor", e);
            }
            isOverriden = true;
            MythicBukkit.inst().getPackManager().loadPacks();
            MythicBukkit.inst().getMobManager().loadMobs();
            MythicBukkit.inst().getSkillManager().loadSkills();
        }, 1L);

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

    public static AlchemistMMExtension inst() {
        return plugin;
    }
}
