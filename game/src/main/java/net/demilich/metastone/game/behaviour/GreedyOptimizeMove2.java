package net.demilich.metastone.game.behaviour;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.behaviour.heuristic.IGameStateHeuristic;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.logic.GameLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GreedyOptimizeMove2 extends Behaviour {

    private final static Logger logger = LoggerFactory.getLogger(GreedyOptimizeMove2.class);

    private final IGameStateHeuristic heuristic;

    public GreedyOptimizeMove2(IGameStateHeuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public String getName() {
        return "Greedy Best Move True";
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

    @Override
    public GameAction requestAction(GameContext context, Player player, List<GameAction> validActions) {
        GameAction bestAction = validActions.get(0);
        double bestScore = Double.NEGATIVE_INFINITY;
        logger.debug("Current game state has a score of {}", bestScore, hashCode());
        for (GameAction gameAction : validActions) {
            GameContext simulationResult = simulateAction(context.clone(), player, gameAction);
            double gameStateScore = heuristic.getScore(simulationResult, player.getId());
            if (gameStateScore > bestScore) {
                bestScore = gameStateScore;
                bestAction = gameAction;
            }
            simulationResult.dispose();

        }
        logger.debug("Performing best action: {}", bestAction);

        return bestAction;
    }

    private GameContext simulateAction(GameContext simulation, Player player, GameAction action) {
        GameLogic.logger.debug("");
        GameLogic.logger.debug("********SIMULATION starts********** " + simulation.getLogic().hashCode());
        simulation.getLogic().performGameAction(player.getId(), action);
        GameLogic.logger.debug("********SIMULATION ends**********");
        GameLogic.logger.debug("");
        return simulation;
    }

}
