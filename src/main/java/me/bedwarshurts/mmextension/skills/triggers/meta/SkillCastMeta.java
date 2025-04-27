package me.bedwarshurts.mmextension.skills.triggers.meta;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;

public class SkillCastMeta extends SkillTriggerMetadata {

    private final PlayerCastSkillEvent event;

    public SkillCastMeta(PlayerCastSkillEvent event) {
        this.event = event;
    }

    @Override
    public void applyToSkillMetadata(SkillMetadata data) {
        data.getVariables().put("caster", new StringVariable(event.getPlayer().getName()));
        data.getVariables().put("skill", new StringVariable(event.getMetadata().getCast().getHandler().getId()));
    }
}
