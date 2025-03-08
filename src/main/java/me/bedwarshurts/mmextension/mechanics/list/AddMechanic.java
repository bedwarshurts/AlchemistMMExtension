package me.bedwarshurts.mmextension.mechanics.list;

import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

@MythicMechanic(author = "bedwarshurts", name = "add", aliases = {}, description = "Adds a value to a list")
public class AddMechanic implements INoTargetSkill {
    private final String name;
    private final String value;

    public AddMechanic(MythicLineConfig mlc) {
        this.name = mlc.getString(new String[]{"name"}, "");
        this.value = mlc.getString(new String[]{"value"}, "");
    }

    @Override
    public SkillResult cast(SkillMetadata skillMetadata) {
        ListHandler<Object> listHandler = ListHandler.getListHandler(name);
        if (listHandler == null) return SkillResult.INVALID_CONFIG;

        switch (listHandler.getType()) {
            case STRING:
                listHandler.register(value);
                break;
            case INTEGER:
                listHandler.register(Integer.parseInt(value));
                break;
            default:
                return SkillResult.INVALID_CONFIG;
        }
        return SkillResult.SUCCESS;
    }
}
