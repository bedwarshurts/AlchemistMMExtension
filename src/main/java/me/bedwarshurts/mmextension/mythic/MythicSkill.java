package me.bedwarshurts.mmextension.mythic;

import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillExecutor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Optional;

public class MythicSkill {
    private final Skill skill;

    public MythicSkill(String skillName) {
        this(skillName, MythicBukkit.inst().getSkillManager());
    }

    public MythicSkill(String skillName, SkillExecutor manager) {
        if (skillName.isEmpty()) {
            this.skill = null;
            return;
        }
        Optional<Skill> skillOptional = manager.getSkill(skillName);
        if (skillOptional.isEmpty()) {
            throw new NullPointerException("Skill not found");
        }

        this.skill = skillOptional.get();
    }

    public void cast(SkillMetadata data) {
        if (skill == null) return;

        skill.execute(data);
    }

    public void castAtPoint(SkillMetadata data, Location location) {
        if (skill == null) return;

        data.setLocationTarget(BukkitAdapter.adapt(location));
        skill.execute(data);
    }

    public void castAtEntity(SkillMetadata data, Entity entity) {
        if (skill == null) return;

        data.setEntityTarget(BukkitAdapter.adapt(entity));
        skill.execute(data);
    }
}
