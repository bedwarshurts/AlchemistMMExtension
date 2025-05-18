package me.bedwarshurts.mmextension.skills.mechanics.aura.events;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.variables.types.IntegerVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import me.bedwarshurts.mmextension.skills.mechanics.aura.AlchemistAura;
import me.bedwarshurts.mmextension.utils.ReflectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@MythicMechanic(author = "bedwarshurts", name = "events:subscribe", aliases = {"events:sub"}, description = "Subscribes to an event and runs a skill when it is triggered")
public class EventSubscribeMechanic extends AlchemistAura implements ITargetedEntitySkill {

    private final Class<? extends Event> eventClass;
    private final MythicSkill skill;
    private final EventPriority priority;
    private final String triggerMethod;
    private final boolean requirePlayer;

    public EventSubscribeMechanic(SkillExecutor manager, File file, MythicLineConfig mlc) {
        super(manager, file, mlc.getLine(), mlc);
        try {
            this.eventClass = Class.forName(mlc.getString(new String[]{"class"}, "org.bukkit.event.player.PlayerMoveEvent"))
                    .asSubclass(Event.class);
            this.skill = new MythicSkill(mlc.getString(new String[]{"skill", "s"}, ""));
            this.priority = EventPriority.valueOf(mlc.getString(new String[]{"eventPriority", "priority"}, "NORMAL").toUpperCase());
            this.triggerMethod = mlc.getString(new String[]{"triggerMethod", "trigger"}, "getPlayer()");
            this.requirePlayer = mlc.getBoolean(new String[]{"requirePlayer", "rp"}, false);
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Invalid event class: " + e);
        }
    }

    @SuppressWarnings("resource")
    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (requirePlayer && !target.isPlayer()) return SkillResult.INVALID_TARGET;
        new EventSubscribeMechanicTracker(target, data);
        return SkillResult.SUCCESS;
    }

    private class EventSubscribeMechanicTracker extends AlchemistAura.AlchemistAuraTracker implements IParentSkill {
        private final AbstractEntity target;
        private boolean cancel;

        public EventSubscribeMechanicTracker(AbstractEntity target, SkillMetadata data) {
            super(target, data);
            this.target = target;

            start();
        }

        @Override
        public void auraStart() {
            executeAuraSkill(onStartSkill, skillMetadata);
            Events.subscribe(eventClass, priority)
                    .filter(e -> {
                        try {
                            Method isCancelled = ReflectionUtils.getMethod(e.getClass(), "isCancelled");
                            return !((boolean) isCancelled.invoke(e));
                        } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException ignored) {
                            return true;
                        }
                    })
                    .handler(e -> {
                        long lastCallTime = System.currentTimeMillis();
                        skillMetadata.setMetadata("event", e);

                        try {
                            Method getTrigger = ReflectionUtils.getMethod(e.getClass(), triggerMethod.substring(0, triggerMethod.indexOf("(")));
                            Entity trigger = (Entity) getTrigger.invoke(e);
                            if (!trigger.getUniqueId().equals(target.getBukkitEntity().getUniqueId())) return;
                            skillMetadata.setTrigger(BukkitAdapter.adapt(trigger));
                        } catch (InvocationTargetException | IllegalAccessException ex) {
                            throw new IllegalArgumentException("The trigger method specified is invalid", ex);
                        }

                        skillMetadata.getVariables().put("lastCallTime", new IntegerVariable((int) lastCallTime));
                            skill.cast(skillMetadata);

                            if (this.cancel) {
                                Method cancelEvent = ReflectionUtils.getMethod(e.getClass(), "setCancelled", boolean.class);
                                try {
                                    cancelEvent.invoke(e, true);
                                } catch (IllegalAccessException | InvocationTargetException ex) {
                                    throw new UnsupportedOperationException("This event cannot be cancelled", ex);
                                }
                            }
                        })
                    .bindWith(this);
        }

        @Override
        public void auraStop() {
            executeAuraSkill(onEndSkill, skillMetadata);
        }

        @Override
        public void setCancelled() {
            this.cancel = true;
        }

        @Override
        public boolean getCancelled() {
            return cancel;
        }
    }
}
