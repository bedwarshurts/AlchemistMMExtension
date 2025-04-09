package me.bedwarshurts.mmextension.skills.mechanics.list;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.types.DoubleVariable;
import io.lumine.mythic.core.skills.variables.types.IntegerVariable;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

@MythicMechanic(author = "bedwarshurts", name = "add", aliases = {}, description = "Gets a value from a list")
public class GetMechanic implements INoTargetSkill {
    private final String name;
    private final int index;

    public GetMechanic(MythicLineConfig mlc) {
        this.name = mlc.getString(new String[]{"name"}, "");
        this.index = mlc.getInteger(new String[]{"index"}, 0);
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        VariableRegistry registry = data.getVariables();

        ListHandler<?> listHandler = ListHandler.getListHandler(name);
        if (listHandler == null) return SkillResult.INVALID_CONFIG;

        switch (listHandler.getType()) {
            case VariableTypes.STRING:
                registry.put("returnValue", new StringVariable((String) listHandler.get(index)));
                break;
            case VariableTypes.INTEGER:
                registry.put("returnValue", new IntegerVariable((int) listHandler.get(index)));
                break;
            case VariableTypes.DOUBLE:
                registry.put("returnValue", new DoubleVariable((double) listHandler.get(index)));
                break;
            default:
                return SkillResult.INVALID_CONFIG;
        }
        return SkillResult.SUCCESS;
    }
}
