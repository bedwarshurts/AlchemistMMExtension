package me.bedwarshurts.mmextension.mechanics;

import me.bedwarshurts.mmextension.mythic.MythicAuraRegistry;
import me.bedwarshurts.mmextension.utils.DataHolder;

import java.util.UUID;

public interface AuraMechanic {
    default void register(UUID uuid, AuraMechanic type, String identifier, Double duration) {
        MythicAuraRegistry.register(uuid, type, identifier, duration);
    };

    default void register(UUID uuid, AuraMechanic type, String identifier, Double duration, DataHolder dataHolder) {
        MythicAuraRegistry.register(uuid, type, identifier, duration, dataHolder);
    };

    void terminate();
}
