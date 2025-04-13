package me.bedwarshurts.mmextension.skills.mechanics;

import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.events.Events;
import me.bedwarshurts.mmextension.utils.terminable.TerminableConsumer;
import me.bedwarshurts.mmextension.utils.terminable.TerminableRegistry;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;

@MythicMechanic(name = "mirrorplayerskin", aliases = {"mirrorskin"}, description = "Mirror the skin of the player to the entity")
public class MirrorPlayerSkinMechanic implements INoTargetSkill, TerminableConsumer {
    private final TerminableRegistry consumer = new TerminableRegistry();
    private static final HashSet<SkillCaster> disguisedCasters = new HashSet<>();

    @Override
    public SkillResult cast(SkillMetadata data) {
        SkillCaster caster = data.getCaster();
        LivingEntity casterEntity = (LivingEntity) caster.getEntity().getBukkitEntity();

        if (disguisedCasters.contains(caster)) {
            DisguiseAPI.undisguiseToAll(casterEntity);
            consumer.close();
            return SkillResult.SUCCESS;
        }
        if (DisguiseAPI.isDisguised(casterEntity)) {
            DisguiseAPI.undisguiseToAll(casterEntity);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerDisguise playerDisguise = new PlayerDisguise(player.getName());
            playerDisguise.setDynamicName(true);
            DisguiseAPI.disguiseToPlayers(casterEntity, playerDisguise, player);
        }

        Events.subscribe(PlayerJoinEvent.class, EventPriority.NORMAL)
                .handler(event -> {
                    PlayerDisguise playerDisguise = new PlayerDisguise(event.getPlayer().getName());
                    playerDisguise.setDynamicName(true);
                    DisguiseAPI.disguiseToPlayers(casterEntity, playerDisguise, event.getPlayer().getName());
                })
                .bindWith(this);

        disguisedCasters.add(caster);

        return SkillResult.SUCCESS;
    }

    public TerminableConsumer with(AutoCloseable terminable) {
        this.consumer.with(terminable);
        return this;
    }
}