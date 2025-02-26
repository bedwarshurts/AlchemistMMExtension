package me.bedwarshurts.mmextension.mechanics.loop;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

@MythicMechanic(author = "bedwarshurts", name = "foreach", aliases = {}, description = "Execute a skill for each location target")
public class ForEachMechanic implements INoTargetSkill {
    private final String skillName;
    private final PlaceholderInt delayMs;

    public ForEachMechanic(MythicLineConfig mlc) {
        this.skillName = mlc.getString("skill", "");
        this.delayMs = PlaceholderInt.of(mlc.getString(new String[]{"interval","i"}, "0"));
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        Collection<AbstractLocation> locations = data.getLocationTargets();
        int delayInMs = delayMs.get(data);
        int i = 0;
        for (AbstractLocation loc : locations) {
            final int index = i++;
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()),
                    () -> SkillUtils.castSkillAtPoint(data, BukkitAdapter.adapt(loc), skillName),
                    (long) delayInMs * index / 50
            );
        }
        return SkillResult.SUCCESS;
    }
}