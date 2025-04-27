package me.bedwarshurts.mmextension.utils.events;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

public final class Events {

    public static <T extends Event> EventSubscriptionBuilder<T> subscribe(Class<T> eventClass, EventPriority priority) {
        return new EventSubscriptionBuilder<>(eventClass, priority);
    }
}

