package net.demilich.metastone.game.behaviour.diplom;

import com.sun.istack.internal.Nullable;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.EndTurnAction;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.actions.PhysicalAttackAction;
import net.demilich.metastone.game.behaviour.Behaviour;
import net.demilich.metastone.game.cards.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ilya2 on 06.05.2017.
 */
public class NotSoRandomBehaviour extends Behaviour {
    private DiplomBehaviour behaviour = new DiplomBehaviour(false);

    @Override
    public String getName() {
        return "Not so random behaviour";
    }

    @Override
    public List<Card> mulligan(GameContext context, Player player, List<Card> cards) {
        List<Card> discardedCards = new ArrayList<Card>();
        for (Card card : cards) {
            if (card.getBaseManaCost() >= 4) {
                discardedCards.add(card);
            }
        }
        return discardedCards;
    }

    private double measureAction(GameContext context, Player player, GameAction action) {
        GameContext clone = context.clone();
        clone.getLogic().performGameAction(player.getId(), action);
        double answer = measureState(clone, player);
        clone.dispose();
        return answer;
    }

    private double measureRecursivelyAction(GameContext context, Player player, GameAction action) {
        GameContext clone = context.clone();
        clone.getLogic().performGameAction(player.getId(), action);
        List<GameAction> actions = clone.getValidActions();
        double bestScore = measureState(clone, player);
        List<GameAction> notTradingActions = actions.stream().filter(gameAction -> !(gameAction instanceof PhysicalAttackAction) && !(gameAction instanceof EndTurnAction)).collect(Collectors.toList());
        for (GameAction action1 : notTradingActions) {
            double score = measureRecursivelyAction(clone, player, action1);
            if (score > bestScore) {
                bestScore = score;
            }
        }
        clone.dispose();
        return bestScore;
    }

    private double measureState(GameContext context, Player player) {
        return behaviour.getAvQ(context, player);
    }

    @Nullable
    private GameAction getBestAction(GameContext context, Player player, List<GameAction> validActions) {
        double bestScore = measureState(context, player);
        GameAction bestAction = null;
        for (GameAction gameAction : validActions) {
            double score = measureRecursivelyAction(context, player, gameAction);
            if (score > bestScore) {
                bestAction = gameAction;
                bestScore = score;
            }
        }

        return bestAction;
    }

    @Override
    public GameAction requestAction(GameContext context, Player player, List<GameAction> validActions) {
        List<GameAction> notTradingActions = validActions.stream().filter(gameAction -> !(gameAction instanceof PhysicalAttackAction) && !(gameAction instanceof EndTurnAction)).collect(Collectors.toList());
        if (notTradingActions.size() > 0) {
            GameAction answer = getBestAction(context, player, notTradingActions);
            if (answer != null) {
                return answer;
            }
        }
        return behaviour.requestAction(context, player, validActions);
    }

    @Override
    public void printGame() {

    }
}
