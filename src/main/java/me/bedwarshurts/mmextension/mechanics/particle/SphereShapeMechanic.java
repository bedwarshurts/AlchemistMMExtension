package me.bedwarshurts.mmextension.mechanics.particle;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@MythicMechanic(author = "bedwarshurts", name = "sphereshape", aliases = {}, description = "Spawns particles in a sphere shape and casts a skill at each particle location")
public class SphereShapeMechanic extends ParticleMechanic implements ITargetedLocationSkill {

    public SphereShapeMechanic(SkillExecutor manager, MythicLineConfig mlc) {
        super(manager, mlc);
    }

    @Override
    public SkillResult castAtLocation(SkillMetadata data, AbstractLocation targetLocation) {
        Location origin = targetLocation.toPosition().toLocation();
        Random random = new Random();

        final double[] newRadius = {radius.get(data)};
        List<Double> newDirection = direction.stream().map(d -> d.get(data)).collect(Collectors.toList());

        final Set<Player> audience = SkillUtils.getAudienceTargets(data, audienceTargeter);

        for (int i = 0; i < particleCount.get(data); i++) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getProvidingPlugin(getClass()), () -> {
                double theta = -360 + random.nextDouble() * 720;
                double phi = -720 + random.nextDouble() * 1440;

                double dr = newRadius[0] + (random.nextDouble() * 2 - 1) * variance.get(data);
                double x = dr * Math.sin(theta) * Math.cos(phi);
                double y = dr * Math.cos(theta);
                double z = dr * Math.sin(theta) * Math.sin(phi);

                double dx = x * newDirection.get(0);
                double dy = y * newDirection.get(1);
                double dz = z * newDirection.get(2);

                newRadius[0] += shiftRadius.get(data);

                int j = 0;
                while (j < newDirection.size()) {
                    newDirection.set(j, newDirection.get(j) * dirMultiplier.get(data));
                    j++;
                }

                Location particleLocation = origin.clone().add(x, y, z);

                SkillUtils.spawnParticle(audience, particleType, particleLocation, dx, dy, dz, speed.get(data));

                SkillUtils.castSkillAtPoint(data, particleLocation, skillName, skillExecutor);
            }, (long) (delay.get(data) * i / 50)); // Convert delay from milliseconds to ticks (50 ms = 1 tick)
        }

        return SkillResult.SUCCESS;
    }
}