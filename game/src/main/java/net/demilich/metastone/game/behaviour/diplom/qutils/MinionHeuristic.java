package net.demilich.metastone.game.behaviour.diplom.qutils;

import net.demilich.metastone.game.Attribute;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.behaviour.heuristic.IGameStateHeuristic;
import net.demilich.metastone.game.entities.minions.Minion;

/**
 * @author ilya2
 *         created on 10.04.2017
 */
public class MinionHeuristic implements IGameStateHeuristic {

    private float calculateMinionScore(Minion minion) {
        float minionScore = minion.getAttack() + minion.getHp();
        float baseScore = minionScore;
        if (minion.hasAttribute(Attribute.TAUNT)) {
            minionScore += 2;
        }
        if (minion.hasAttribute(Attribute.DIVINE_SHIELD)) {
            minionScore += 1.5f * baseScore;
        }
        return minionScore;
    }

    @Override
    public double getScore(GameContext context, int playerId) {
        float score = 0;
        Player player = context.getPlayer(playerId);
        Player opponent = context.getOpponent(player);
        if (player.getHero().isDestroyed()) {
            return -1000.0;
        }
        if (opponent.getHero().isDestroyed()) {
            return 1000.0;
        }
        int ownHp = player.getHero().getHp() + player.getHero().getArmor();
        int opponentHp = opponent.getHero().getHp() + opponent.getHero().getArmor();
        score += ownHp - opponentHp;

        score += player.getMinions().size() * 2;
        score -= opponent.getMinions().size() * 2;
        for (Minion minion : player.getMinions()) {
            score += calculateMinionScore(minion);
        }
        for (Minion minion : opponent.getMinions()) {
            score -= calculateMinionScore(minion);
        }

        return score;
    }

    @Override
    public void onActionSelected(GameContext context, int playerId) {

    }
}
