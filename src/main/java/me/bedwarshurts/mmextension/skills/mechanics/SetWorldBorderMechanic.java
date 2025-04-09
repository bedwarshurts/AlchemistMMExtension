package me.bedwarshurts.mmextension.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.events.EventSubscriptionBuilder;
import me.bedwarshurts.mmextension.utils.events.Events;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

@MythicMechanic(author = "bedwarshurts", name = "setworldborder", aliases = {"swb"}, description = "Sets the world border for a player")
public class SetWorldBorderMechanic implements ITargetedEntitySkill {
    private final PlaceholderDouble radius;
    private final boolean cancelOnQuit;
    private EventSubscriptionBuilder<?> subscription;

    public SetWorldBorderMechanic(MythicLineConfig mlc) {
        this.radius = mlc.getPlaceholderDouble(new String[]{"radius", "r"}, "0");
        this.cancelOnQuit = mlc.getBoolean(new String[]{"cancelOnQuit", "coq"}, true);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (!target.isPlayer()) return SkillResult.INVALID_TARGET;
        Player player = (Player) target.getBukkitEntity();

        if (player.getWorldBorder() != null) {
            player.setWorldBorder(null);
            subscription.unsubscribe();

            return SkillResult.SUCCESS;
        }

        WorldBorder border = Bukkit.createWorldBorder();
        border.setCenter(player.getLocation());
        border.setSize(radius.get(data));

        player.setWorldBorder(border);

        if (!cancelOnQuit) {
            subscription = Events.subscribe(PlayerJoinEvent.class, EventPriority.NORMAL)
                    .filter(e -> e.getPlayer().getUniqueId().equals(player.getUniqueId()))
                    .handler(e -> player.setWorldBorder(border));
        }
        return SkillResult.SUCCESS;
    }
}
