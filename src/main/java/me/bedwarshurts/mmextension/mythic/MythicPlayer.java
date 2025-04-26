package me.bedwarshurts.mmextension.mythic;

import me.bedwarshurts.mmextension.utils.terminable.Terminable;
import me.bedwarshurts.mmextension.utils.terminable.TerminableConsumer;
import me.bedwarshurts.mmextension.utils.terminable.TerminableRegistry;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class MythicPlayer {
    private final Player player;
    private final ConcurrentHashMap<String, MythicPlayerTracker> data = new ConcurrentHashMap<>();

    private final static HashSet<MythicPlayer> loadedPlayers = new HashSet<>();

    private MythicPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public MythicPlayerTracker getTracker(String keyword) {
        return data.get(keyword);
    }

    public MythicPlayerTracker addTracker(String keyword) {
        if (data.containsKey(keyword))
            throw new UnsupportedOperationException("A tracker with that keyword already exists.");

        MythicPlayerTracker tracker = new MythicPlayerTracker(keyword);
        data.put(keyword, tracker);

        return tracker;
    }

    public MythicPlayerTracker removeTracker(String keyword) {
        MythicPlayerTracker tracker = data.remove(keyword);
        if (tracker == null) return null;

        tracker.consumer.close();
        return tracker;
    }

    public static MythicPlayer getMythicPlayer(Player player) {
        return loadedPlayers.stream()
                .filter(mythicPlayer -> mythicPlayer.getPlayer().getUniqueId().equals(player.getUniqueId()))
                .findFirst()
                .orElseGet(() -> {
                    MythicPlayer mythicPlayer = new MythicPlayer(player);
                    loadedPlayers.add(mythicPlayer);
                    return mythicPlayer;
                });
    }

    public class MythicPlayerTracker implements TerminableConsumer, Terminable {
        private final TerminableRegistry consumer = new TerminableRegistry();
        private final String keyword;

        public MythicPlayerTracker(String keyword) {
            this.keyword = keyword;

            data.put(keyword, this);
        }

        @Override
        public TerminableConsumer with(AutoCloseable terminable) {
            return consumer.with(terminable);
        }

        @Override
        public void close() {
            consumer.close();
            data.remove(keyword);
        }
    }
}
