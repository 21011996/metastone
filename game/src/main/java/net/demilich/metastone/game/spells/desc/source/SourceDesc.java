package net.demilich.metastone.game.spells.desc.source;

import net.demilich.metastone.game.cards.desc.Desc;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

public class SourceDesc extends Desc<SourceArg> {

    public SourceDesc(Map<SourceArg, Object> arguments) {
        super(arguments);
    }

    public static Map<SourceArg, Object> build(Class<? extends CardSource> cardSourceClass) {
        final Map<SourceArg, Object> arguments = new EnumMap<>(SourceArg.class);
        arguments.put(SourceArg.CLASS, cardSourceClass);
        return arguments;
    }

    public CardSource create() {
        Class<? extends CardSource> cardSourceClass = getSourceClass();
        try {
            return cardSourceClass.getConstructor(SourceDesc.class).newInstance(this);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends CardSource> getSourceClass() {
        return (Class<? extends CardSource>) get(SourceArg.CLASS);
    }

}
