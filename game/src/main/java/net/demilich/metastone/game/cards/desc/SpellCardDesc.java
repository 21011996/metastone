package net.demilich.metastone.game.cards.desc;

import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.SpellCard;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.desc.condition.ConditionDesc;
import net.demilich.metastone.game.targeting.TargetSelection;

import java.io.Serializable;

public class SpellCardDesc extends CardDesc implements Serializable {

    public TargetSelection targetSelection;
    public SpellDesc spell;
    public ConditionDesc condition;

    @Override
    public Card createInstance() {
        return new SpellCard(this);
    }

}
