package net.demilich.metastone.game.actions;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.desc.condition.Condition;
import net.demilich.metastone.game.spells.desc.filter.EntityFilter;
import net.demilich.metastone.game.targeting.EntityReference;
import net.demilich.metastone.game.targeting.TargetSelection;

import java.io.Serializable;
import java.util.function.Predicate;

public class BattlecryAction extends GameAction implements Serializable {

    private final SpellDesc spell;
    private Condition condition;

    protected BattlecryAction(SpellDesc spell) {
        this.spell = spell;
        setActionType(ActionType.BATTLECRY);
    }

    public static BattlecryAction createBattlecry(SpellDesc spell) {
        return createBattlecry(spell, TargetSelection.NONE);
    }

    public static BattlecryAction createBattlecry(SpellDesc spell, TargetSelection targetSelection) {
        BattlecryAction battlecry = new BattlecryAction(spell);
        battlecry.setTargetRequirement(targetSelection);
        return battlecry;
    }

    public boolean canBeExecuted(GameContext context, Player player) {
        if (getCondition() == null) {
            return true;
        }
        return getCondition().isFulfilled(context, player, null, null);
    }

    @Override
    public final boolean canBeExecutedOn(GameContext context, Player player, Entity entity) {
        if (!super.canBeExecutedOn(context, player, entity)) {
            return false;
        }
        if (getSource().getId() == entity.getId()) {
            return false;
        }
        if (getEntityFilter() == null) {
            return true;
        }
        return getEntityFilter().matches(context, player, entity);
    }

    @Override
    public BattlecryAction clone() {
        BattlecryAction clone = BattlecryAction.createBattlecry(getSpell(), getTargetRequirement());
        clone.setActionSuffix(getActionSuffix());
        clone.setSource(getSource());
        return clone;
    }

    @Override
    public void execute(GameContext context, int playerId) {
        EntityReference target = getSpell().hasPredefinedTarget() ? getSpell().getTarget() : getTargetKey();
        context.getLogic().castSpell(playerId, getSpell(), getSource(), target, getTargetRequirement(), false);
    }

    private Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public EntityFilter getEntityFilter() {
        return spell.getEntityFilter();
    }

    public void setEntityFilter(Predicate<Entity> entityFilter) {
        // this.entityFilter = entityFilter;
    }

    @Override
    public String getPromptText() {
        return "[Battlecry]";
    }

    public SpellDesc getSpell() {
        return spell;
    }

    @Override
    public boolean isSameActionGroup(GameAction anotherAction) {
        return anotherAction.getActionType() == getActionType();
    }

    @Override
    public String toString() {
        return String.format("[%s '%s']", getActionType(), getSpell().getSpellClass().getSimpleName());
    }
}
