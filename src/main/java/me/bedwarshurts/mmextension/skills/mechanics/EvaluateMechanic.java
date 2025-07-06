package me.bedwarshurts.mmextension.skills.mechanics;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableScope;
import io.lumine.mythic.core.skills.variables.types.DoubleVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.bedwarshurts.mmextension.AlchemistMMExtension;
import me.bedwarshurts.mmextension.utils.PlaceholderUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

@MythicMechanic(author = "bedwarshurts", name = "evaluate", aliases = {}, description = "Evaluates a math expression")
public class EvaluateMechanic implements ITargetedEntitySkill {

    private final String expression;
    private final VariableScope scope;

    public EvaluateMechanic(MythicLineConfig mlc) {
        this.expression = mlc.getString("expression", "0");
        this.scope = switch (mlc.getString("scope", "caster").toLowerCase()) {
            case "caster" -> VariableScope.CASTER;
            case "skill" -> VariableScope.SKILL;
            default -> throw new IllegalArgumentException("Invalid scope specified: " + mlc.getString("scope"));
        };
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        VariableRegistry registry = MythicBukkit.inst().getVariableManager().getRegistry(scope, data);
        if (registry == null) {
            AlchemistMMExtension.inst().getLogger().severe("Variable registry for scope " + scope + " is null. Cannot evaluate expression: " + expression);
            return SkillResult.CONDITION_FAILED;
        }

        String parsedExpression = PlaceholderUtils.parseStringPlaceholders(expression, data, target);
        parsedExpression = PlaceholderAPI.setPlaceholders(target.isPlayer() ? (Player) target.getBukkitEntity() : null, parsedExpression);

        Expression expression = new Expression(parsedExpression);

        try {
            registry.put("expression_result", new DoubleVariable(expression.evaluate().getNumberValue().doubleValue()));
        } catch (EvaluationException | ParseException e) {
            throw new IllegalArgumentException("Failed to evaluate expression: " + parsedExpression, e);
        }

        return SkillResult.SUCCESS;
    }
}
