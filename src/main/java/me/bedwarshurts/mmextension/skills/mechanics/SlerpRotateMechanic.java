package me.bedwarshurts.mmextension.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;

@MythicMechanic(author = "bedwarshurts", name = "lerprotate", aliases = {"slerp"}, description = "Interpolates Display rotation via setTransformation.")
public class SlerpRotateMechanic implements INoTargetSkill {

    private final PlaceholderDouble rotPitchDeg;
    private final PlaceholderDouble rotYawDeg;
    private final PlaceholderDouble rotRollDeg;
    private final int durationTicks;
    private final String mode;

    public SlerpRotateMechanic(MythicLineConfig mlc) {
        this.rotPitchDeg = mlc.getPlaceholderDouble("x", "0");
        this.rotYawDeg = mlc.getPlaceholderDouble("y", "0");
        this.rotRollDeg = mlc.getPlaceholderDouble("z", "0");
        this.durationTicks = mlc.getInteger("ticks", 20);
        this.mode = mlc.getString("mode", "ADD").toUpperCase();
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        Entity ent = data.getCaster().getEntity().getBukkitEntity();
        if (!(ent instanceof Display display)) {
            return SkillResult.CONDITION_FAILED;
        }

        float dx = (float) Math.toRadians(rotPitchDeg.get(data.getCaster()));
        float dy = (float) Math.toRadians(rotYawDeg.get(data.getCaster()));
        float dz = (float) Math.toRadians(rotRollDeg.get(data.getCaster()));

        Transformation original = display.getTransformation();
        Quaternionf startQuat = mode.equals("SET")
                ? new Quaternionf()
                : new Quaternionf(original.getRightRotation());
        Quaternionf endQuat = new Quaternionf(startQuat)
                .rotateX(dx)
                .rotateY(dy)
                .rotateZ(dz)
                .normalize();

        if (durationTicks <= 0) {
            applyTransformation(display, endQuat);
            return SkillResult.SUCCESS;
        }

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick > durationTicks || display.isDead()) {
                    cancel();
                    return;
                }
                float t = tick / (float) durationTicks;
                Quaternionf interp = new Quaternionf(startQuat)
                        .slerp(endQuat, t, new Quaternionf())
                        .normalize();
                applyTransformation(display, interp);
                tick++;
            }
        }.runTaskTimer(AlchemistMMExtension.inst(), 0L, 1L);

        return SkillResult.SUCCESS;
    }

    private void applyTransformation(Display display, Quaternionf rightQuat) {
        Transformation cur = display.getTransformation();
        Transformation next = new Transformation(
                cur.getTranslation(),
                cur.getLeftRotation(),
                cur.getScale(),
                rightQuat
        );
        display.setTransformation(next);
    }
}