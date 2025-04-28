package me.bedwarshurts.mmextension.skills.mechanics.variable;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableScope;
import io.lumine.mythic.core.skills.variables.types.DoubleVariable;
import io.lumine.mythic.core.skills.variables.types.FloatVariable;
import io.lumine.mythic.core.skills.variables.types.IntegerVariable;
import io.lumine.mythic.core.skills.variables.types.LocationVariable;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.PlaceholderUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

@MythicMechanic(author = "bedwarshurts", description = "Sets a variable in an easier to read way")
public class VariableMechanic implements ITargetedEntitySkill {
    private final VariableScope scope;
    private final String type;
    private String key;
    private String value;
    private final String line;

    public VariableMechanic(String variableLine) {
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
        this.type = parts[1];
        this.key = parts[2];
        this.value = parts[4];
        this.line = variableLine;
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        key = PlaceholderUtils.parseStringPlaceholders(key, data);
        value = PlaceholderUtils.parseStringPlaceholders(value, data);
        if (target.isPlayer()) {
            value = PlaceholderAPI.setPlaceholders((Player) target.getBukkitEntity(), value);
            key = PlaceholderAPI.setPlaceholders((Player) target.getBukkitEntity(), key);
        } else {
            value = PlaceholderAPI.setPlaceholders(null, value);
            key = PlaceholderAPI.setPlaceholders(null, key);
        }

        VariableRegistry registry = MythicBukkit.inst().getVariableManager().getRegistry(scope, data);

        if (registry == null) {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "ERROR GETTING VARIABLE REGISTRY " + line);
            return SkillResult.ERROR;
        }

        switch (type.toLowerCase()) {
            case "int":
                registry.put(key, new IntegerVariable(Integer.parseInt(value)));
                break;
            case "string":
                registry.put(key, new StringVariable(value));
                break;
            case "double":
                registry.put(key, new DoubleVariable(Double.parseDouble(value)));
                break;
            case "float":
                registry.put(key, new FloatVariable(Float.parseFloat(value)));
                break;
            case "location":
                registry.put(key, new LocationVariable(target.getLocation()));
                break;
            default: break;
        }
        return SkillResult.SUCCESS;
    }
}
