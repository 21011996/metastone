package net.demilich.metastone.game.behaviour.diplom.datasetPrep;

/**
 * Created by ilya2 on 20.05.2017.
 */

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.ActionType;
import net.demilich.metastone.game.actions.EndTurnAction;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.actions.PhysicalAttackAction;
import net.demilich.metastone.game.behaviour.Behaviour;
import net.demilich.metastone.game.behaviour.heuristic.IGameStateHeuristic;
import net.demilich.metastone.game.behaviour.threat.FeatureVector;
import net.demilich.metastone.game.behaviour.threat.ThreatBasedHeuristic;
import net.demilich.metastone.game.cards.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class GameStateNNHu extends Behaviour {

    private final Logger logger = LoggerFactory.getLogger(net.demilich.metastone.game.behaviour.threat.GameStateValueBehaviour.class);
    int lastturn = 0;
    private IGameStateHeuristic heuristic = new ThreatBasedHeuristic(FeatureVector.getFittest());
    private HashMap<Integer, Double> visited = new HashMap<>();

    public GameStateNNHu() {
    }

    public double evaluateOppTurn(GameContext context, int playerId) {
        List<GameAction> lul = context.getValidActions();
        List<GameAction> validActions = lul.stream().filter(action1 -> action1 instanceof PhysicalAttackAction || action1 instanceof EndTurnAction).collect(Collectors.toList());
        if (validActions.size() == 0) {
            return heuristic.getScore(context, playerId);
        }
        double worseScore = Float.POSITIVE_INFINITY;

        for (GameAction gameAction : validActions) {
            double score = beta(context, context.getActivePlayerId(), gameAction, playerId);
            if (score < worseScore) {
                worseScore = score;
            }
        }
        return worseScore;

    }

    public double beta(GameContext context, int playerId, GameAction action, int ourPlayerId) {
        GameContext simulation = context.clone();
        simulation.getLogic().performGameAction(playerId, action);
        if (action instanceof EndTurnAction) {
            return heuristic.getScore(simulation, ourPlayerId);
        }
        if (simulation.getActivePlayerId() != playerId || simulation.gameDecided()) {
            return heuristic.getScore(simulation, ourPlayerId);
        }

        List<GameAction> validActions = simulation.getValidActions().stream().filter(action1 -> action1 instanceof PhysicalAttackAction || action1 instanceof EndTurnAction).collect(Collectors.toList());

        double score = Float.POSITIVE_INFINITY;

        for (GameAction gameAction : validActions) {
            score = Math.min(score, beta(simulation, playerId, gameAction, ourPlayerId));
            if (score < -10000) {
                break;
            }
        }
        return score;
    }

    private double alpha(GameContext context, int playerId, GameAction action, int depth) {
        GameContext simulation = context.clone();
        simulation.getLogic().performGameAction(playerId, action);
        if (action instanceof EndTurnAction) {
            GameContext lul = context.clone();
            lul.performAction(playerId, new EndTurnAction());
            return evaluateOppTurn(lul, playerId);
        }
        if (depth == 0 || simulation.getActivePlayerId() != playerId || simulation.gameDecided()) {
            return heuristic.getScore(simulation, playerId);
        }

        List<GameAction> validActions = simulation.getValidActions();

        double score = Float.NEGATIVE_INFINITY;

        try {
            if (visited.containsKey(simulation.hashCode())) {
                return visited.get(simulation.hashCode());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        for (GameAction gameAction : validActions) {

            score = Math.max(score, alpha(simulation, playerId, gameAction, depth - 1));
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
            double score = alpha(context, player.getId(), gameAction, depth);
            if (score > bestScore) {
                bestAction = gameAction;
                bestScore = score;
            }
        }

        logger.debug("Selecting best action {} with score {}", bestAction, bestScore);

        return bestAction;
    }
}

