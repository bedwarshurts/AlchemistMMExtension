package me.bedwarshurts.mmextension.mechanics.particle;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@MythicMechanic(author = "bedwarshurts", name = "sphereshape", aliases = {}, description = "Spawns particles in a sphere shape and casts a skill at each particle location")
public final class SphereShapeMechanic extends ParticleMechanic implements ITargetedLocationSkill {

    public SphereShapeMechanic(MythicLineConfig mlc) {
        super(mlc);
    }

    @Override
    public SkillResult castAtLocation(SkillMetadata data, AbstractLocation targetLocation) {
        Location origin = targetLocation.toPosition().toLocation();
        Random random = new Random();

        final double[] newRadius = {radius.get(data)};
        List<Double> newDirection = direction.stream().map(d -> d.get(data)).collect(Collectors.toList());

        final Set<Player> audience = SkillUtils.getAudienceTargets(data, audienceTargeter);

        final Vector offset;
        if (dirOverride != null) {
            offset = new Vector(dirOverride.get(0).get(data), dirOverride.get(1).get(data), dirOverride.get(2).get(data)).subtract(origin.toVector());
        } else {
            offset = null;
        }

        for (int i = 0; i < particleCount.get(data); i++) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getProvidingPlugin(getClass()), () -> {
                double theta = -360 + random.nextDouble() * 720;
                double phi = -720 + random.nextDouble() * 1440;

                double dr = newRadius[0] + (random.nextDouble() * 2 - 1) * variance.get(data);
                double x = dr * Math.sin(theta) * Math.cos(phi);
                double y = dr * Math.cos(theta);
                double z = dr * Math.sin(theta) * Math.sin(phi);

                double dx;
                double dy;
                double dz;

                newRadius[0] += shiftRadius.get(data);

                int j = 0;
                while (j < newDirection.size()) {
                    newDirection.set(j, newDirection.get(j) * dirMultiplier.get(data));
                    j++;
                }

                Location particleLocation = origin.clone().add(x, y, z);

                Vector endLocation;
                Vector directionVector;
                if (offset != null) {
                    endLocation = particleLocation.clone().add(offset).toVector();
                    directionVector = endLocation.subtract(particleLocation.toVector()).normalize();
                } else {
                    directionVector = particleLocation.toVector().subtract(origin.toVector()).normalize();
                }

                directionVector.multiply(new Vector(newDirection.get(0), newDirection.get(1), newDirection.get(2)));

                dx = directionVector.getX() * dirMultiplier.get(data);
                dy = directionVector.getY() * dirMultiplier.get(data);
                dz = directionVector.getZ() * dirMultiplier.get(data);

                SkillUtils.spawnParticle(audience, particleType, particleLocation, dx, dy, dz, speed.get(data));

                SkillUtils.castSkillAtPoint(data, particleLocation, skillName.get(data));
            }, (long) (delayMs.get(data) * i / 50)); // Convert delay from milliseconds to ticks (50 ms = 1 tick)
        }

        return SkillResult.SUCCESS;
    }
}