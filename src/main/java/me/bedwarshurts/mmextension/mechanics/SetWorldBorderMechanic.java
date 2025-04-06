package me.bedwarshurts.mmextension.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

@MythicMechanic(author = "bedwarshurts", name = "setworldborder", aliases = {"swb"}, description = "Sets the world border for a player")
public class SetWorldBorderMechanic implements ITargetedEntitySkill {
    private final PlaceholderDouble radius;

    public SetWorldBorderMechanic(MythicLineConfig mlc) {
        radius = mlc.getPlaceholderDouble(new String[]{"radius", "r"}, "0");
    }
    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (!target.isPlayer()) return SkillResult.INVALID_TARGET;

        Player player = (Player) target.getBukkitEntity();

        if (player.getWorldBorder() != null) {
            player.setWorldBorder(null);
            return SkillResult.SUCCESS;
        }
        WorldBorder border = Bukkit.createWorldBorder();
        border.setCenter(player.getLocation());
        border.setSize(radius.get(data));

        player.setWorldBorder(border);
        return SkillResult.SUCCESS;
    }
}
