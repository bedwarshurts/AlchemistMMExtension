package me.bedwarshurts.mmextension.mechanics.list;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.types.IntegerVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

@MythicMechanic(author = "bedwarshurts", name = "index", aliases = {}, description = "Gets the index of a value in a list")
public class IndexMechanic implements INoTargetSkill {
    private final String name;
    private final String value;
    private final boolean last;

    public IndexMechanic(MythicLineConfig mlc) {
        this.name = mlc.getString(new String[]{"name"}, "");
        this.value = mlc.getString(new String[]{"value"}, "");
        this.last = mlc.getBoolean(new String[]{"last"}, false);
    }

    @Override
    public SkillResult cast(SkillMetadata skillMetadata) {
        ListHandler<?> listHandler = ListHandler.getListHandler(name);
        if (listHandler == null) return SkillResult.INVALID_CONFIG;

        int index;
        switch (listHandler.getType()) {
            case STRING:
                if (last) {
                    index = listHandler.getRegistry().lastIndexOf(value);
                    break;
                }
                index = listHandler.getRegistry().indexOf(value);
                break;
            case INTEGER:
                if (last) {
                    index = listHandler.getRegistry().lastIndexOf(Integer.parseInt(value));
                    break;
                }
                index = listHandler.getRegistry().indexOf(Integer.parseInt(value));
                break;
            default:
                return SkillResult.INVALID_CONFIG;
        }

        VariableRegistry variables = skillMetadata.getVariables();
        variables.put("returnValue", new IntegerVariable(index));
        return SkillResult.SUCCESS;
    }
}
