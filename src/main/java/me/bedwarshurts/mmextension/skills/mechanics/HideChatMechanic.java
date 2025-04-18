package me.bedwarshurts.mmextension.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import me.bedwarshurts.mmextension.utils.events.Events;
import me.bedwarshurts.mmextension.utils.terminable.TerminableConsumer;
import me.bedwarshurts.mmextension.utils.terminable.TerminableRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;

import java.util.concurrent.ConcurrentHashMap;

@MythicMechanic(author = "bedwarshurts", name = "hidechat", description = "Hide the chat for a player")
public class HideChatMechanic implements ITargetedEntitySkill, TerminableConsumer {
    private final PlaceholderInt duration;
    private final TerminableRegistry consumer = new TerminableRegistry();

    public static ConcurrentHashMap<Player, TerminableRegistry> playerChatList = new ConcurrentHashMap<>();

    public HideChatMechanic(MythicLineConfig mlc) {
        this.duration = PlaceholderInt.of(String.valueOf(mlc.getInteger(new String[]{"duration", "d"}, 0)));
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (!target.isPlayer()) return SkillResult.INVALID_TARGET;

        if (playerChatList.containsKey((Player) target.getBukkitEntity())) {
            playerChatList.get((Player) target.getBukkitEntity()).close();
            playerChatList.remove((Player) target.getBukkitEntity());
            return SkillResult.SUCCESS;
        }

        Events.subscribe(AsyncChatEvent.class, EventPriority.NORMAL)
                .filter(event -> event.getPlayer().getUniqueId().equals(target.getUniqueId()))
                .handler(event -> {
                    if (event.getPlayer().hasPermission("alchemist.bypasshiddenchat")) return;

                    event.viewers().remove(target.getBukkitEntity());
                })
                .bindWith(this);

        Bukkit.getScheduler().runTaskLaterAsynchronously(AlchemistMMExtension.inst(), consumer::close, duration.get(data));

        return SkillResult.SUCCESS;
    }

    @Override
    public TerminableConsumer with(AutoCloseable terminable) {
        return consumer.with(terminable);
    }
}
