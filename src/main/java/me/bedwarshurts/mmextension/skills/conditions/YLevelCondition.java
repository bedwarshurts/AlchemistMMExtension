package me.bedwarshurts.mmextension.skills.conditions;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;

@MythicCondition(author = "bedwarshurts", name = "isylevel", aliases = {}, description = "Check if the origin is at a certain Y level")
public class YLevelCondition implements ISkillMetaCondition {

    private final PlaceholderInt yLevel;

    public YLevelCondition(MythicLineConfig mlc) {
        this.yLevel = PlaceholderInt.of(mlc.getString("y", "0"));
    }

    @Override
    public boolean check(SkillMetadata data) {
        return data.getOrigin().getBlockY() == yLevel.get(data);
    }
}