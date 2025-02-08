package me.bedwarshurts.mmextension;

import me.bedwarshurts.mmextension.comp.MythicMobsHook;
import me.bedwarshurts.mmextension.comp.PlaceholderAPIHook;
import me.bedwarshurts.mmextension.listeners.EntityDamageListener;
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
        AlchemistMMExtension = this;

        Bukkit.getPluginManager().registerEvents(new MythicMobsHook(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new TnTExplosionListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerSkillCastListener(), this);

        new PlaceholderAPIHook().register();

        getLogger().info("AlchemistMMExtension has been enabled! Made by bedwarshurts");
    }

    @Override
    public void onDisable() {
        getLogger().info("AlchemistMMExtension has been disabled!");
    }
}
