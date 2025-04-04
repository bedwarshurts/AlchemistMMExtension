package me.bedwarshurts.mmextension.listeners;

import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class InventoryItemListener implements Listener {
    private final NamespacedKey skillKey = new NamespacedKey(JavaPlugin.getProvidingPlugin(getClass()), "skill");
    private final NamespacedKey casterKey = new NamespacedKey(JavaPlugin.getProvidingPlugin(getClass()), "caster");

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        SkillUtils.castItemSkill(event, skillKey, casterKey);
    }
}