package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import me.bedwarshurts.mmextension.mechanics.inventory.HotbarSnapshotMechanic;
import me.bedwarshurts.mmextension.mechanics.inventory.InventorySerializer;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class HotbarSnapshotListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> {
            if (!player.isOnline()) return;

            PlayerData mythicPlayer = SkillUtils.getMythicPlayer(player);
            if (mythicPlayer == null) return;

            if (!mythicPlayer.getVariables().has("originalHotbar")) return;

            try {
                ItemStack[] originalHotbar = InventorySerializer.fromBase64(mythicPlayer.getVariables().get("originalHotbar").toString());

                for (int slot = 0; slot < 9; slot++) {
                    player.getInventory().setItem(slot, originalHotbar[slot]);
                }

                HotbarSnapshotMechanic.activeTemporaryItems.remove(player);
                mythicPlayer.getVariables().remove("originalHotbar");
            } catch (Exception ignored) {
            }
        }, 20);
    }

    @EventHandler
    public void onInteractWithItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!HotbarSnapshotMechanic.activeTemporaryItems.containsKey(player)) return;

        int slot = player.getInventory().getHeldItemSlot();
        if (slot < 0 || slot > 8) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) return;

        event.setCancelled(true);

        SkillMetadata data = new SkillMetadataImpl(SkillTriggers.API, SkillUtils.getMythicPlayer(player), BukkitAdapter.adapt(player));
        SkillUtils.castSkill(data, HotbarSnapshotMechanic.activeTemporaryItems.get(player)[slot].getSkill());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!HotbarSnapshotMechanic.activeTemporaryItems.containsKey(player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!HotbarSnapshotMechanic.activeTemporaryItems.containsKey(player)) return;

        event.setCancelled(true);
    }
}
