package net.demilich.metastone.game.spells.custom;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.GameTag;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.Actor;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.Spell;
import net.demilich.metastone.game.spells.desc.SpellDesc;

public class InnerFireSpell extends Spell {
	
	public static SpellDesc create() {
		SpellDesc desc = new SpellDesc(InnerFireSpell.class);
		return desc;
	}

	@Override
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity target) {
		Actor targetActor = (Actor) target;
		int buffAmount = targetActor.getHp() - targetActor.getAttack();
		target.modifyTag(GameTag.ATTACK, buffAmount);
	}
	
}