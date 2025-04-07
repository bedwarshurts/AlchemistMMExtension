package me.bedwarshurts.mmextension.utils.events;

import me.bedwarshurts.mmextension.AlchemistMMExtension;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class EventSubscriptionBuilder<T extends Event> {
    private final Class<T> eventClass;
    private final Listener listener = new Listener() {};
    private final EventPriority priority;
    private Predicate<T> filter;

    public EventSubscriptionBuilder(Class<T> eventClass, EventPriority priority) {
        this.eventClass = eventClass;
        this.priority = priority;
    }

    public EventSubscriptionBuilder<T> filter(Predicate<T> filter) {
        this.filter = filter;
        return this;
    }

    public EventSubscriptionBuilder<T> handler(Consumer<T> handler) {
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
                AlchemistMMExtension.inst()
        );
        return this;
    }

    public EventSubscriptionBuilder<T> bindWith(Terminable terminable) {
        terminable.onTerminate(this::unsubscribe);
        return this;
    }

    public void unsubscribe() {
        HandlerList.unregisterAll(listener);
    }
}
