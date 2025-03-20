package me.bedwarshurts.mmextension.mechanics.list;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

@MythicMechanic(author = "bedwarshurts", name = "replace", aliases = {}, description = "Replaces a value in a list")
public final class ReplaceMechanic implements INoTargetSkill {
    private final String name;
    private final int index;
    private final String value;

    public ReplaceMechanic(MythicLineConfig mlc) {
        this.name = mlc.getString(new String[]{"name"}, "");
        this.index = mlc.getInteger(new String[]{"index"}, 0);
        this.value = mlc.getString(new String[]{"value"}, "");
    }

    @Override
    public SkillResult cast(SkillMetadata skillMetadata) {
        ListHandler<Object> listHandler = ListHandler.getListHandler(name);
        if (listHandler == null) return SkillResult.INVALID_CONFIG;

        switch (listHandler.getType()) {
            case VariableTypes.STRING:
                listHandler.replace(index, value);
                break;
            case VariableTypes.INTEGER:
                listHandler.replace(index, Integer.parseInt(value));
                break;
            case VariableTypes.DOUBLE:
                listHandler.replace(index, Double.parseDouble(value));
                break;
            default:
                return SkillResult.INVALID_CONFIG;
        }
        return SkillResult.SUCCESS;
    }
}
