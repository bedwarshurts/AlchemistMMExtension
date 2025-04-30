package me.bedwarshurts.mmextension.skills.mechanics.variable;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableScope;
import io.lumine.mythic.core.skills.variables.types.DoubleVariable;
import io.lumine.mythic.core.skills.variables.types.IntegerVariable;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.PlaceholderUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.Arrays;

@MythicMechanic(author = "bedwarshurts", description = "Mechanic that allows you to edit variables in a more conventional way")
public class VariableEditMechanic implements ITargetedEntitySkill {

    private final VariableScope scope;
    private String key;
    private final String operationKey;
    private String operation;

    public VariableEditMechanic(String variableLine) {
        System.out.println(variableLine);
        String[] parts = variableLine.split(" ");
        String scope = parts[0];
        this.scope = switch (scope) {
            case "target" -> VariableScope.TARGET;
            case "global" -> VariableScope.GLOBAL;
            case "skill" -> VariableScope.SKILL;
            case "caster" -> VariableScope.CASTER;
            case "world" -> VariableScope.WORLD;
            default -> throw new IllegalArgumentException("Invalid scope: " + scope);
        };
        this.key = parts[1];
        this.operationKey = parts[2];
        this.operation = String.join(" ", Arrays.copyOfRange(parts, 3, parts.length));
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        key = PlaceholderUtils.parseStringPlaceholders(key, data);
        operation = PlaceholderUtils.parseStringPlaceholders(operation, data);
        if (target.isPlayer()) {
            operation = PlaceholderAPI.setPlaceholders((Player) target.getBukkitEntity(), operation);
            key = PlaceholderAPI.setPlaceholders((Player) target.getBukkitEntity(), key);
        } else {
            operation = PlaceholderAPI.setPlaceholders(null, operation);
            key = PlaceholderAPI.setPlaceholders(null, key);
        }


        VariableRegistry variables = MythicBukkit.inst().getVariableManager().getRegistry(scope, data);
        Variable variable = variables.get(key);

        if (variable == null) {
            return SkillResult.INVALID_TARGET;
        }

        if (variable instanceof StringVariable casted) {
            operation = removeSpacesOutsideQuotes(operation);
            StringBuilder builder = new StringBuilder(casted.getValue());
            switch (operationKey) {
                case "*=":
                    builder.append(String.valueOf(casted.getValue()).repeat(Math.max(0, Integer.parseInt(operation))));
                    break;
                case "+=":
                    builder.append(operation);
                    break;
                case "=":
                    builder = new StringBuilder();
                    String[] split = operation.split("\\+");
                    for (String s : split) {
                        if (s.contains("*")) {
                            String[] parts = s.split("\\*");
                            int times = Integer.parseInt(parts[1].trim());
                            builder.append(String.valueOf(parts[0]).repeat(Math.max(0, times)));
                        } else {
                            builder.append(s);
                        }
                    }
                    break;
                default: throw new IllegalArgumentException("Invalid operation key for StringVariable: " + operationKey);
            }
            MiniMessage miniMessage = MiniMessage.miniMessage();
            casted.setValue(miniMessage.serialize(miniMessage.deserialize(PlaceholderUtils.parseMythicTags(builder.toString()))));
            return SkillResult.SUCCESS;
        }

        Expression ex;
        if (operationKey.equals("=")) {
            ex = new Expression(operation);
        } else {
            ex = new Expression(String.format("%s %s (%s)", variable.get().toString(), operationKey.replace("=", ""), operation));
        }

        if (variable instanceof DoubleVariable casted) {
            casted.setValue(ex.calculate());
        } else if (variable instanceof IntegerVariable casted) {
            casted.setValue((int) ex.calculate());
        }

        return SkillResult.SUCCESS;
    }

    private String removeSpacesOutsideQuotes(String input) {
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (char c : input.toCharArray()) {
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c != ' ' || inQuotes) {
                sb.append(c);
            }
        }

        return sb.toString().replaceAll("\"", "");
    }
}
