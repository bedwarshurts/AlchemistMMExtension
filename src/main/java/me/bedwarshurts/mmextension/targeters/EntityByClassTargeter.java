package me.bedwarshurts.mmextension.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.targeters.IEntityTargeter;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static me.bedwarshurts.mmextension.AlchemistMMExtension.plugin;

@MythicTargeter(author = "bedwarshurts", name = "targetentitiesinradius", aliases = {"TPIR"}, description = "Targets a specific entity in a radius around the caster")
public class EntityByClassTargeter implements IEntityTargeter {
    private final PlaceholderDouble radius;
    private final String className;

    public EntityByClassTargeter(MythicLineConfig mlc) {
        this.radius = mlc.getPlaceholderDouble("radius", 5);
        this.className = mlc.getString("class", "org.bukkit.entity.Projectile");
    }

    @Override
    public Collection<AbstractEntity> getEntities(SkillMetadata data) {
        Location center = BukkitAdapter.adapt(data.getCaster().getLocation());
        double r = radius.get(data);

        try {
            return Bukkit.getScheduler().callSyncMethod(plugin, () ->
                    center.getWorld().getEntitiesByClass(Class.forName(className).asSubclass(Entity.class)).stream()
                            .filter(entity -> entity.getLocation().distanceSquared(center) <= r * r)
                            .map(BukkitAdapter::adapt)
                            .collect(Collectors.toList())
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}