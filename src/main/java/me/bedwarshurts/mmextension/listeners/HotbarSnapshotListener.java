package me.bedwarshurts.mmextension.listeners;

import me.bedwarshurts.mmextension.skills.mechanics.inventory.RestoreHotbarMechanic;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class HotbarSnapshotListener implements Listener {
    private final NamespacedKey skillKey = new NamespacedKey(JavaPlugin.getProvidingPlugin(getClass()), "skill");
    private final NamespacedKey casterKey = new NamespacedKey(JavaPlugin.getProvidingPlugin(getClass()), "caster");
    private final NamespacedKey key = new NamespacedKey(JavaPlugin.getProvidingPlugin(getClass()), "hotbarsnapshot");


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> {
            if (!player.isOnline()) return;

            RestoreHotbarMechanic.restoreHotbar(player);
        }, 20);
    }

    @EventHandler
    public void onInteractWithItem(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (!event.getItem().getPersistentDataContainer().has(key, PersistentDataType.STRING)) return;

        SkillUtils.castItemSkill(event, skillKey, casterKey);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        ItemMeta meta = event.getCurrentItem().getItemMeta();
        if (meta == null) return;

        event.setCancelled(cancelInteraction(meta));
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        ItemMeta meta = event.getItemDrop().getItemStack().getItemMeta();
        if (meta == null) return;

        event.setCancelled(cancelInteraction(meta));
    }

    private boolean cancelInteraction(ItemMeta meta) {
        return meta.getPersistentDataContainer().has(key, PersistentDataType.STRING);

    }
}
