package net.demilich.metastone.game.behaviour.diplom.datasetPrep;

/**
 * Created by ilya2 on 20.05.2017.
 */

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.ActionType;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.behaviour.Behaviour;
import net.demilich.metastone.game.behaviour.heuristic.IGameStateHeuristic;
import net.demilich.metastone.game.cards.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GameStateNNHu extends Behaviour {

    private final Logger logger = LoggerFactory.getLogger(net.demilich.metastone.game.behaviour.threat.GameStateValueBehaviour.class);
    int lastturn = 0;
    private IGameStateHeuristic heuristic = new NeuNetHeuristic();
    private HashMap<Integer, Double> visited = new HashMap<>();

    public GameStateNNHu() {
    }

    private double alphaBeta(GameContext context, int playerId, GameAction action, int depth) {
        GameContext simulation = context.clone();
        simulation.getLogic().performGameAction(playerId, action);
        if (depth == 0 || simulation.getActivePlayerId() != playerId || simulation.gameDecided()) {
            return heuristic.getScore(simulation, playerId);
        }

        List<GameAction> validActions = simulation.getValidActions();

        double score = Float.NEGATIVE_INFINITY;

        if (visited.containsKey(simulation.hashCode())) {
            return visited.get(simulation.hashCode());
        }
        for (GameAction gameAction : validActions) {

            score = Math.max(score, alphaBeta(simulation, playerId, gameAction, depth - 1));
            if (score >= 100000) {
                break;
            }
        }
        visited.put(simulation.hashCode(), score);
        return score;
    }

    @Override
    public String getName() {
        return "GameStateNN";
    }

    @Override
    public List<Card> mulligan(GameContext context, Player player, List<Card> cards) {
        List<Card> discardedCards = new ArrayList<Card>();
        for (Card card : cards) {
            if (card.getBaseManaCost() > 3) {
                discardedCards.add(card);
            }
        }
        return discardedCards;
    }

    public void printGame() {
    }

    @Override
    public GameAction requestAction(GameContext context, Player player, List<GameAction> validActions) {
        if (lastturn != context.getTurn()) {
            lastturn = context.getTurn();
            visited = new HashMap<>();
        }
        if (validActions.size() == 1) {
            return validActions.get(0);
        }

        int depth = 10000000;
        // when evaluating battlecry and discover actions, only optimize the immediate value
        if (validActions.get(0).getActionType() == ActionType.BATTLECRY) {
            depth = 0;
        } else if (validActions.get(0).getActionType() == ActionType.DISCOVER) {
            return validActions.get(0);
        }

        GameAction bestAction = validActions.get(0);
        double bestScore = Double.NEGATIVE_INFINITY;

        for (GameAction gameAction : validActions) {
            double score = alphaBeta(context, player.getId(), gameAction, depth);
            if (score > bestScore) {
                bestAction = gameAction;
                bestScore = score;
            }
        }

        logger.debug("Selecting best action {} with score {}", bestAction, bestScore);

        return bestAction;
    }
}

