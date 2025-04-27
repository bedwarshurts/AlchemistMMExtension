package me.bedwarshurts.mmextension.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import me.bedwarshurts.mmextension.mythic.MythicPlayer;
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
    private final boolean remove;
    private final PlaceholderInt timeTicks;
    private final PlaceholderDouble damageAmount;
    private final PlaceholderDouble damageBuffer;
    private final PlaceholderInt warningTicks;
    private final PlaceholderInt warningDistance;

    public SetWorldBorderMechanic(MythicLineConfig mlc) {
        this.radius = mlc.getPlaceholderDouble(new String[]{"radius", "r"}, "0");
        this.remove = mlc.getBoolean(new String[]{"remove"}, false);
        this.damageAmount = mlc.getPlaceholderDouble(new String[]{"damageAmount", "da"}, 0.2);
        this.damageBuffer = mlc.getPlaceholderDouble(new String[]{"damageBuffer", "db"}, 2);
        this.warningTicks = mlc.getPlaceholderInteger(new String[]{"warningTicks", "wt"}, 15);
        this.warningDistance = mlc.getPlaceholderInteger(new String[]{"warningDistance", "wd"}, 5);
        this.cancelOnQuit = mlc.getBoolean(new String[]{"cancelOnQuit", "coq"}, true);
        this.timeTicks = mlc.getPlaceholderInteger(new String[]{"time", "ticks", "t"}, 0);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (!target.isPlayer()) return SkillResult.INVALID_TARGET;
        Player player = (Player) target.getBukkitEntity();

        if (player.getWorldBorder() != null) {
            if (remove) {
                player.setWorldBorder(null);
                if (MythicPlayer.getMythicPlayer(player).removeTracker("worldBorder") != null) {
                    MythicPlayer.getMythicPlayer(player).removeTracker("worldBorder").close();
                }
            } else {
                player.getWorldBorder().setSize(radius.get(data), timeTicks.get(data, data.getCaster().getEntity()) / 20);
            }
            return SkillResult.SUCCESS;
        }

        WorldBorder border = Bukkit.createWorldBorder();
        border.setCenter(BukkitAdapter.adapt(data.getCaster().getLocation()));
        border.setWarningTime(warningTicks.get(data, data.getCaster().getEntity()) / 20);
        border.setDamageBuffer(damageBuffer.get(data, data.getCaster().getEntity()));
        border.setWarningDistance(warningDistance.get(data, data.getCaster().getEntity()));
        border.setDamageAmount(damageAmount.get(data, data.getCaster().getEntity()));
        border.setSize(radius.get(data));

        player.setWorldBorder(border);

        if (!cancelOnQuit) {
            Events.subscribe(PlayerJoinEvent.class, EventPriority.NORMAL)
                    .filter(e -> e.getPlayer().getUniqueId().toString().equals(player.getUniqueId().toString()))
                    .handler(e -> Bukkit.getScheduler().runTaskLater(AlchemistMMExtension.inst(),
                            () -> e.getPlayer().setWorldBorder(border), 40L))
                    .bindWith(MythicPlayer.getMythicPlayer(player).addTracker("worldBorder"));
        }
        return SkillResult.SUCCESS;
    }
}
