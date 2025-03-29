package me.bedwarshurts.mmextension.utils.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class EventSubscriptionBuilder<T extends Event> {
    private final Class<T> eventClass;
    private final Listener listener = new Listener() {};
    private final EventPriority priority;
    private Predicate<T> filter;
    private Consumer<T> handler;

    public EventSubscriptionBuilder(Class<T> eventClass, EventPriority priority) {
        this.eventClass = eventClass;
        this.priority = priority;
    }

    public EventSubscriptionBuilder<T> filter(Predicate<T> filter) {
        this.filter = filter;
        return this;
    }

    public EventSubscriptionBuilder<T> handler(Consumer<T> handler) {
        this.handler = handler;
        return this;
    }

    public EventSubscriptionBuilder<T> bindWith(Plugin plugin) {
        Bukkit.getPluginManager().registerEvent(
                eventClass,
                listener,
                priority,
                (listener, event) -> {
                    if (eventClass.isInstance(event)) {
                        T casted = eventClass.cast(event);
                        if (filter == null || filter.test(casted)) {
                            handler.accept(casted);
                        }
                    }
                },
                plugin
        );
        return this;
    }

    public void unsubscribe() {
        HandlerList.unregisterAll(listener);
    }
}
