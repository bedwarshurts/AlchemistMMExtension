package me.bedwarshurts.mmextension.mechanics;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import me.bedwarshurts.mmextension.utils.events.EventSubscriptionBuilder;
import me.bedwarshurts.mmextension.utils.events.Events;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;

import static me.bedwarshurts.mmextension.AlchemistMMExtension.plugin;

@MythicMechanic(author = "bedwarshurts", name = "events:subscribe", aliases = {}, description = "Subscribes to an event and runs a skill when it is triggered")
public class EventSubscribeMechanic implements ITargetedEntitySkill {
    private final Class<? extends Event> eventClass;
    private String listenerIdentifier;
    private final MythicSkill skill;
    private final EventPriority priority;
    private final ArrayList<String> methods = new ArrayList<>();
    private final String triggerMethod;
    private final boolean cancelled;
    private final boolean requirePlayer;
    private final double duration;

    public static ConcurrentMap<String, EventSubscriptionBuilder<? extends Event>> activeSubscriptions = Maps.newConcurrentMap();

    public EventSubscribeMechanic(MythicLineConfig mlc) {
        try {
            this.listenerIdentifier = mlc.getString(new String[]{"name", "id", "identifier", "listenerIdentifier"}, "event");
            this.eventClass = Class.forName(mlc.getString(new String[]{"class"}, "org.bukkit.event.player.PlayerMoveEvent"))
                    .asSubclass(Event.class);
            this.skill = new MythicSkill(mlc.getString(new String[]{"skill", "s"}, ""));
            this.priority = EventPriority.valueOf(mlc.getString(new String[]{"eventPriority", "priority"}, "NORMAL").toUpperCase());
            this.methods.addAll(Arrays.asList(mlc.getString(new String[]{"methods", "m"}, "").split("\\),")));
            this.triggerMethod = mlc.getString(new String[]{"triggerMethod", "trigger"}, "getPlayer()");
            this.cancelled = mlc.getBoolean(new String[]{"cancelled", "cancel", "c"}, false);
            this.requirePlayer = mlc.getBoolean(new String[]{"requirePlayer", "p"}, false);
            this.duration = mlc.getDouble(new String[]{"duration", "d"}, 90);

        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Invalid event class: " + e);
        }
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (requirePlayer && !target.isPlayer()) return SkillResult.INVALID_TARGET;
        EventSubscriptionBuilder<? extends Event> subscriptionBuilder = Events.subscribe(eventClass, priority)
                .filter(e -> {
                    try {
                        Method isCancelled = e.getClass().getMethod("isCancelled");
                        return !((boolean) isCancelled.invoke(e));
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
                        return true;
                    }
                })
                .handler(e -> {
                    try {
                        if (cancelled) {
                            Method cancelEvent = e.getClass().getMethod("setCancelled", boolean.class);
                            cancelEvent.invoke(e, true);
                        }

                        Method getTrigger = e.getClass().getMethod(triggerMethod.substring(0, triggerMethod.indexOf("(")));
                        Entity trigger = (Entity) getTrigger.invoke(e);
                        if (!trigger.getUniqueId().equals(target.getBukkitEntity().getUniqueId())) return;
                        data.setTrigger(BukkitAdapter.adapt(trigger));

                        for (String methodString : methods) {
                            Method method;
                            Object obj = e;
                            Class<?> objClass = e.getClass();
                            for (String call : methodString.split("\\.")) {
                                String methodName = call.split("\\(")[0];
                                String methodArgs = call.split("\\(")[1].replace(")", "");
                                String[] args = methodArgs.split(",");
                                Class<?>[] argTypes = new Class<?>[args.length];
                                Object[] argValues = new Object[args.length];
                                for (String arg : args) {
                                    int spaceIndex = arg.indexOf(" ");
                                    if (spaceIndex == -1) continue;
                                    String type = arg.substring(0, arg.indexOf(" ")).trim();
                                    String value = arg.substring(arg.indexOf(" ")).trim();
                                    argTypes[Arrays.asList(args).indexOf(arg)] = getClassFromString(type);
                                    argValues[Arrays.asList(args).indexOf(arg)] = getValue(getClassFromString(type), value);
                                }
                                try {
                                    method = argTypes[0] == null
                                            ? objClass.getMethod(methodName)
                                            : objClass.getMethod(methodName, argTypes);
                                    obj = argTypes[0] == null
                                            ? method.invoke(obj)
                                            : method.invoke(obj, argValues);
                                    objClass = obj.getClass();
                                } catch (NullPointerException ignored) {
                                }
                            }
                            data.getVariables().put(methodString, new StringVariable(obj.toString()));
                        }
                        skill.cast(data);
                    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                             InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                })
                .bindWith(plugin);

        listenerIdentifier += target.getUniqueId();
        activeSubscriptions.put(listenerIdentifier, subscriptionBuilder);

        if (duration == 0) return SkillResult.SUCCESS;

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            EventSubscriptionBuilder<? extends Event> subscription = activeSubscriptions.remove(listenerIdentifier);
            if (subscription != null) subscription.unsubscribe();
        }, (long) duration);
        return SkillResult.SUCCESS;
    }

    private Class<?> getClassFromString(String typeName) throws ClassNotFoundException {
        return switch (typeName) {
            case "byte.class" -> byte.class;
            case "short.class" -> short.class;
            case "int.class" -> int.class;
            case "long.class" -> long.class;
            case "float.class" -> float.class;
            case "double.class" -> double.class;
            case "boolean.class" -> boolean.class;
            case "char.class" -> char.class;
            default -> Class.forName(typeName);
        };
    }

    private Object getValue(Class<?> type, String strValue) {
        if (type == double.class) return Double.parseDouble(strValue);
        if (type == float.class) return Float.parseFloat(strValue);
        if (type == int.class) return Integer.parseInt(strValue);
        if (type == long.class) return Long.parseLong(strValue);
        if (type == boolean.class) return Boolean.parseBoolean(strValue);
        if (type == byte.class) return Byte.parseByte(strValue);
        if (type == short.class) return Short.parseShort(strValue);
        if (type == char.class) return strValue.charAt(0);
        if (type.isEnum()) {
            try {
                Method valueOf = type.getMethod("valueOf", String.class);
                return valueOf.invoke(type, strValue);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return strValue;
    }
}
