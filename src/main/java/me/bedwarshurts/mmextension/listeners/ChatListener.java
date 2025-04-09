package me.bedwarshurts.mmextension.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.bedwarshurts.mmextension.skills.mechanics.HideChatMechanic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        if (event.getPlayer().hasPermission("alchemist.bypasshiddenchat")) return;

        for (Player player : HideChatMechanic.playerChatList) {
            event.viewers().remove(player);
        }
    }
}
