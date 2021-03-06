package net.demilich.metastone.game.spells.trigger;

import net.demilich.metastone.game.Attribute;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.minions.Race;
import net.demilich.metastone.game.events.GameEvent;
import net.demilich.metastone.game.events.GameEventType;
import net.demilich.metastone.game.events.SummonEvent;
import net.demilich.metastone.game.spells.desc.trigger.EventTriggerArg;
import net.demilich.metastone.game.spells.desc.trigger.EventTriggerDesc;

public class MinionSummonedTrigger extends GameEventTrigger {

    public MinionSummonedTrigger(EventTriggerDesc desc) {
        super(desc);
    }

    @Override
    protected boolean fire(GameEvent event, Entity host) {
        SummonEvent summonEvent = (SummonEvent) event;
        Race race = (Race) desc.get(EventTriggerArg.RACE);
        if (race != null && summonEvent.getMinion().getRace() != race) {
            return false;
        }

        Attribute requiredAttribute = (Attribute) desc.get(EventTriggerArg.REQUIRED_ATTRIBUTE);
        return !(requiredAttribute != null && !summonEvent.getMinion().hasAttribute(requiredAttribute));
    }

    @Override
    public GameEventType interestedIn() {
        return GameEventType.SUMMON;
    }

}
