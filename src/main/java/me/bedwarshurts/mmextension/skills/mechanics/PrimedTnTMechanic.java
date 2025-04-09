package me.bedwarshurts.mmextension.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import me.bedwarshurts.mmextension.listeners.TnTExplosionListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.plugin.java.JavaPlugin;

@MythicMechanic(author = "bedwarshurts", name = "primedtnt", aliases = {}, description = "Spawns a primed TNT at a specific location with break and damage options")
public class PrimedTnTMechanic implements ITargetedLocationSkill {

    private final boolean breakBlocks;
    private final int fuseTicks;
    private final boolean damage;

    public PrimedTnTMechanic(MythicLineConfig mlc) {
        Bukkit.getPluginManager().registerEvents(new TnTExplosionListener(), AlchemistMMExtension.inst());

        this.breakBlocks = mlc.getBoolean("break", false);
        this.fuseTicks = mlc.getInteger("fuse", 80);
        this.damage = mlc.getBoolean("damage", false);
    }

    @Override
    public SkillResult castAtLocation(SkillMetadata data, AbstractLocation targetLocation) {
        Location location = targetLocation.toPosition().toLocation();
        spawnPrimedTNT(location);
        return SkillResult.SUCCESS;
    }

    private void spawnPrimedTNT(Location location) {
        TNTPrimed tnt = location.getWorld().spawn(location, TNTPrimed.class);
        tnt.setIsIncendiary(false);
        tnt.setYield(4.0F);
        tnt.setFuseTicks(fuseTicks);

        if (!breakBlocks) {
            tnt.setMetadata("noBreak", new FixedMetadataValue(JavaPlugin.getProvidingPlugin(getClass()), true));
        }

        if (!damage) {
            tnt.setMetadata("noDamage", new FixedMetadataValue(JavaPlugin.getProvidingPlugin(getClass()), true));
        }
    }
}