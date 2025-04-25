package me.bedwarshurts.mmextension.skills.mechanics.aura.events;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import me.bedwarshurts.mmextension.utils.InvokeUtils;
import org.bukkit.event.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class InvokeMethodMechanic implements INoTargetSkill {
    private final ArrayList<String> methods = new ArrayList<>();

    public InvokeMethodMechanic(MythicLineConfig mlc) {
        this.methods.addAll(Arrays.stream(mlc.getString(new String[]{"methods", "m"}, "")
                .split("\\),"))
                .map(s -> !s.endsWith(")") && !s.isEmpty() ? s + ")" : s)
                .toList());
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        Optional<Object> o = data.getMetadata("event");
        if (o.isEmpty()) return SkillResult.CONDITION_FAILED;
        if (!(o.get() instanceof Event e)) return SkillResult.INVALID_TARGET;

        try {
            for (String methodString : methods) {
                if (methodString.isEmpty()) continue;
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
                        Class<?> argClass = InvokeUtils.getClassFromString(type);
                        argTypes[i] = argClass;
                        argValues[i] = InvokeUtils.getValue(argClass, value);
                    }
                    method = InvokeUtils.getMethod(objClass, methodName, argTypes);
                    obj = length == 0
                            ? method.invoke(obj)
                            : method.invoke(obj, argValues);
                    if (!method.getReturnType().equals(void.class)) {
                        objClass = obj.getClass();
                    }
                }
                try {
                    data.getVariables().put(methodString, new StringVariable(obj.toString()));
                } catch (NullPointerException ignored) {
                }
            }
            return SkillResult.SUCCESS;
        } catch (InvocationTargetException | IllegalAccessException | ClassNotFoundException ex) {
            throw new IllegalArgumentException("The method signature specified is invalid", ex);
        }
    }
}
