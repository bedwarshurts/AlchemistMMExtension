package me.bedwarshurts.mmextension.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@MythicMechanic(author = "bedwarshurts", name = "cancelplayerdeath", aliases = {}, description = "Cancels the player's next death if activated")
public class CancelPlayerDeathMechanic implements ITargetedEntitySkill, Listener {
    private final Set<UUID> playersWithCancelDeath = new HashSet<>();
    private final double healthPercentage;
    private final String skillName;
    private SkillMetadata data;

    public CancelPlayerDeathMechanic(MythicLineConfig mlc) {
        this.healthPercentage = mlc.getDouble("healthPercentage", 100.0);
        this.skillName = mlc.getString("skill", "");

        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getProvidingPlugin(getClass()));
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        this.data = data;
        if (target.getBukkitEntity() instanceof Player player) {
            if (playersWithCancelDeath.contains(player.getUniqueId())) {
                playersWithCancelDeath.remove(player.getUniqueId());
            } else {
                playersWithCancelDeath.add(player.getUniqueId());
            }
            return SkillResult.SUCCESS;
        }
        return SkillResult.CONDITION_FAILED;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (playersWithCancelDeath.contains(player.getUniqueId())) {
            event.setCancelled(true);
            playersWithCancelDeath.remove(player.getUniqueId());
            double newHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue() * (healthPercentage / 100.0);
            player.setHealth(Math.min(newHealth, Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue()));

            if (!skillName.isEmpty()) {
                Optional<Skill> skillOptional = MythicBukkit.inst().getSkillManager().getSkill(skillName);
                if (skillOptional.isPresent()) {
                    Skill skill = skillOptional.get();
                    skill.execute(data.deepClone().setEntityTarget(BukkitAdapter.adapt(player)));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        playersWithCancelDeath.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playersWithCancelDeath.remove(player.getUniqueId());
    }
}