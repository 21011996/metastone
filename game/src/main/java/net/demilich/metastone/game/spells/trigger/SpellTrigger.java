package net.demilich.metastone.game.spells.trigger;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.events.GameEvent;
import net.demilich.metastone.game.events.GameEventType;
import net.demilich.metastone.game.logic.CustomCloneable;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.targeting.EntityReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpellTrigger extends CustomCloneable implements IGameEventListener {
    private final static Logger logger = LoggerFactory.getLogger(SpellTrigger.class);
    private final boolean oneTurn;
    private GameEventTrigger primaryTrigger;
    private GameEventTrigger secondaryTrigger;
    private SpellDesc spell;
    private EntityReference hostReference;
    private boolean expired;
    private boolean persistentOwner;
    private int turnDelay;

    public SpellTrigger(GameEventTrigger primaryTrigger, GameEventTrigger secondaryTrigger, SpellDesc spell, boolean oneTurn, int turnDelay) {
        this.primaryTrigger = primaryTrigger;
        this.secondaryTrigger = secondaryTrigger;
        this.spell = spell;
        this.oneTurn = oneTurn;
        this.turnDelay = turnDelay;
    }

    public SpellTrigger(GameEventTrigger primaryTrigger, GameEventTrigger secondaryTrigger, SpellDesc spell, boolean oneTurn) {
        this(primaryTrigger, secondaryTrigger, spell, oneTurn, 0);
    }

    public SpellTrigger(GameEventTrigger trigger, SpellDesc spell) {
        this(trigger, spell, false, 0);
    }

    public SpellTrigger(GameEventTrigger trigger, SpellDesc spell, boolean oneTime, int turnDelay) {
        this(trigger, null, spell, oneTime, turnDelay);
    }

    @Override
    public SpellTrigger clone() {
        SpellTrigger clone = (SpellTrigger) super.clone();
        clone.primaryTrigger = primaryTrigger.clone();
        if (secondaryTrigger != null) {
            clone.secondaryTrigger = secondaryTrigger.clone();
        }
        clone.spell = spell.clone();
        return clone;
    }

    public void expire() {
        expired = true;
    }

    @Override
    public EntityReference getHostReference() {
        return hostReference;
    }

    @Override
    public int getOwner() {
        return primaryTrigger.getOwner();
    }

    @Override
    public void setOwner(int playerIndex) {
        primaryTrigger.setOwner(playerIndex);
        if (secondaryTrigger != null) {
            secondaryTrigger.setOwner(playerIndex);
        }
    }

    protected SpellDesc getSpell() {
        return spell;
    }

    @Override
    public boolean interestedIn(GameEventType eventType) {
        boolean result = primaryTrigger.interestedIn() == eventType || primaryTrigger.interestedIn() == GameEventType.ALL;
        if (secondaryTrigger != null) {
            result |= secondaryTrigger.interestedIn() == eventType || secondaryTrigger.interestedIn() == GameEventType.ALL;
        }
        return result;
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public void onAdd(GameContext context) {
    }

    protected void onFire(int ownerId, SpellDesc spell, GameEvent event) {
        event.getGameContext().getLogic().castSpell(ownerId, spell, hostReference, null, true);
    }

    @Override
    public void onGameEvent(GameEvent event) {
        if (expired) {
            return;
        }

        int ownerId = primaryTrigger.getOwner();

        // Expire the trigger beforehand, in case of copying minion (Echoing
        // Ooze). Since this method should only be called
        // after being checked to be played, copying one-turn triggers should no
        // longer matter.
        // But let's check to make sure we don't accidentally expire something
        // that's still using it.
        if (oneTurn && (event.getEventType() == GameEventType.TURN_END || event.getEventType() == GameEventType.TURN_START)) {
            expire();
        }
        try {
            if (event.getEventTarget() != null) {
                event.getGameContext().getEventTargetStack().push(event.getEventTarget().getReference());
            } else {
                event.getGameContext().getEventTargetStack().push(null);
            }
            onFire(ownerId, spell, event);
            event.getGameContext().getEventTargetStack().pop();
        } catch (Exception e) {
            event.getGameContext().printCurrentTriggers();
            logger.error("SpellTrigger cannot be executed; GameEventTrigger: {} Spell: {}", primaryTrigger, spell);
            throw e;
        }
    }

    @Override
    public void onRemove(GameContext context) {
    }

    @Override
    public void setHost(Entity host) {
        this.hostReference = host.getReference();
    }

    @Override
    public String toString() {
        return "[SpellTrigger primaryTrigger=" + primaryTrigger + ", secondaryTrigger=" + secondaryTrigger + ", spell=" + spell
                + ", hostReference=" + hostReference + ", oneTurn=" + oneTurn + ", expired=" + expired + ", persistentOwner="
                + persistentOwner + ", turnDelay=" + turnDelay + "]";
    }

    @Override
    public boolean canFire(GameEvent event) {
        if (turnDelay > 0) {
            return false;
        }

        Entity host = event.getGameContext().resolveSingleTarget(hostReference);
        return (triggerFires(primaryTrigger, event, host) || triggerFires(secondaryTrigger, event, host));
    }

    private boolean triggerFires(GameEventTrigger trigger, GameEvent event, Entity host) {
        if (trigger == null) {
            return false;
        }
        if (trigger.interestedIn() != event.getEventType() && trigger.interestedIn() != GameEventType.ALL) {
            return false;
        }
        return trigger.fires(event, host);
    }

    public boolean hasPersistentOwner() {
        return persistentOwner;
    }

    public void setPersistentOwner(boolean persistentOwner) {
        this.persistentOwner = persistentOwner;
    }

    public void delayTimeDown() {
        if (turnDelay > 0) {
            turnDelay--;
        }
    }

    public boolean isDelayed() {
        return turnDelay > 0;
    }

    public void countDown() {
        primaryTrigger.countDown();
        if (secondaryTrigger != null) {
            secondaryTrigger.countDown();
        }
    }

    public void countDown(int num) {
        primaryTrigger.countDown(num);
        if (secondaryTrigger != null) {
            secondaryTrigger.countDown(num);
        }
    }

    public boolean hasCounter() {
        if (secondaryTrigger != null) {
            return primaryTrigger.getTriggerCount() > 0 || secondaryTrigger.getTriggerCount() > 0;
        }
        return primaryTrigger.getTriggerCount() > 0;
    }

    public boolean oneTurnOnly() {
        return oneTurn;
    }

    public boolean canFireCondition(GameEvent event) {
        return primaryTrigger.canFireCondition(event) || (secondaryTrigger != null && secondaryTrigger.canFireCondition(event));
    }

}
