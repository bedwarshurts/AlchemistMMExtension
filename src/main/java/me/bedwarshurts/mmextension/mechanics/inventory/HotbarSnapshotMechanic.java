package me.bedwarshurts.mmextension.mechanics.inventory;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@MythicMechanic(author = "bedwarshurts", name = "hotbarsnapshot", aliases = {}, description = "Saves and replaces a player's hotbar for a duration")
public class HotbarSnapshotMechanic implements INoTargetSkill {

    private final TemporaryInventoryItem[] replacementItems;
    private final int durationTicks;
    private final String itemsArg;

    public static final Map<Player, TemporaryInventoryItem[]> activeTemporaryItems = new HashMap<>();

    public HotbarSnapshotMechanic(MythicLineConfig mlc) {
        this.itemsArg = mlc.getString("items", "air");
        this.replacementItems = new TemporaryInventoryItem[9];
        this.durationTicks = mlc.getInteger("duration", 60);
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        String[] parts = itemsArg.split(",");
        for (int i = 0; i < 9; i++) {
            if (i < parts.length) {
                String itemName = parts[i].trim();
                String skillName = null;

                Pattern pattern = Pattern.compile("(\\w+)\\[(\\w+)]");
                Matcher matcher = pattern.matcher(parts[i]);
                if (matcher.find()) {
                    itemName = matcher.group(1);
                    skillName = matcher.group(2);
                }

                Material mat = Material.matchMaterial(itemName.toUpperCase());
                replacementItems[i] = mat != null ? new TemporaryInventoryItem(new ItemStack(mat)) :  new TemporaryInventoryItem(new ItemStack(Material.AIR));

                if (skillName != null) {
                   replacementItems[i].setSkill(skillName);
                }
            } else {
                replacementItems[i] = new TemporaryInventoryItem(new ItemStack(Material.AIR));
            }
        }

        boolean success = false;
        for (AbstractEntity target : data.getEntityTargets()) {
            if (!target.isPlayer()) continue;
            success = true;

            Player player = BukkitAdapter.adapt(target.asPlayer());

            PlayerData mythicPlayer = SkillUtils.getMythicPlayer(player);
            if (mythicPlayer == null) continue;

            try {
                mythicPlayer.getVariables().put("originalHotbar", new StringVariable(InventorySerializer.toBase64(player.getInventory().getContents())));
            } catch (IOException ignored) {
            }

            for (int slot = 0; slot < 9; slot++) {
                player.getInventory().setItem(slot, replacementItems[slot].getItem());
            }

            activeTemporaryItems.put(player, replacementItems);

            Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> {
                try {
                    if (!player.isOnline()) return;

                    ItemStack[] originalHotbar = InventorySerializer.fromBase64(mythicPlayer.getVariables().get("originalHotbar").toString());
                    for (int slot = 0; slot < 9; slot++) {
                        player.getInventory().setItem(slot, originalHotbar[slot]);
                        activeTemporaryItems.remove(player);
                    }
                    mythicPlayer.getVariables().remove("originalHotbar");
                } catch (Exception ignored) {
                }
            }, durationTicks);
        }
        return success ? SkillResult.SUCCESS : SkillResult.INVALID_TARGET;
    }
}