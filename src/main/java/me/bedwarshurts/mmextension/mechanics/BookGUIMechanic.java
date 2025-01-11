package me.bedwarshurts.mmextension.mechanics;

import me.bedwarshurts.mmextension.utils.Utilities;
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
    public SkillResult cast(SkillMetadata skillMetadata) {
        String parsedContents = Utilities.parseStringPlaceholders(contents, skillMetadata);
        parsedContents = Utilities.parseMythicTags(parsedContents);
        String[] pages = parsedContents.split("\\n");
        Component[] components = new Component[pages.length];
        MiniMessage miniMessage = MiniMessage.miniMessage();

        for (int i = 0; i < pages.length; i++) {
            components[i] = miniMessage.deserialize(pages[i]);
        }

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        meta.addPages(components);
        meta.setTitle(title);
        meta.setAuthor(author);

        book.setItemMeta(meta);

        for (AbstractEntity target : skillMetadata.getEntityTargets()) {
            if (target.isPlayer()) {
                Player player = (Player) target.getBukkitEntity();
                player.openBook(book);
            }
        }

        return SkillResult.SUCCESS;
    }
}