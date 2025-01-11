package me.bedwarshurts.mmextension.conditions;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;

@MythicCondition(author = "bedwarshurts", name = "isylevel", aliases = {}, description = "Check if the origin is at a certain Y level")
public class YLevelCondition extends SkillCondition implements ISkillMetaCondition {
    private final PlaceholderInt yLevel;

    public YLevelCondition(MythicLineConfig mlc) {
        super(mlc.getLine());
        this.yLevel = PlaceholderInt.of(mlc.getString("y", "0"));
    }

    @Override
    public boolean check(SkillMetadata skillMetadata) {
        return skillMetadata.getOrigin().getBlockY() == yLevel.get(skillMetadata);
    }
}