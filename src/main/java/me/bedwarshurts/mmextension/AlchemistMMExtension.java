package me.bedwarshurts.mmextension;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import lombok.Getter;
import me.bedwarshurts.mmextension.commands.AMECommand;
import me.bedwarshurts.mmextension.commands.subcommands.PlayerSpawnMythicMobCommand;
import me.bedwarshurts.mmextension.comp.AlchemistSkillManager;
import me.bedwarshurts.mmextension.comp.MythicMobsHook;
import me.bedwarshurts.mmextension.comp.PlaceholderAPIHook;
import me.bedwarshurts.mmextension.comp.PluginHooks;
import me.bedwarshurts.mmextension.listeners.EntityDamageListener;
import me.bedwarshurts.mmextension.listeners.SkillTriggerListeners;
import me.bedwarshurts.mmextension.listeners.mmocore.SkillCastTriggerListener;
import me.bedwarshurts.mmextension.skills.triggers.MoreSkillTriggers;
import me.bedwarshurts.mmextension.utils.ConfigUtils;
import me.bedwarshurts.mmextension.utils.ReflectionUtils;
import me.bedwarshurts.mmextension.utils.exceptions.DependencyNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mariuszgromada.math.mxparser.License;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;

public class AlchemistMMExtension extends JavaPlugin {

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
        PluginHooks.detectAll();

        getLogger().info("Loading configuration...");
        ConfigUtils.load();

        getLogger().info("Registering mythic skills...");
        Bukkit.getPluginManager().registerEvents(new MythicMobsHook(), this);

        getLogger().info("Registering skill triggers...");
        MoreSkillTriggers.registerTriggers();

        getLogger().info("Registering events...");
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new SkillTriggerListeners(), this);
        if (PluginHooks.isInstalled(PluginHooks.MMOCore))
            Bukkit.getPluginManager().registerEvents(new SkillCastTriggerListener(), this);

        getLogger().info("Registering commands...");
        Objects.requireNonNull(this.getCommand("ame")).setExecutor(new AMECommand());
        AMECommand.registerSubcommand(new PlayerSpawnMythicMobCommand());

        getLogger().info("Registering placeholders...");
        try {
            Collection<Class<?>> placeholderClasses =
                    ReflectionUtils.getAnnotatedClasses("me.bedwarshurts.mmextension.skills.placeholders", MythicPlaceholder.class);
            placeholderClasses.forEach(clazz -> {
                MythicPlaceholder placeholder = clazz.getAnnotation(MythicPlaceholder.class);
                if (placeholder != null) {
                    try {
                        MythicBukkit.inst().getPlaceholderManager().register(placeholder.placeholder(), (Placeholder) clazz.getConstructor().newInstance());
                    } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                             IllegalAccessException e) {
                        throw new UnsupportedOperationException("Failed to register placeholder " + placeholder.placeholder(), e);
                    }
                }
            });
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Failed to register placeholders", e);
        }

        if (ConfigUtils.getConfig().getBoolean("VolatileSettings.OverrideMythicSkillExecutor")) {
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
            }, 1L);
        }

        Bukkit.getScheduler().runTaskLater(this, () -> {
            MythicBukkit.inst().getPackManager().loadPacks();
            MythicBukkit.inst().getMobManager().loadMobs();
            MythicBukkit.inst().getSkillManager().loadSkills();
        }, 2L);

        if (PluginHooks.isInstalled(PluginHooks.PlaceholderAPI)) {
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
