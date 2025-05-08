package me.bedwarshurts.mmextension.comp;

import lombok.Getter;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import org.bukkit.plugin.PluginManager;

import java.util.EnumSet;
import java.util.logging.Logger;

@Getter
public enum PluginHooks {
    MMOItems("MMOItems"),
    MMOCore("MMOCore"),
    PlaceholderAPI("PlaceholderAPI"),
    ProtocolLib("ProtocolLib");

    private final String pluginName;

    private static final EnumSet<PluginHooks> detectedHooks = EnumSet.noneOf(PluginHooks.class);

    PluginHooks(String pluginName) {
        this.pluginName = pluginName;
    }

    public static void detectAll() {
        detectedHooks.clear();
        PluginManager pm = AlchemistMMExtension.inst().getServer().getPluginManager();
        Logger log = AlchemistMMExtension.inst().getLogger();
        for (PluginHooks hook : values()) {
            if (pm.isPluginEnabled(hook.pluginName)) {
                detectedHooks.add(hook);
                log.info("Detected hook: " + hook.pluginName);
                continue;
            }
            log.info("Hook not found: " + hook.pluginName);
        }
    }

    public static boolean isInstalled(PluginHooks hook) {
        return detectedHooks.contains(hook);
    }

}
