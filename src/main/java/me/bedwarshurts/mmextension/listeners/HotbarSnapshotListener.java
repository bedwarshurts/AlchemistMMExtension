package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import me.bedwarshurts.mmextension.mechanics.inventory.HotbarSnapshotMechanic;
import me.bedwarshurts.mmextension.mechanics.inventory.InventorySerializer;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class HotbarSnapshotListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> {
            if (!player.isOnline()) return;

            Optional<PlayerData> optionalMythicPlayer = MythicBukkit.inst().getPlayerManager().getProfile(player.getUniqueId());
            if (optionalMythicPlayer.isEmpty()) return;

            PlayerData mythicPlayer = optionalMythicPlayer.get();

            if (!mythicPlayer.getVariables().has("originalHotbar")) return;

            try {
                ItemStack[] originalHotbar = InventorySerializer.fromBase64(mythicPlayer.getVariables().get("originalHotbar").toString());

                for (int slot = 0; slot < 9; slot++) {
                    player.getInventory().setItem(slot, originalHotbar[slot]);
                    HotbarSnapshotMechanic.skillItems.remove(originalHotbar[slot]);
                }

                mythicPlayer.getVariables().remove("originalHotbar");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, 20);
    }

    @EventHandler
    public void onInteractWithItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!HotbarSnapshotMechanic.skillItems.containsKey(item)) return;

        SkillMetadata data = new SkillMetadataImpl(SkillTriggers.API, new GenericCaster(BukkitAdapter.adapt(player)), BukkitAdapter.adapt(player));
        SkillUtils.castSkill(MythicBukkit.inst().getSkillManager(), data, HotbarSnapshotMechanic.skillItems.get(item));
    }
}
