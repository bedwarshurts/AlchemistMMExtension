package me.bedwarshurts.mmextension.skills.mechanics.list;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

@MythicMechanic(author = "bedwarshurts", name = "list", aliases = {}, description = "Creates a list")
public class ListMechanic implements INoTargetSkill {

    private final String name;
    private final String type;

    public ListMechanic(MythicLineConfig mlc) {
        this.name = mlc.getString(new String[]{"name"}, "");
        this.type = mlc.getString(new String[]{"type"}, "");
    }

    public SkillResult cast(SkillMetadata data) {
        switch (this.type.toLowerCase()) {
            case "string":
                ListHandler.createListHandler(name, VariableTypes.STRING);
                break;
            case "integer":
                ListHandler.createListHandler(name, VariableTypes.INTEGER);
                break;
            case "double":
                ListHandler.createListHandler(name, VariableTypes.DOUBLE);
                break;
            default:
                return SkillResult.INVALID_CONFIG;
        }
        return SkillResult.SUCCESS;
    }
}
