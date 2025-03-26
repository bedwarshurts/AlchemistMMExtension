package me.bedwarshurts.mmextension.mechanics.inventory;

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
        boolean success = false;

        for (AbstractEntity target : data.getEntityTargets()) {
            if (!target.isPlayer()) continue;
            Player player = (Player) target.getBukkitEntity();
            success = true;

            List<Integer> slots = new ArrayList<>();
            for (int i = 0; i < 9; i++) slots.add(i);
            Collections.shuffle(slots);

            ItemStack[] contents = player.getInventory().getContents();
            ItemStack[] shuffledHotbar = new ItemStack[9];
            for (int i = 0; i < 9; i++) {
                shuffledHotbar[i] = contents[slots.get(i)];
            }
            System.arraycopy(shuffledHotbar, 0, contents, 0, 9);
            player.getInventory().setContents(contents);
        }
        return success ? SkillResult.SUCCESS : SkillResult.INVALID_TARGET;
    }
}