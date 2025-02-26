package me.bedwarshurts.mmextension.mechanics.inventory;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(author = "bedwarshurts", name = "restorehotbar", aliases = {}, description = "Restores a player's hotbar to its original state")
public class RestoreHotbarMechanic implements INoTargetSkill {

    @Override
    public SkillResult cast(SkillMetadata data) {
        boolean success = false;
        for (AbstractEntity target : data.getEntityTargets()) {
            if (!target.isPlayer()) continue;
            Player player = BukkitAdapter.adapt(target.asPlayer());

            if (!HotbarSnapshotMechanic.activeTemporaryItems.containsKey(player)) continue;

            PlayerData mythicPlayer = SkillUtils.getMythicPlayer(player);
            if (mythicPlayer == null) continue;
            if (!mythicPlayer.getVariables().has("originalHotbar")) continue;

            try {
                ItemStack[] originalHotbar = InventorySerializer.fromBase64(mythicPlayer.getVariables().get("originalHotbar").toString());
                for (int slot = 0; slot < 9; slot++) {
                    player.getInventory().setItem(slot, originalHotbar[slot]);
                }
                HotbarSnapshotMechanic.activeTemporaryItems.remove(BukkitAdapter.adapt(target.asPlayer()));
                mythicPlayer.getVariables().remove("originalHotbar");
                success = true;
            } catch (Exception ignored) {
            }
        }
        return success ? SkillResult.SUCCESS : SkillResult.INVALID_TARGET;
    }
}
