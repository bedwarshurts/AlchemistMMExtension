package me.bedwarshurts.mmextension.mechanics.inventory;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import me.bedwarshurts.mmextension.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class PlaceToInventoryMechanic implements ITargetedEntitySkill {
    private final String itemString;
    private final int inventorySlot;

    public PlaceToInventoryMechanic(MythicLineConfig mlc) {
        this.itemString = mlc.getString("item", "stone[name=,lore=,enchants=,custommodeldata=,skill=]");
        this.inventorySlot = mlc.getInteger("inventorySlot", 0);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (!target.isPlayer()) return SkillResult.CONDITION_FAILED;

        Player player = (Player) target.getBukkitEntity();
        ItemStack stack = ItemUtils.buildItem(itemString);
        if (stack == null) return SkillResult.ERROR;

        ItemMeta meta = stack.getItemMeta();
        NamespacedKey key = new NamespacedKey(JavaPlugin.getProvidingPlugin(getClass()), "caster");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, data.getCaster().getEntity().getUniqueId().toString());

        player.getInventory().setItem(inventorySlot, stack);
        return SkillResult.SUCCESS;
    }
}