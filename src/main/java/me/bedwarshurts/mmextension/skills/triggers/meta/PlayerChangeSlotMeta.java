package me.bedwarshurts.mmextension.skills.triggers.meta;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class PlayerChangeSlotMeta extends SkillTriggerMetadata {

    private final PlayerItemHeldEvent event;

    public PlayerChangeSlotMeta(PlayerItemHeldEvent event) {
        this.event = event;
    }

    @Override
    public void applyToSkillMetadata(SkillMetadata data) {
        data.getVariables().put("trigger", new StringVariable(event.getPlayer().getName()));
        data.getVariables().put("previousSlot", new StringVariable(String.valueOf(event.getPreviousSlot())));
        data.getVariables().put("currentSlot", new StringVariable(String.valueOf(event.getNewSlot())));
    }
}
