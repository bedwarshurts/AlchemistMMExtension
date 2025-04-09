package me.bedwarshurts.mmextension.skills.mechanics;

import me.bedwarshurts.mmextension.utils.PlaceholderUtils;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.Material;
import me.clip.placeholderapi.PlaceholderAPI;

@MythicMechanic(author = "bedwarshurts", name = "bookgui", aliases = {}, description = "Opens a book GUI for the target players with specified contents")
public class BookGUIMechanic implements INoTargetSkill {
    private final String contents;
    private final String title;
    private final String author;

    public BookGUIMechanic(MythicLineConfig config) {
        this.contents = config.getString("contents", "Alchemist - No Contents Specified");
        this.title = config.getString("title", "BuffsMenu");
        this.author = config.getString("author", "AlchemistNetwork");
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        String parsedContents = PlaceholderUtils.parseStringPlaceholders(contents, data);
        parsedContents = PlaceholderUtils.parseMythicTags(parsedContents);
        MiniMessage miniMessage = MiniMessage.miniMessage();
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        for (AbstractEntity target : data.getEntityTargets()) {
            if (!target.isPlayer()) continue;

            Player player = (Player) target.getBukkitEntity();
            parsedContents = PlaceholderAPI.setPlaceholders(player, parsedContents);
            String[] pages = parsedContents.split("\\\\n");
            Component[] components = new Component[pages.length];

            for (int i = 0; i < pages.length; i++) {
                components[i] = miniMessage.deserialize(pages[i]);
            }

            meta.addPages(components);
            meta.setTitle(title);
            meta.setAuthor(author);

            book.setItemMeta(meta);
            player.openBook(book);
        }

        return SkillResult.SUCCESS;
    }
}