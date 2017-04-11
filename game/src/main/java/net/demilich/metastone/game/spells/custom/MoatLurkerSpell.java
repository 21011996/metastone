package net.demilich.metastone.game.spells.custom;

import net.demilich.metastone.game.Attribute;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.SummonCard;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.minions.Minion;
import net.demilich.metastone.game.spells.*;
import net.demilich.metastone.game.spells.desc.SpellDesc;

public class MoatLurkerSpell extends Spell {

    @Override
    protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
        Minion minion = (Minion) target;
        TargetPlayer targetPlayer = TargetPlayer.SELF;
        if (minion.getOwner() != source.getOwner()) {
            targetPlayer = TargetPlayer.OPPONENT;
        }
        source.removeAttribute(Attribute.DEATHRATTLES);
        SpellDesc deathrattle = SummonSpell.create(targetPlayer, (SummonCard) minion.getSourceCard());
        SpellDesc addDeathrattleSpell = AddDeathrattleSpell.create(deathrattle);
        SpellDesc destroySpell = DestroySpell.create(target.getReference());
        SpellUtils.castChildSpell(context, player, destroySpell, source, target);
        SpellUtils.castChildSpell(context, player, addDeathrattleSpell, source, source);
    }

}
