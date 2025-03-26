package me.bedwarshurts.mmextension.mechanics.list;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableScope;
import io.lumine.mythic.core.skills.variables.types.DoubleVariable;
import io.lumine.mythic.core.skills.variables.types.IntegerVariable;
import io.lumine.mythic.core.skills.variables.types.StringVariable;

public class CreateVariablesMechanic implements ITargetedEntitySkill {
    private final String name;
    private final String registry;

    public CreateVariablesMechanic(MythicLineConfig mlc) {
        this.name = mlc.getString(new String[]{"name"}, "");
        this.registry = mlc.getString(new String[]{"registry"}, "");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        ListHandler<?> listHandler = ListHandler.getListHandler(name);
        if (listHandler == null) return SkillResult.INVALID_CONFIG;

        VariableTypes type = listHandler.getType();
        VariableRegistry variables = switch (registry) {
            case "target" -> MythicBukkit.inst().getVariableManager().getRegistry(VariableScope.TARGET, target);
            case "global" -> MythicBukkit.inst().getVariableManager().getRegistry(VariableScope.GLOBAL, data);
            case "caster" -> MythicBukkit.inst().getVariableManager().getRegistry(VariableScope.CASTER, data);
            case "world" ->
                    MythicBukkit.inst().getVariableManager().getRegistry(VariableScope.WORLD, target.getLocation());
            default -> null;
        };
        if (variables == null) return SkillResult.INVALID_CONFIG;

        String key = name + ".";
        for (int i = 0; i < listHandler.getRegistry().size(); i++) {
            switch (type) {
                case INTEGER:
                    variables.put(key + i, new IntegerVariable((int) listHandler.get(i)));
                    break;
                case STRING:
                    variables.put(key + i, new StringVariable(listHandler.get(i).toString()));
                    break;
                case DOUBLE:
                    variables.put(key + i, new DoubleVariable((double) listHandler.get(i)));
                    break;
                default:
                    return SkillResult.INVALID_CONFIG;
            }
        }
        return SkillResult.SUCCESS;
    }
}
