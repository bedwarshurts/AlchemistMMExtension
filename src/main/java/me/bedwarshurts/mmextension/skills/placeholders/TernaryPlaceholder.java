package me.bedwarshurts.mmextension.skills.placeholders;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.conditions.InvalidCondition;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.types.MetaPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

@MythicPlaceholder(placeholder = "Ternary Placeholder by bedwarshurts", description = "A placeholder that evaluates a condition and returns one of two values based on the result.")
public class TernaryPlaceholder implements MetaPlaceholder {

    @Override
    public String apply(PlaceholderMeta meta, String input) {
        if (!(meta instanceof SkillMetadata data)) return "[Invalid Placeholder]";
        if (input == null || !input.contains(".") || !input.contains("?")) return "[Invalid Format]";

        input = input.replaceAll(" ", "");
        String[] split = input.split("\\.");
        if (split.length < 2) return "[Invalid Format]";
        String scope = split[0];

        String[] operation = split[1].split("\\?");
        if (operation.length < 2 || !operation[1].contains(":")) return "[Invalid Format]";
        String conditionString = operation[0].replace("[", "{").replace("]", "}");

        boolean negate = conditionString.startsWith("!");
        if (negate) conditionString = conditionString.substring(1);

        SkillCondition condition = MythicBukkit.inst().getSkillManager().getCondition(conditionString);
        if (condition instanceof InvalidCondition) return "[Invalid Condition]";

        boolean result = switch (scope) {
            case "caster" -> condition.evaluateCaster(data);
            case "target" -> condition.evaluateTargets(data);
            case "trigger" -> condition.evaluateTrigger(data);
            default -> false;
        };
        if (negate) result = !result;

        int delim = operation[1].indexOf(':');
        return result ? operation[1].substring(0, delim) : operation[1].substring(delim + 1);
    }
}
