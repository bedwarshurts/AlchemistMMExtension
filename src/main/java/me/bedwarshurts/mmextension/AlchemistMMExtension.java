package me.bedwarshurts.mmextension;

import io.lumine.mythic.api.mobs.MythicMob;
import me.bedwarshurts.mmextension.comp.MythicMobsHook;
import me.bedwarshurts.mmextension.comp.PlaceholderAPIHook;
import me.bedwarshurts.mmextension.listeners.ChestGUIListener;
import me.bedwarshurts.mmextension.listeners.EntityDamageListener;
import me.bedwarshurts.mmextension.listeners.HotbarSnapshotListener;
import me.bedwarshurts.mmextension.listeners.OnSignalListener;
import me.bedwarshurts.mmextension.listeners.PlayerChangeSlotListener;
import me.bedwarshurts.mmextension.listeners.PlayerSkillCastListener;
import me.bedwarshurts.mmextension.listeners.TnTExplosionListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mariuszgromada.math.mxparser.License;

public class AlchemistMMExtension extends JavaPlugin {
    public static AlchemistMMExtension AlchemistMMExtension;

    static {
        License.iConfirmCommercialUse("bedwarshurts@alchemistnetwork.org");
    }

    @Override
    public void onEnable() {
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

        AlchemistMMExtension = this;

        Bukkit.getPluginManager().registerEvents(new MythicMobsHook(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new TnTExplosionListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerSkillCastListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChestGUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new OnSignalListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChangeSlotListener(), this);
        Bukkit.getPluginManager().registerEvents(new HotbarSnapshotListener(), this);

        new PlaceholderAPIHook().register();

        getLogger().info("AlchemistMMExtension has been enabled! Made by bedwarshurts");
    }

    @Override
    public void onDisable() {
        getLogger().info("AlchemistMMExtension has been disabled!");
    }
}
