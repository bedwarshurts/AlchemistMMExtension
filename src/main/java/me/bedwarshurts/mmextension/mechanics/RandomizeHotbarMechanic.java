package me.bedwarshurts.mmextension.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MythicMechanic(author = "bedwarshurts", name = "randomizehotbar", aliases = {}, description = "Randomizes each targeted player's hotbar.")
public class RandomizeHotbarMechanic implements INoTargetSkill {

    @Override
    public SkillResult cast(SkillMetadata data) {
        boolean affectedAnyPlayer = false;

        for (AbstractEntity target : data.getEntityTargets()) {
            if (target.isPlayer()) {
                affectedAnyPlayer = true;
                Player player = (Player) target.getBukkitEntity();
                List<ItemStack> hotbarItems = new ArrayList<>();

                for (int i = 0; i < 9; i++) {
                    hotbarItems.add(player.getInventory().getItem(i));
                }

                Collections.shuffle(hotbarItems);

                for (int i = 0; i < 9; i++) {
                    player.getInventory().setItem(i, hotbarItems.get(i));
                }
            }
        }

        return affectedAnyPlayer ? SkillResult.SUCCESS : SkillResult.CONDITION_FAILED;
    }
}