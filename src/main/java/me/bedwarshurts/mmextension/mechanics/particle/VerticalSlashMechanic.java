package me.bedwarshurts.mmextension.mechanics.particle;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

@MythicMechanic(author = "bedwarshurts", name = "verticalslash", aliases = {}, description = "Spawns particles in a half circle shape")
public class VerticalSlashMechanic extends ParticleMechanic implements ITargetedLocationSkill {

    public VerticalSlashMechanic(MythicLineConfig mlc) {
        super(mlc);
    }

    @Override
    public SkillResult castAtLocation(SkillMetadata data, AbstractLocation location) {
        Location origin = BukkitAdapter.adapt(data.getOrigin());
        Location target = BukkitAdapter.adapt(location);

        Vector chord = target.toVector().subtract(origin.toVector());
        double distance = chord.length();
        Vector chordDir = chord.clone().normalize();

        Vector globalUp = new Vector(0, 1, 0);
        Vector planeNormal = chordDir.clone().crossProduct(globalUp).normalize();
        Vector arcUp = planeNormal.clone().crossProduct(chordDir).normalize();

        Location center = origin.clone().add(chord.multiply(0.5));
        double radius = distance / 2;

        for (int i = 0; i <= 180; i++) {
            int finalI = i;
            Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getProvidingPlugin(getClass()), () -> {
                        double angle = Math.toRadians(180 - finalI);
                        double cos = Math.cos(angle);
                        double sin = Math.sin(angle);

                        Vector offset = chordDir.clone().multiply(cos * radius)
                                .add(arcUp.clone().multiply(sin * radius));
                        Location particleLocation = center.clone().add(offset);

                        SkillUtils.spawnParticle(SkillUtils.getAudienceTargets(data, audienceTargeter), particleType,
                                particleLocation, 0, 0, 0, speed.get(data), particleCount.get(data));
                        SkillUtils.castSkillAtPoint(data, particleLocation, skillName.get(data));
                    },
                    (long) (delayMs.get(data) * i / 50)
            );
        }
        return SkillResult.SUCCESS;
    }
}