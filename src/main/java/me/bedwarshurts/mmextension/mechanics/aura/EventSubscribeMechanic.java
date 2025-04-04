package me.bedwarshurts.mmextension.mechanics.aura;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.variables.types.IntegerVariable;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.mythic.MythicSkill;
import me.bedwarshurts.mmextension.utils.SkillUtils;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;

@MythicMechanic(author = "bedwarshurts", name = "events:subscribe", aliases = {"events:sub"}, description = "Subscribes to an event and runs a skill when it is triggered")
public class EventSubscribeMechanic extends Aura implements ITargetedEntitySkill {
    private final Class<? extends Event> eventClass;
    private final MythicSkill skill;
    private final EventPriority priority;
    private final ArrayList<String> methods = new ArrayList<>();
    private final String triggerMethod;
    private final boolean requirePlayer;

    private static final ConcurrentMap<String, Method> cachedMethods = Maps.newConcurrentMap();
    private static final ConcurrentMap<String, Class<?>> cachedClasses = Maps.newConcurrentMap();

    public EventSubscribeMechanic(SkillExecutor manager, File file, MythicLineConfig mlc) {
        super(manager, file, mlc.getLine(), mlc);
        try {
            this.eventClass = Class.forName(mlc.getString(new String[]{"class"}, "org.bukkit.event.player.PlayerMoveEvent"))
                    .asSubclass(Event.class);
            this.skill = new MythicSkill(mlc.getString(new String[]{"skill", "s"}, ""));
            this.priority = EventPriority.valueOf(mlc.getString(new String[]{"eventPriority", "priority"}, "NORMAL").toUpperCase());
            this.methods.addAll(Arrays.stream(mlc.getString(new String[]{"methods", "m"}, "")
                    .split("\\),"))
                    .map(s -> s.endsWith(")") ? s : s + ")")
                    .toList());
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

    private Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        String key = clazz.getName() + "." + methodName + Arrays.toString(parameterTypes);
        return cachedMethods.computeIfAbsent(key, k -> {
            try {
                if (parameterTypes.length == 0) {
                    return clazz.getMethod(methodName);
                }
                return clazz.getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Specified method not found: " + e);
            }
        });
    }

    private Class<?> getClassFromString(String typeName) throws ClassNotFoundException {
        return cachedClasses.computeIfAbsent(typeName, k -> switch (k) {
            case "byte.class" -> byte.class;
            case "short.class" -> short.class;
            case "int.class" -> int.class;
            case "long.class" -> long.class;
            case "float.class" -> float.class;
            case "double.class" -> double.class;
            case "boolean.class" -> boolean.class;
            case "char.class" -> char.class;
            default -> {
                try {
                    yield Class.forName(k);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
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
                throw new IllegalArgumentException("Not an enum " + e);
            }
        }
        return strValue;
    }

    private class EventSubscribeMechanicTracker extends AuraTracker implements IParentSkill {
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
                            Method isCancelled = getMethod(e.getClass(), "isCancelled");
                            return !((boolean) isCancelled.invoke(e));
                        } catch (InvocationTargetException | IllegalAccessException ignored) {
                            return true;
                        }
                    })
                    .handler(e -> {
                        long lastCallTime = System.currentTimeMillis();
                        try {
                            Method getTrigger = getMethod(e.getClass(), triggerMethod.substring(0, triggerMethod.indexOf("(")));
                            Entity trigger = (Entity) getTrigger.invoke(e);
                            if (!trigger.getUniqueId().equals(target.getBukkitEntity().getUniqueId())) return;
                            skillMetadata.setTrigger(BukkitAdapter.adapt(trigger));

                            for (String methodString : methods) {
                                Method method;
                                Object obj = e;
                                Class<?> objClass = e.getClass();
                                for (String call : methodString.split("\\).")) {
                                    call += ")";
                                    String methodName = call.split("\\(")[0];
                                    String methodArgs = call.split("\\(")[1].replace(")", "");
                                    String[] args = methodArgs.split(",");
                                    final int length = args[0].isEmpty() ? 0 : args.length;
                                    Class<?>[] argTypes = new Class<?>[length];
                                    Object[] argValues = new Object[length];
                                    for (int i = 0; i < args.length; i++) {
                                        String arg = args[i];
                                        int spaceIndex = arg.indexOf(" ");
                                        if (spaceIndex == -1) continue;
                                        String type = arg.substring(0, spaceIndex).trim();
                                        String value = arg.substring(spaceIndex).trim();
                                        Class<?> argClass = getClassFromString(type);
                                        argTypes[i] = argClass;
                                        argValues[i] = getValue(argClass, value);
                                    }
                                    method = getMethod(objClass, methodName, argTypes);
                                    obj = length == 0
                                            ? method.invoke(obj)
                                            : method.invoke(obj, argValues);
                                    if (!method.getReturnType().equals(void.class)) {
                                        objClass = obj.getClass();
                                    }
                                }
                                try {
                                    skillMetadata.getVariables().put(methodString, new StringVariable(obj.toString()));
                                } catch (NullPointerException ignored) {
                                }
                            }

                            skillMetadata.getVariables().put("lastCallTime", new IntegerVariable((int) lastCallTime));
                            skill.cast(skillMetadata);

                            if (this.cancel) {
                                Method cancelEvent = getMethod(e.getClass(), "setCancelled", boolean.class);
                                cancelEvent.invoke(e, true);
                            }
                        } catch (ClassNotFoundException | IllegalAccessException |
                                 InvocationTargetException ex) {
                            throw new IllegalArgumentException("Couldnt access a method " + ex);
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

        @Override
        public boolean isValid() {
            return this.entity.filter(abstractEntity ->
                    SkillUtils.isAuraValid(this.components, this.startDuration, this.chargesRemaining,
                            this.startCharges, this.ticksRemaining, abstractEntity, this.hasEnded)).isPresent();
        }

        @Override
        public void run() {
            if (this.startDuration >= 0) {
                this.ticksRemaining -= this.interval;
            }
            if (!this.isValid()) {
                this.terminate();
                return;
            }
            this.entity.ifPresent(e -> this.skillMetadata.setOrigin(e.getLocation()));
            this.auraTick();
        }
    }
}
