package me.bedwarshurts.mmextension.skills.mechanics.spectatortarget;

import io.lumine.mythic.lib.message.actionbar.ActionBarPriority;
import me.bedwarshurts.mmextension.comp.PluginHooks;
import me.bedwarshurts.mmextension.utils.events.Events;
import me.bedwarshurts.mmextension.utils.terminable.Terminable;
import me.bedwarshurts.mmextension.utils.terminable.TerminableConsumer;
import me.bedwarshurts.mmextension.utils.terminable.TerminableRegistry;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class SpectatorData implements TerminableConsumer, Terminable {

    private final Player spectatingPlayer;
    private final GameMode previousGameMode;
    private final Entity spectatingEntity;
    private final Location returningPosition;
    private final TerminableRegistry consumer = new TerminableRegistry();

    public SpectatorData(Player spectatingPlayer, Entity spectatingEntity, Location returningPosition) {
        this.returningPosition = returningPosition.clone();
        this.spectatingPlayer = spectatingPlayer;
        this.spectatingEntity = spectatingEntity;
        this.previousGameMode = spectatingPlayer.getGameMode();

        this.init();
    }

    private void init() {
        if (!spectatingPlayer.isOnline() || !spectatingEntity.isValid()) {
            close();
            return;
        }

        spectatingPlayer.setGameMode(GameMode.SPECTATOR);
        spectatingPlayer.setSpectatorTarget(spectatingEntity);

        if (PluginHooks.isInstalled(PluginHooks.MMOCore)) {
            PlayerData.get(spectatingPlayer.getUniqueId()).getMMOPlayerData().getActionBar().hide(ActionBarPriority.LOW, Integer.MAX_VALUE);
        }
        registerEvents();
    }

    private void registerEvents() {
        Events.subscribe(PlayerToggleSneakEvent.class, EventPriority.HIGH)
                .filter(e -> e.getPlayer().equals(spectatingPlayer))
                .handler(e -> e.setCancelled(true))
                .bindWith(this);

        Events.subscribe(EntityDeathEvent.class, EventPriority.HIGH)
                .filter(e -> e.getEntity().equals(spectatingEntity))
                .handler(e -> this.close())
                .bindWith(this);

        Events.subscribe(PlayerTeleportEvent.class, EventPriority.HIGHEST)
                .filter(e -> e.getPlayer().equals(spectatingPlayer))
                .handler(e -> {
                    if (e.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
                        e.setCancelled(true);
                        if (spectatingEntity != null && spectatingEntity.isValid()) {
                            spectatingPlayer.setSpectatorTarget(spectatingEntity);
                        } else {
                            this.close();
                        }
                    }
                })
                .bindWith(this);

        Events.subscribe(PlayerQuitEvent.class, EventPriority.MONITOR)
                .filter(e -> e.getPlayer().equals(spectatingPlayer))
                .handler(e -> this.close())
                .bindWith(this);
    }

    @Override
    public TerminableConsumer with(AutoCloseable terminable) {
        consumer.with(terminable);
        return this;
    }

    @Override
    public void close() {
        consumer.close();

        spectatingPlayer.setSpectatorTarget(null);
        spectatingPlayer.setGameMode(previousGameMode);
        spectatingPlayer.teleport(returningPosition);
        PlayerData.get(spectatingPlayer.getUniqueId()).getMMOPlayerData().getActionBar().hide(ActionBarPriority.LOW, 1);
    }
}
