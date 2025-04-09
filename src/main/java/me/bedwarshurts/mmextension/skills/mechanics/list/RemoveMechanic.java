package me.bedwarshurts.mmextension.skills.mechanics.list;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

@MythicMechanic(author = "bedwarshurts", name = "remove", aliases = {}, description = "Removes a value from a list")
public class RemoveMechanic implements INoTargetSkill {
    private final String name;
    private final int index;

    public RemoveMechanic(MythicLineConfig mlc) {
        this.name = mlc.getString(new String[]{"name"}, "");
        this.index = mlc.getInteger(new String[]{"index"}, 0);
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        ListHandler<?> listHandler = ListHandler.getListHandler(name);
        if (listHandler == null) return SkillResult.INVALID_CONFIG;

        listHandler.remove(index);

        return SkillResult.SUCCESS;
    }
}
