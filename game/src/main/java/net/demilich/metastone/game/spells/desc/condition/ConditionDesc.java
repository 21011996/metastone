package net.demilich.metastone.game.spells.desc.condition;

import net.demilich.metastone.game.cards.desc.Desc;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

public class ConditionDesc extends Desc<ConditionArg> {

    public ConditionDesc(Map<ConditionArg, Object> arguments) {
        super(arguments);
    }

    public static Map<ConditionArg, Object> build(Class<? extends Condition> conditionClass) {
        final Map<ConditionArg, Object> arguments = new EnumMap<>(ConditionArg.class);
        arguments.put(ConditionArg.CLASS, conditionClass);
        return arguments;
    }

    public Condition create() {
        Class<? extends Condition> conditionClass = getConditionClass();
        try {
            return conditionClass.getConstructor(ConditionDesc.class).newInstance(this);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Condition> getConditionClass() {
        return (Class<? extends Condition>) get(ConditionArg.CLASS);
    }

}
