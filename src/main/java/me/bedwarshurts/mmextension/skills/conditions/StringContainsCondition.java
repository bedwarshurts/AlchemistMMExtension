package me.bedwarshurts.mmextension.skills.conditions;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import org.jetbrains.annotations.Nullable;

@MythicCondition(author = "bedwarshurts", name = "stringcontains", aliases = {}, description = "Check if one string is contained within another")
public class StringContainsCondition implements ISkillMetaCondition {

    private final PlaceholderString string;
    private final PlaceholderString contains;

    public StringContainsCondition(MythicLineConfig mlc) {
        this.string = mlc.getPlaceholderString(new String[]{"string", "s"}, "anexampleword");
        this.contains = mlc.getPlaceholderString(new String[]{"contains", "c"}, "example");
    }

    @Override
    public boolean check(SkillMetadata skillMetadata) {
        String parsedString = string.get(skillMetadata);
        String parsedContains = contains.get(skillMetadata);

        return parsedString.contains(parsedContains);
    }
}