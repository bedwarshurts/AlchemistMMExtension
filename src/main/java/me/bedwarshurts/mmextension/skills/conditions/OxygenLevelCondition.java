package me.bedwarshurts.mmextension.skills.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.LivingEntity;

@MythicCondition(author = "bedwarshurts", name = "oxygenlevel", aliases = {}, description = "Check the oxygen level of the caster")
public class OxygenLevelCondition implements IEntityCondition {

    private final String airTicks;

    public OxygenLevelCondition(MythicLineConfig mlc) {
        this.airTicks = mlc.getString(new String[]{"air", "a"}, "<0");
    }

    @Override
    public boolean check(AbstractEntity target) {
        if (!(target.getBukkitEntity() instanceof LivingEntity entity)) return false;

        return switch (airTicks.charAt(0)) {
            case '<' -> entity.getRemainingAir() < Integer.parseInt(airTicks.substring(1));
            case '>' -> entity.getRemainingAir() > Integer.parseInt(airTicks.substring(1));
            default -> entity.getRemainingAir() == Integer.parseInt(airTicks);
        };
    }
}
