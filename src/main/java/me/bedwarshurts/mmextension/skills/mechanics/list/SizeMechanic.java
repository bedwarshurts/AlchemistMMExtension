package me.bedwarshurts.mmextension.skills.mechanics.list;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.types.IntegerVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

@MythicMechanic(author = "bedwarshurts", name = "size", aliases = {}, description = "Gets the size of a list")
public class SizeMechanic implements INoTargetSkill {
    private final String name;

    public SizeMechanic(MythicLineConfig mlc) {
        this.name = mlc.getString(new String[]{"name"}, "");
    }
    @Override
    public SkillResult cast(SkillMetadata skillMetadata) {
        ListHandler<?> listHandler = ListHandler.getListHandler(name);
        if (listHandler == null) return SkillResult.INVALID_CONFIG;

        VariableRegistry variables = skillMetadata.getVariables();
        variables.put("returnResult", new IntegerVariable(listHandler.getRegistry().size()));
        return SkillResult.SUCCESS;
    }
}
