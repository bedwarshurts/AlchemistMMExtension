package me.bedwarshurts.mmextension.skills.mechanics.chestgui;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.PlaceholderUtils;
import me.bedwarshurts.mmextension.listeners.ChestGUIListener;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@MythicMechanic(author = "bedwarshurts", name = "chestgui", description = "Opens a custom Chest GUI for target players")
public class ChestGUIMechanic implements INoTargetSkill {

    private final Component titleTemplate;
    private final int size;
    private final List<String> itemTemplates;

    static {
        Bukkit.getPluginManager().registerEvents(new ChestGUIListener(), AlchemistMMExtension.inst());
    }

    public ChestGUIMechanic(MythicLineConfig mlc) {
        String rawTitle = mlc.getString("title", "Alchemist Chest GUI");
        this.titleTemplate = MiniMessage.miniMessage().deserialize(rawTitle);
        this.size = Math.max(9, mlc.getInteger("slots", 9));

        String rawContents = mlc.getString("contents", "");
        this.itemTemplates = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int depth = 0;

        for (char c : rawContents.toCharArray()) {
            if (c == '[') depth++;
            if (c == ']') depth--;

            if (c == ',' && depth == 0) {
                itemTemplates.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        if (!sb.isEmpty())
            itemTemplates.add(sb.toString().trim());
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        for (AbstractEntity abstractEntity : data.getEntityTargets()) {
            if (!abstractEntity.isPlayer()) continue;
            Player player = (Player) abstractEntity.getBukkitEntity();

            String rawTitle = PlaceholderAPI.setPlaceholders(player, titleTemplate.toString());
            Component title = MiniMessage.miniMessage().deserialize(rawTitle);

            ChestGUIHolder holder = new ChestGUIHolder(data, size);
            Inventory inv = Bukkit.createInventory(holder, size, title);
            holder.setInventory(inv);

            for (String template : itemTemplates) {
                String replaced = PlaceholderAPI.setPlaceholders(player,
                        PlaceholderUtils.parseStringPlaceholders(template, data));

                ChestItem built = holder.addItemFromTemplate(replaced);
                if (built == null) continue;

                inv.setItem(built.slot(), built.stack());
            }

            player.openInventory(inv);
        }
        return SkillResult.SUCCESS;
    }
}
