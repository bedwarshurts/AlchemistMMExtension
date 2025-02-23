package me.bedwarshurts.mmextension.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

@MythicMechanic(author = "bedwarshurts", name = "hotbarsnapshot", aliases = {}, description = "Saves and replaces a player's hotbar for a duration")
public class HotbarSnapshotMechanic implements INoTargetSkill {

    private final ItemStack[] replacementItems;
    private final int durationTicks;

    public HotbarSnapshotMechanic(MythicLineConfig mlc) {
        String itemsArg = mlc.getString("items", "air");
        String[] parts = itemsArg.split(",");
        this.replacementItems = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            if (i < parts.length) {
                Material mat = Material.matchMaterial(parts[i].trim().toUpperCase());
                replacementItems[i] = mat != null ? new ItemStack(mat) : new ItemStack(Material.AIR);
            } else {
                replacementItems[i] = new ItemStack(Material.AIR);
            }
        }
        this.durationTicks = mlc.getInteger("duration", 60);
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        boolean success = false;
        for (AbstractEntity target : data.getEntityTargets()) {
            if (!target.isPlayer()) continue;
            Player player = BukkitAdapter.adapt(target.asPlayer());
            success = true;

            ItemStack[] originalHotbar = player.getInventory().getContents();

            for (int slot = 0; slot < 9; slot++) {
                player.getInventory().setItem(slot, replacementItems[slot]);
            }

            Bukkit.getScheduler().runTaskLater(
                    JavaPlugin.getProvidingPlugin(getClass()),
                    () -> {
                        for (int slot = 0; slot < 9; slot++) {
                            player.getInventory().setItem(slot, originalHotbar[slot]);
                        }
                    }, durationTicks
            );
        }
        return success ? SkillResult.SUCCESS : SkillResult.INVALID_TARGET;
    }
}