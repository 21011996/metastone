package net.demilich.metastone.game.spells;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.desc.filter.EntityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DestroyAllExceptOneSpell extends DestroySpell {

    public static Logger logger = LoggerFactory.getLogger(DestroyAllExceptOneSpell.class);

    @Override
    public void cast(GameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targets) {
        if (targets == null || targets.isEmpty()) {
            return;
        }
        EntityFilter filter = desc.getEntityFilter();
        List<Entity> destroyedTargets = new ArrayList<Entity>(targets);
        List<Entity> potentialSurvivors = SpellUtils.getValidTargets(context, player, destroyedTargets, filter);
        if (!potentialSurvivors.isEmpty()) {
            Entity randomTarget = SpellUtils.getRandomTarget(potentialSurvivors);
            destroyedTargets.remove(randomTarget);
        }

        for (Entity entity : destroyedTargets) {
            onCast(context, player, null, null, entity);
        }
    }
}
