package me.bedwarshurts.mmextension.skills.conditions;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import me.bedwarshurts.mmextension.utils.PlaceholderUtils;

@MythicCondition(author = "bedwarshurts", name = "isylevel", aliases = {}, description = "Check if the origin is at a certain Y level")
public class YLevelCondition implements ISkillMetaCondition {

    private final String yLevel;

    public YLevelCondition(MythicLineConfig mlc) {
        this.yLevel = mlc.getString("y", "0");
    }

    @Override
    public boolean check(SkillMetadata data) {
        String yLevelParsed = PlaceholderUtils.parseDoublePlaceholders(yLevel, data);

        return switch (yLevelParsed.charAt(0)) {
            case '<' -> data.getOrigin().getBlockY() < Integer.parseInt(yLevelParsed.substring(1));
            case '>' -> data.getOrigin().getBlockY() > Integer.parseInt(yLevelParsed.substring(1));
            default -> data.getOrigin().getBlockY() == Integer.parseInt(yLevelParsed);
        };
    }
}