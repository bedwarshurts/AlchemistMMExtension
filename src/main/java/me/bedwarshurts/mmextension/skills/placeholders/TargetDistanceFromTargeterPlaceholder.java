package me.bedwarshurts.mmextension.skills.placeholders;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.targeters.IEntityTargeter;
import io.lumine.mythic.api.skills.targeters.ILocationTargeter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.all.MetaTargetPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

import java.util.Collection;
import java.util.Comparator;

@MythicPlaceholder(placeholder = "target.distanceFrom", description = "Returns the distance from the targeter to the target.")
public class TargetDistanceFromTargeterPlaceholder implements MetaTargetPlaceholder {

    @Override
    public String apply(PlaceholderMeta placeholderMeta, AbstractEntity abstractEntity, String s) {
        if (!(placeholderMeta instanceof SkillMetadata data)) return "[Invalid PlaceholderMeta]";

        String[] parts = s.split("\\.");
        SkillTargeter targeter = MythicBukkit.inst().getSkillManager().getTargeter(parts[0]);

        AbstractLocation targetLoc = abstractEntity.getLocation();
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
                            .min(Comparator.comparingDouble(targetLoc::distance)).get() :
                    targeterList.stream()
                            .max(Comparator.comparingDouble(targetLoc::distance)).get();
        }

        return String.valueOf(targetLoc.distance(targeterResult));
    }
}
