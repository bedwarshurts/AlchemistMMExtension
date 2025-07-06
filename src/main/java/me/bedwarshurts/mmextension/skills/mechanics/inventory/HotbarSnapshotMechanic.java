package me.bedwarshurts.mmextension.skills.mechanics.inventory;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import me.bedwarshurts.mmextension.listeners.HotbarSnapshotListener;
import me.bedwarshurts.mmextension.utils.ItemUtils;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.ArrayList;

@MythicMechanic(author = "bedwarshurts", name = "hotbarsnapshot", aliases = {}, description = "Saves and replaces a player's hotbar for a duration")
public class HotbarSnapshotMechanic implements INoTargetSkill {

    private final ItemStack[] replacementItems;
    private final int durationTicks;
    private final String itemsArg;

    static {
        Bukkit.getPluginManager().registerEvents(new HotbarSnapshotListener(), AlchemistMMExtension.inst());
    }

    public HotbarSnapshotMechanic(MythicLineConfig mlc) {
        this.itemsArg = mlc.getString("items", "air");
        this.replacementItems = new ItemStack[9];
        this.durationTicks = mlc.getInteger("duration", 60);
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        ArrayList<String> items = ItemUtils.splitItems(itemsArg);
        for (int i = 0; i < 9; i++) {
            if (i < items.size()) {
                ItemStack item = ItemUtils.buildItem(items.get(i) + "]");
                ItemMeta meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(
                        new NamespacedKey(AlchemistMMExtension.inst(), "caster"),
                        PersistentDataType.STRING,
                        data.getCaster().getEntity().getUniqueId().toString()
                );
                meta.getPersistentDataContainer().set(
                        new NamespacedKey(AlchemistMMExtension.inst(), "hotbarsnapshot"),
                        PersistentDataType.STRING,
                        "true"
                );
                replacementItems[i] = item;
            } else {
                replacementItems[i] = new ItemStack(Material.AIR);
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
                player.getInventory().setItem(slot, replacementItems[slot]);
            }

            Bukkit.getScheduler().runTaskLater(AlchemistMMExtension.inst(), () -> {
                if (!player.isOnline()) return;
                RestoreHotbarMechanic.restoreHotbar(player);
            }, durationTicks);
        }
        return success ? SkillResult.SUCCESS : SkillResult.INVALID_TARGET;
    }
}