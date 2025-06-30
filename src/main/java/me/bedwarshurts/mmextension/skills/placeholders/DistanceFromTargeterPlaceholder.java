package me.bedwarshurts.mmextension.skills.placeholders;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.targeters.IEntityTargeter;
import io.lumine.mythic.api.skills.targeters.ILocationTargeter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.types.MetaPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

import java.util.Collection;
import java.util.Comparator;

@MythicPlaceholder(placeholder = "caster.distanceFrom", description = "Returns distance from a targeter")
public class DistanceFromTargeterPlaceholder implements MetaPlaceholder {

    @Override
    public String apply(PlaceholderMeta placeholderMeta, String s) {
        if (!(placeholderMeta instanceof SkillMetadata data)) return "[Invalid PlaceholderMeta]";

        String[] parts = s.split("\\.");
        SkillTargeter targeter = MythicBukkit.inst().getSkillManager().getTargeter(parts[0]);

        AbstractLocation casterLoc = data.getCaster().getLocation();
        Collection<AbstractLocation> targeterList;

        if (targeter instanceof ILocationTargeter locTargeter) {
            targeterList = locTargeter.getLocations(data);
        } else if (targeter instanceof IEntityTargeter entityTargeter) {
            targeterList = entityTargeter.getEntities(data).stream()
                    .map(AbstractEntity::getLocation)
                    .toList();
        } else {
            return "[Invalid Targeter: " + parts[0] + "]";
        }

        if (targeterList.isEmpty()) {
            return "0";
        }

        AbstractLocation targeterResult;
        if (targeterList.size() == 1) {
            targeterResult = targeterList.stream().findFirst().get();
        } else {
            if (parts.length < 2) {
                return "[Invalid Placeholder: " + s + " - specify 'min' or 'max' for multi location targeters]";
            }
            targeterResult = parts[1].equalsIgnoreCase("min") ?
                    targeterList.stream()
                            .min(Comparator.comparingDouble(casterLoc::distance)).get() :
                    targeterList.stream()
                            .max(Comparator.comparingDouble(casterLoc::distance)).get();
        }

        return String.valueOf(casterLoc.distance(targeterResult));
    }
}
