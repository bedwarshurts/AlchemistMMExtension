package me.bedwarshurts.mmextension.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.listeners.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

import static me.bedwarshurts.mmextension.AlchemistMMExtension.plugin;

@MythicMechanic(author = "bedwarshurts", name = "hidechat", description = "Hide the chat for a player")
public class HideChatMechanic implements ITargetedEntitySkill {
    private final PlaceholderInt duration;

    public static HashSet<Player> playerChatList = new HashSet<>();

    public HideChatMechanic(MythicLineConfig mlc) {
        Bukkit.getPluginManager().registerEvents(new ChatListener(), plugin);

        this.duration = PlaceholderInt.of(String.valueOf(mlc.getInteger(new String[]{"duration", "d"}, 0)));
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (!target.isPlayer()) return SkillResult.INVALID_TARGET;

        if (playerChatList.contains((Player) target.getBukkitEntity())) {
            playerChatList.remove((Player) target.getBukkitEntity());
        } else {
            playerChatList.add((Player) target.getBukkitEntity());
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getProvidingPlugin(getClass()), () -> {
            playerChatList.remove((Player) target.getBukkitEntity());
        }, duration.get(data));

        return SkillResult.SUCCESS;
    }
}
