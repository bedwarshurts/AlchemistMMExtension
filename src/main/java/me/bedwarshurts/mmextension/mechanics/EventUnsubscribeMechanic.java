package me.bedwarshurts.mmextension.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.utils.events.EventSubscriptionBuilder;
import org.bukkit.event.Event;

@MythicMechanic(author = "bedwarshurts", name = "events:unsubscribe", aliases = {"events:unsub"}, description = "Unsubscribes from an event")
public class EventUnsubscribeMechanic implements ITargetedEntitySkill {
    private final String listenerIdentifier;

    public EventUnsubscribeMechanic(MythicLineConfig mlc) {
        this.listenerIdentifier = mlc.getString(new String[]{"name", "id", "identifier", "listenerIdentifier"}, "event");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        EventSubscriptionBuilder<? extends Event> subscription = EventSubscribeMechanic.activeSubscriptions.remove(listenerIdentifier + target.getUniqueId());
        if (subscription == null) return SkillResult.CONDITION_FAILED;

        subscription.unsubscribe();
        return SkillResult.SUCCESS;
    }
}
