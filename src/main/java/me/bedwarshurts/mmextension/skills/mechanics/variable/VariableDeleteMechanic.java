package me.bedwarshurts.mmextension.skills.mechanics.variable;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableScope;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

@MythicMechanic(author = "bedwarshurts", description = "Deletes a variable from the specified scope")
public class VariableDeleteMechanic implements ITargetedEntitySkill {

    private final String name;
    private final VariableScope scope;

    public VariableDeleteMechanic(String variableLine) {
        String[] parts = variableLine.split(" ");
        this.name = parts[2];
        this.scope = switch (parts[1]) {
            case "target" -> VariableScope.TARGET;
            case "global" -> VariableScope.GLOBAL;
            case "skill" -> VariableScope.SKILL;
            case "caster" -> VariableScope.CASTER;
            case "world" -> VariableScope.WORLD;
            default -> throw new IllegalArgumentException("Invalid scope: " + parts[1]);
        };
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        VariableRegistry variables = MythicBukkit.inst().getVariableManager().getRegistry(scope, data);

        if (!variables.has(name)) {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Variable {0} does not exist in scope {1}", name, scope);
            return SkillResult.CONDITION_FAILED;
        }

        variables.remove(name);
        return SkillResult.SUCCESS;
    }
}
