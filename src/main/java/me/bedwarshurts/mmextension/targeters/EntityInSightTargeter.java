package me.bedwarshurts.mmextension.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.targeters.IEntityTargeter;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;

import java.util.Collection;
import java.util.List;

@MythicTargeter(author = "bedwarshurts", name = "targetentityinsight", aliases = {"TEIS"}, description = "Targets the entity the caster is looking at")
public class EntityInSightTargeter implements IEntityTargeter {
    private final double maxDistance;

    public EntityInSightTargeter(MythicLineConfig mlc) {
        this.maxDistance = mlc.getDouble(new String[]{"maxDistance", "md"}, 10);
    }

    @Override
    public Collection<AbstractEntity> getEntities(SkillMetadata data) {
        if (!(data.getCaster().getEntity() instanceof LivingEntity livingEntity)) return null;

        Entity targetEntity = getEntityPlayerIsLookingAt(livingEntity, maxDistance);
        if (targetEntity == null)
            return List.of();

        return List.of(BukkitAdapter.adapt(targetEntity));
    }

    private Entity getEntityPlayerIsLookingAt(LivingEntity entity, double maxDistance) {
        Location eyeLocation = entity.getEyeLocation();
        RayTraceResult result = entity.getWorld().rayTraceEntities(eyeLocation, eyeLocation.getDirection(), maxDistance,
                0.5, tracedEntity -> !tracedEntity.equals(entity));
        return result != null ? result.getHitEntity() : null;
    }
}
