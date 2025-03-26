package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import me.bedwarshurts.mmextension.mechanics.canceldeath.CancelPlayerDeathMechanic;
import me.bedwarshurts.mmextension.mechanics.canceldeath.PlayerDeathData;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public class PlayerDeathListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!CancelPlayerDeathMechanic.playerDeathDatas.containsKey(player.getUniqueId())) return;
        event.setCancelled(true);

        PlayerDeathData deathData = CancelPlayerDeathMechanic.playerDeathDatas.remove(player.getUniqueId());

        double newHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue() * (deathData.getHealthPercentage() / 100.0);
        player.setHealth(Math.min(newHealth, Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue()));

        SkillMetadata data = deathData.getData();
        data.setTrigger(BukkitAdapter.adapt(player));

        MythicSkill skill = new MythicSkill(deathData.getSkill());
        skill.cast(data);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        CancelPlayerDeathMechanic.playerDeathDatas.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CancelPlayerDeathMechanic.playerDeathDatas.remove(player.getUniqueId());
    }
}
