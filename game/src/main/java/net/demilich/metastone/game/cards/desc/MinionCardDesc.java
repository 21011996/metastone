package net.demilich.metastone.game.cards.desc;

import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.MinionCard;
import net.demilich.metastone.game.entities.minions.Race;

import java.io.Serializable;

public class MinionCardDesc extends SummonCardDesc implements Serializable {

    public int baseAttack;
    public int baseHp;
    public Race race;

    @Override
    public Card createInstance() {
        return new MinionCard(this);
    }

}
