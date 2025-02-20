package me.bedwarshurts.mmextension.mechanics.loop;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Optional;

@MythicMechanic(author = "bedwarshurts", name = "foreach", aliases = {}, description = "Execute a skill for each location target")
public class ForEachMechanic implements INoTargetSkill {
    private final String skillName;
    private final SkillExecutor manager;
    private final PlaceholderInt delayMs;

    public ForEachMechanic(SkillExecutor manager, MythicLineConfig mlc) {
        this.manager = manager;
        this.skillName = mlc.getString("skill", "");
        this.delayMs = PlaceholderInt.of(mlc.getString(new String[]{"interval","i"}, "0"));
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        if (skillName.isEmpty()) {
            return SkillResult.INVALID_CONFIG;
        }
        Optional<Skill> skillOptional = manager.getSkill(skillName);
        if (skillOptional.isEmpty()) {
            return SkillResult.INVALID_CONFIG;
        }

        Collection<AbstractLocation> locations = data.getLocationTargets();
        Skill skillToExecute = skillOptional.get();
        int delayInMs = delayMs.get(data);
        int i = 0;
        for (AbstractLocation loc : locations) {
            final int index = i++;
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()),
                    () -> skillToExecute.execute(data.deepClone().setLocationTarget(loc)),
                    (long) delayInMs * index / 50
            );
        }
        return SkillResult.SUCCESS;
    }
}