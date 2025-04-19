package me.bedwarshurts.mmextension.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import me.bedwarshurts.mmextension.utils.InvokeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@MythicMechanic(author = "bedwarshurts", name = "stringbuilder", aliases = {"sbuilder"}, description = "Builds a string using Java's StringBuilder")
public class StringBuilderMechanic implements INoTargetSkill {
    private final PlaceholderString inputString;
    private final String variableName;
    private final String[] actions;

    public StringBuilderMechanic(MythicLineConfig mlc) {
        this.inputString = mlc.getPlaceholderString(new String[]{"string", "s", "input", "i"}, "");
        this.variableName = mlc.getString(new String[]{"variableName", "var", "v", "variable"}, "result");
        this.actions = mlc.getString(new String[]{"action", "a"}, "").split(",");
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        StringBuilder builder = new StringBuilder(inputString.get(data));

        for (String action : actions) {
            if (action.trim().isEmpty()) continue;
            int startIndex = action.indexOf("(");
            int endIndex = action.indexOf(")");
            String methodName = (startIndex > 0)
                    ? action.substring(0, startIndex).trim()
                    : action.trim();
            String argString = (startIndex >= 0 && endIndex > startIndex)
                    ? action.substring(startIndex + 1, endIndex).trim()
                    : "";

            String[] argValues = argString.isEmpty() ? new String[0] : argString.split(",");
            Class<?>[] paramTypes = new Class<?>[argValues.length];
            Object[] parsedValues = new Object[argValues.length];
            for (int i = 0; i < argValues.length; i++) {
                String[] args = argValues[i].split(" ", 2);
                if (args.length < 2) {
                    throw new IllegalArgumentException("Invalid argument format: " + argValues[i] + ". Expected format: 'type value'");
                }
                try {
                    Class<?> paramType = InvokeUtils.getClassFromString(args[0]);
                    paramTypes[i] = paramType;
                    parsedValues[i] = InvokeUtils.getValue(paramType, args[1].trim());
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Invalid class name: " + args[0], e);
                }
            }

            try {
                Method method = InvokeUtils.getMethod(StringBuilder.class, methodName, paramTypes);
                method.invoke(builder, parsedValues);
            } catch (IllegalAccessException | InvocationTargetException e) {
                AlchemistMMExtension.inst().getLogger().severe("An error occurred while invoking method: " + methodName + " - " + e);
            }
        }

        VariableRegistry variables = data.getVariables();
        variables.put(variableName, new StringVariable(builder.toString()));

        return SkillResult.SUCCESS;
    }
}
