package net.demilich.metastone.game.spells.trigger;

import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.events.GameEvent;
import net.demilich.metastone.game.events.GameEventType;
import net.demilich.metastone.game.spells.desc.trigger.EventTriggerDesc;

public class QuestSuccessTrigger extends GameEventTrigger {

    public QuestSuccessTrigger(EventTriggerDesc desc) {
        super(desc);
    }

    @Override
    protected boolean fire(GameEvent event, Entity host) {
        return true;
    }

    @Override
    public GameEventType interestedIn() {
        return GameEventType.QUEST_SUCCESSFUL;
    }

}
