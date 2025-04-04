package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import me.bedwarshurts.mmextension.mechanics.inventory.RestoreHotbarMechanic;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
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

import java.util.UUID;

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
