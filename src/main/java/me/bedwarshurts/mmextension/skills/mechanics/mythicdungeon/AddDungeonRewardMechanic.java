/*
package me.bedwarshurts.mmextension.skills.mechanics.mythicdungeon;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.ItemUtils;
import net.playavalon.mythicdungeons.MythicDungeons;
import net.playavalon.mythicdungeons.player.MythicPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(author = "bedwarshurts", name = "adddungeonreward", aliases = {"addreward"}, description = "Adds a dungeon reward to the target entity's loot table")
public class AddDungeonRewardMechanic implements ITargetedEntitySkill {
    private final ItemStack item;

    public AddDungeonRewardMechanic(MythicLineConfig mlc) {
        this.item = ItemUtils.getItemStack(mlc.getString("item", "DIAMOND_SWORD"));
        if (this.item == null) {
            throw new IllegalArgumentException("Invalid item specified in adddungeonreward mechanic: " + mlc.getString("item"));
        }
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (!target.isPlayer()) return SkillResult.INVALID_TARGET;

        MythicPlayer mythicPlayer = MythicDungeons.inst().getMythicPlayer((Player) target.getBukkitEntity());
        mythicPlayer.addReward(item);

        return SkillResult.SUCCESS;
    }
}
*/