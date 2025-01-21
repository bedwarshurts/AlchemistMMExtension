package me.bedwarshurts.mmextension.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@MythicMechanic(author = "bedwarshurts", name = "ringshape", aliases = {}, description = "Spawns particles in a ring shape and casts a skill at each particle location")
public class RingShapeMechanic extends SkillMechanic implements ITargetedLocationSkill {
    private final Particle particleType;
    private final PlaceholderInt particleCount;
    private final PlaceholderDouble radius;
    private final PlaceholderDouble dirMultiplier;
    private final PlaceholderDouble variance;
    private final PlaceholderDouble shiftRadius;
    private final List<PlaceholderDouble> direction;
    private final PlaceholderDouble speed;
    private final PlaceholderString skillName;
    private final SkillExecutor skillExecutor;
    private final PlaceholderDouble delay;

    public RingShapeMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
        super(manager, file, line, mlc);
        this.particleType = Particle.valueOf(mlc.getString("particle", "FLAME").toUpperCase());
        this.radius = PlaceholderDouble.of(mlc.getString("radius", "1.0"));
        this.particleCount = PlaceholderInt.of(mlc.getString("count", "100"));
        this.dirMultiplier = PlaceholderDouble.of(mlc.getString("dirMultiplier", "1.0"));
        this.shiftRadius = PlaceholderDouble.of(mlc.getString("shift", "0.0"));
        this.variance = PlaceholderDouble.of(mlc.getString("variance", "0.0"));
        String[] directionArgs = mlc.getString("direction", "0,0,0").split(","); // y variable here gets ignored since it's a ring shape
        this.speed = PlaceholderDouble.of(mlc.getString("speed", "0.1"));
        this.skillName = PlaceholderString.of(mlc.getString("skill", ""));
        this.delay = PlaceholderDouble.of(mlc.getString("delay", "0"));
        this.direction = List.of(
                PlaceholderDouble.of(directionArgs[0]),
                PlaceholderDouble.of(directionArgs[1]),
                PlaceholderDouble.of(directionArgs[2])
        );
        this.skillExecutor = manager;
    }

    @Override
    public SkillResult castAtLocation(SkillMetadata data, AbstractLocation targetLocation) {
        Location origin = targetLocation.toPosition().toLocation();
        Random random = new Random();

        final double[] newRadius = {radius.get(data)};
        List<Double> newDirection = direction.stream().map(d -> d.get(data)).collect(Collectors.toList());

        for (int i = 0; i < particleCount.get(data); i++) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getProvidingPlugin(getClass()), () -> {
                double angle = 2 * Math.PI * random.nextDouble();
                double dr = newRadius[0] + (random.nextDouble() * 2 - 1) * variance.get(data);
                double x = dr * Math.cos(angle);
                double z = dr * Math.sin(angle);

                double dx = x * newDirection.get(0);
                double dz = z * newDirection.get(2);

                newRadius[0] += shiftRadius.get(data);

                for (int j = 0; j < newDirection.size(); j++) {
                    newDirection.set(j, newDirection.get(j) * dirMultiplier.get(data));
                }

                Location particleLocation = origin.clone().add(x, 0, z);
                origin.getWorld().spawnParticle(particleType, particleLocation, 0, dx, 0, dz, speed.get(data));

                SkillUtils.castSkillAtPoint(data, particleLocation, skillName, skillExecutor);
            }, (long) (delay.get(data) * i / 50)); // Convert delay from milliseconds to ticks (50 ms = 1 tick)
        }

        return SkillResult.SUCCESS;
    }
}