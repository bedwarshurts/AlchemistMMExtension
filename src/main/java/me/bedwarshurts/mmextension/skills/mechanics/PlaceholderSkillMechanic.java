package me.bedwarshurts.mmextension.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import me.bedwarshurts.mmextension.utils.PlaceholderUtils;

@MythicMechanic(author = "bedwarshurts", name = "placeholderskill", aliases = {"skillp"}, description = "Parses all placeholders of an argument line, then casts the skill")
public class PlaceholderSkillMechanic implements ITargetedEntitySkill {

    private final String skillLine;

    public PlaceholderSkillMechanic(MythicLineConfig mlc) {
        this.skillLine = mlc.getString(new String[]{"skillline", "skill", "s"}, "");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        String parsedSkillLine = target != null ? PlaceholderUtils.parseStringPlaceholders(skillLine, data, target) :
                PlaceholderUtils.parseStringPlaceholders(skillLine, data);

        MythicSkill skill = new MythicSkill(parsedSkillLine);
        skill.cast(data);

        return SkillResult.SUCCESS;
    }
}
