package me.bedwarshurts.mmextension.mechanics.inventory;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@MythicMechanic(author = "bedwarshurts", name = "randomizehotbar", aliases = {}, description = "Randomizes each targeted player's hotbar.")
public class RandomizeHotbarMechanic implements INoTargetSkill {

    @Override
    public SkillResult cast(SkillMetadata data) {
        boolean success = false;
        for (AbstractEntity target : data.getEntityTargets()) {
            if (!target.isPlayer()) continue;
            success = true;
            Player player = (Player) target.getBukkitEntity();
            ItemStack[] contents = player.getInventory().getContents();

            List<ItemStack> hotbar = new ArrayList<>(Arrays.asList(contents).subList(0, 9));
            Collections.shuffle(hotbar);
            for (int i = 0; i < 9; i++) {
                contents[i] = hotbar.get(i);
            }
            player.getInventory().setContents(contents);
        }
        return success ? SkillResult.SUCCESS : SkillResult.INVALID_TARGET;
    }
}