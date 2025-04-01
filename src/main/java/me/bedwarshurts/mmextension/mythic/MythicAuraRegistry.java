package me.bedwarshurts.mmextension.mythic;

import me.bedwarshurts.mmextension.mechanics.AuraMechanic;
import me.bedwarshurts.mmextension.utils.DataHolder;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.bedwarshurts.mmextension.AlchemistMMExtension.plugin;

public class MythicAuraRegistry {
    public static final ConcurrentHashMap<UUID, HashSet<MythicAuraRegistry>> auras = new ConcurrentHashMap<>();

    private final AuraMechanic type;
    private final String identifier;
    private final UUID uuid;
    private final DataHolder dataHolder;

    private MythicAuraRegistry(AuraMechanic type, UUID uuid, String identifier, Double duration, DataHolder dataHolder) {
        this.type = type;
        this.identifier = identifier;
        this.uuid = uuid;
        this.dataHolder = dataHolder;

        if (duration == 0) return;
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, (() -> {
            this.terminate();
            auras.get(uuid).remove(this);
            }), (long) (duration * 20));
    }

    public static void register(UUID uuid, AuraMechanic type, String identifier, Double duration) {
        register(uuid, type, identifier, duration, null);
    }

    public static void register(UUID uuid, AuraMechanic type, String identifier, Double duration, DataHolder dataHolder) {
        auras.computeIfAbsent(uuid, key -> new HashSet<>())
                .add(new MythicAuraRegistry(type, uuid, identifier, duration, dataHolder));
    }

    public static MythicAuraRegistry getAura(String identifier, UUID uuid) {
        for (MythicAuraRegistry aura : auras.get(uuid)) {
            if (aura.identifier.equals(identifier)) {
                return aura;
            }
        }
        return null;
    }

    public DataHolder getAuraData() {
        return dataHolder;
    }

    public AuraMechanic getType() {
        return type;
    }

    public void terminate() {
        type.terminate();
        auras.get(uuid).remove(this);
    }
}
