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
        ItemStack stack = createItem(itemString);
        if (stack == null) return SkillResult.ERROR;

        ItemMeta meta = stack.getItemMeta();
        NamespacedKey key = new NamespacedKey(JavaPlugin.getProvidingPlugin(getClass()), "caster");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, data.getCaster().getEntity().getUniqueId().toString());

        player.getInventory().setItem(inventorySlot, stack);
        return SkillResult.SUCCESS;
    }

    private ItemStack createItem(String input) {
        String materialPart = input.contains("[") ? input.substring(0, input.indexOf("[")).trim() : input.trim();
        String bracketContent = input.contains("[") ? input.substring(input.indexOf("[") + 1, input.lastIndexOf("]")) : "";
        Map<String, String> infoMap = ItemUtils.parse(bracketContent);

        Material material = Material.matchMaterial(materialPart.toUpperCase());
        if (material == null) material = Material.BARRIER;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        // Name
        String nameValue = infoMap.getOrDefault("name", "");
        if (!nameValue.isEmpty()) {
            meta.displayName(MiniMessage.miniMessage().deserialize(nameValue));
        }

        // Lore
        String loreValue = infoMap.getOrDefault("lore", "");
        if (!loreValue.isEmpty()) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : loreValue.split("\\\\n")) {
                if (!line.isEmpty()) {
                    loreComponents.add(MiniMessage.miniMessage().deserialize(line));
                }
            }
            meta.lore(loreComponents);
        }

        // Custom model data
        String cmdValue = infoMap.getOrDefault("customModelData", "");
        if (!cmdValue.isEmpty()) {
            try {
                meta.setCustomModelData(Integer.parseInt(cmdValue));
            } catch (NumberFormatException ignored) { }
        }

        // Enchants
        String enchantsValue = infoMap.getOrDefault("enchants", "");
        if (!enchantsValue.isEmpty()) {
            String[] pairs = enchantsValue.split(",");
            for (String pair : pairs) {
                String[] parts = pair.split(":");
                if (parts.length == 2) {
                    Enchantment enchant = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(parts[0].toLowerCase()));
                    int level = Integer.parseInt(parts[1]);
                    if (enchant != null) {
                        meta.addEnchant(enchant, level, true);
                    }
                }
            }
        }

        // Skills
        String skillValue = infoMap.getOrDefault("skill", "");
        if (!skillValue.isEmpty()) {
            NamespacedKey key = new NamespacedKey(JavaPlugin.getProvidingPlugin(getClass()), "skill");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, skillValue);
        }
        item.setItemMeta(meta);
        return item;
    }
}