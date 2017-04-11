package net.demilich.metastone.game.spells.trigger;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.events.GameEvent;
import net.demilich.metastone.game.events.GameEventType;
import net.demilich.metastone.game.targeting.EntityReference;

public interface IGameEventListener {

    IGameEventListener clone();

    boolean canFire(GameEvent event);

    EntityReference getHostReference();

    int getOwner();

    void setOwner(int playerIndex);

    boolean interestedIn(GameEventType eventType);

    boolean isExpired();

    void onAdd(GameContext context);

    void onGameEvent(GameEvent event);

    void onRemove(GameContext context);

    void setHost(Entity host);

    boolean hasPersistentOwner();

    boolean oneTurnOnly();

    boolean isDelayed();

    void delayTimeDown();

    boolean hasCounter();

    void countDown();

    void expire();

    boolean canFireCondition(GameEvent event);

}