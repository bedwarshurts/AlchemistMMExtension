package me.bedwarshurts.mmextension.skills.triggers.meta;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;

public class PreAttackEntityMeta extends SkillTriggerMetadata {

    private final PrePlayerAttackEntityEvent event;

    public PreAttackEntityMeta(PrePlayerAttackEntityEvent event) {
        this.event = event;
    }

    @Override
    public void applyToSkillMetadata(SkillMetadata data) {
        data.getVariables().put("attackerName", new StringVariable(event.getPlayer().getName()));
        data.getVariables().put("attackerUUID", new StringVariable(event.getPlayer().getUniqueId().toString()));
    }
}
