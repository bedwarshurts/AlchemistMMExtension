package me.bedwarshurts.mmextension.utils;

import lombok.Getter;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigUtils {

    @Getter private static FileConfiguration config;

    public static void load() {
        AlchemistMMExtension.inst().saveDefaultConfig();

        config = AlchemistMMExtension.inst().getConfig();
    }
}
