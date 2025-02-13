package me.bedwarshurts.mmextension.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@MythicMechanic(author = "bedwarshurts", name = "periodicblockbreak", aliases = {"periodicbreak"}, description = "Breaks blocks in a list of locations over time")
public class PeriodicBlockBreakMechanic implements INoTargetSkill {
    private final PlaceholderInt delayMs;
    private final Material blockType;
    private final PlaceholderDouble startX;
    private final PlaceholderDouble startY;
    private final PlaceholderDouble startZ;
    private final PlaceholderString skillName;
    private final SkillExecutor manager;

    public PeriodicBlockBreakMechanic(SkillExecutor manager, MythicLineConfig mlc) {
        this.delayMs = PlaceholderInt.of(mlc.getString(new String[]{"interval", "i"}, "0"));

        PlaceholderString block = PlaceholderString.of(mlc.getString("block", "AIR"));
        this.blockType = Material.valueOf(String.valueOf(block).toUpperCase());

        String[] coords = mlc.getString(new String[]{"startingLocation", "sLoc"},"0,0,0").split(",");
        this.startX = PlaceholderDouble.of(coords[0]);
        this.startY = PlaceholderDouble.of(coords[1]);
        this.startZ = PlaceholderDouble.of(coords[2]);

        this.skillName = PlaceholderString.of(mlc.getString(new String[]{"skill","onBlockBreak","oBB"}, ""));
        this.manager = manager;
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        Collection<AbstractLocation> locations = data.getLocationTargets();
        if (locations.isEmpty()) {
            return SkillResult.CONDITION_FAILED;
        }

        Location startingLocation = new Location(
                BukkitAdapter.adapt(data.getCaster().getLocation().getWorld()),
                startX.get(data),
                startY.get(data),
                startZ.get(data)
        );

        List<AbstractLocation> sortedLocations = locations.stream()
                .sorted(Comparator.comparingDouble(loc -> loc.distanceSquared(BukkitAdapter.adapt(startingLocation))))
                .toList();

        for (int i = 0; i < sortedLocations.size(); i++) {
            AbstractLocation abstractLocation = sortedLocations.get(i);
            Location location = BukkitAdapter.adapt(abstractLocation);
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> {
                        Block block = location.getBlock();
                        if (block.getType() != blockType) {
                            block.setType(blockType, false);
                            SkillUtils.castSkillAtPoint(data, location, skillName, manager);
                        }
                    }, ((long) delayMs.get(data) * i / 50)
            );
        }
        return SkillResult.SUCCESS;
    }
}