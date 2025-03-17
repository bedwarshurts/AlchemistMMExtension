package me.bedwarshurts.mmextension.conditions;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.LivingEntity;

@MythicCondition(author = "bedwarshurts", name = "oxygenlevel", aliases = {}, description = "Check the oxygen level of the caster")
public class OxygenLevelCondition implements ISkillMetaCondition {
    private final PlaceholderString airTicks;

    public OxygenLevelCondition(MythicLineConfig mlc) {
        this.airTicks = mlc.getPlaceholderString(new String[]{"air", "a"}, "<0");
    }

    @Override
    public boolean check(SkillMetadata data) {
        if (!(data.getCaster().getEntity().getBukkitEntity() instanceof LivingEntity caster)) return false;

        return switch (airTicks.get(data).charAt(0)) {
            case '<':
                yield caster.getRemainingAir() < Integer.parseInt(airTicks.get(data).substring(1));
            case '>':
                yield caster.getRemainingAir() > Integer.parseInt(airTicks.get(data).substring(1));
            default:
                yield caster.getRemainingAir() == Integer.parseInt(airTicks.get(data));
        };
    }
}
