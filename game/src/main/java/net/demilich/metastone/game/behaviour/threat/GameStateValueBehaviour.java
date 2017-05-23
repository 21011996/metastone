package net.demilich.metastone.game.behaviour.threat;

import net.demilich.metastone.NotificationProxy;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.ActionType;
import net.demilich.metastone.game.actions.EndTurnAction;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.behaviour.Behaviour;
import net.demilich.metastone.game.behaviour.IBehaviour;
import net.demilich.metastone.game.behaviour.diplom.datasetPrep.DataPoint;
import net.demilich.metastone.game.behaviour.diplom.utils.FeautureExtractor;
import net.demilich.metastone.game.behaviour.diplom.utils.GameCounter;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.trainingmode.RequestTrainingDataNotification;
import net.demilich.metastone.trainingmode.TrainingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.demilich.metastone.game.behaviour.diplom.utils.GameCounter.addStateCount;

public class GameStateValueBehaviour extends Behaviour {

    private final Logger logger = LoggerFactory.getLogger(GameStateValueBehaviour.class);
    public String suffix = "";
    int numberOfStates = 0;
    long lasttime = 0;
    int lastturn = 0;
    ArrayList<DataPoint> stateChain = new ArrayList<>();
    int gameNo = GameCounter.getCount();
    private ThreatBasedHeuristic heuristic;
    private FeatureVector featureVector;
    private String nameSuffix = "";
    private HashMap<Integer, Double> visited = new HashMap<>();

    public GameStateValueBehaviour() {
    }

    public GameStateValueBehaviour(FeatureVector featureVector, String nameSuffix) {
        this.featureVector = featureVector;
        this.nameSuffix = nameSuffix;
        this.heuristic = new ThreatBasedHeuristic(featureVector);
    }

    private double alphaBeta(GameContext context, int playerId, GameAction action, int depth) {
        GameContext simulation = context.clone();
        numberOfStates++;
        simulation.getLogic().performGameAction(playerId, action);
        if (depth == 0 || simulation.getActivePlayerId() != playerId || simulation.gameDecided()) {
            //numberOfStates++;
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

    private void answerTrainingData(TrainingData trainingData) {
        featureVector = trainingData != null ? trainingData.getFeatureVector() : FeatureVector.getFittest();
        heuristic = new ThreatBasedHeuristic(featureVector);
        nameSuffix = trainingData != null ? "(trained)" : "(untrained)";
    }

    @Override
    public IBehaviour clone() {
        if (featureVector != null) {
            return new GameStateValueBehaviour(featureVector.clone(), nameSuffix);
        }
        return new GameStateValueBehaviour();
    }

    @Override
    public String getName() {
        return "Game state value " + nameSuffix;
    }

    @Override
    public List<Card> mulligan(GameContext context, Player player, List<Card> cards) {
        requestTrainingData(player);
        List<Card> discardedCards = new ArrayList<Card>();
        for (Card card : cards) {
            if (card.getBaseManaCost() > 3) {
                discardedCards.add(card);
            }
        }
        return discardedCards;
    }

    public void printGame() {
        //BufferedWriter outputWriter = null;
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream("dataset\\big\\" + gameNo + suffix + ".ser");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(stateChain);
            /*for (Feature feature : stateChain) {
                oos.writeObject(feature);
            }*/
            oos.close();
            fos.close();

            /*outputWriter = new BufferedWriter(new FileWriter("dataset\\" + gameNo + suffix + ".txt"));
            for (Feature feature : stateChain) {
                for (int i = 0; i < feature.x.length; i++) {
                    outputWriter.write(feature.x[i] + " ");

                }
                outputWriter.newLine();
            }
            outputWriter.flush();
            outputWriter.close();*/
        } catch (Exception e) {
        }
    }

    @Override
    public GameAction requestAction(GameContext context, Player player, List<GameAction> validActions) {
        if (context.getTurn() < lastturn) {
            printGame();
            stateChain = new ArrayList<>();
            gameNo = GameCounter.getCount();
        }
        if (lastturn != context.getTurn()) {
            lastturn = context.getTurn();
            visited = new HashMap<>();
            addState(context, player);
        }
        if (validActions.size() == 1) {
            return validActions.get(0);
        }

        //System.out.println( System.currentTimeMillis() - lasttime);
        lasttime = System.currentTimeMillis();
        numberOfStates = 0;
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
        //System.out.println(String.format("%d %d", context.getTurn(), numberOfStates));
        addState(context, player, bestAction);
        addStateCount();

        return bestAction;
    }

    public void addState(GameContext context, Player player, GameAction action) {
        if (!(action instanceof EndTurnAction)) {
            GameContext simulation = context.clone();
            simulation.getLogic().performGameAction(player.getId(), action);
            stateChain.add(new DataPoint(FeautureExtractor.getFeatures(simulation, player).left, heuristic.getScore(simulation, player.getId())));
            simulation.dispose();
        }
    }

    public void addState(GameContext context, Player player) {
        stateChain.add(new DataPoint(FeautureExtractor.getFeatures(context, player).left, heuristic.getScore(context, player.getId())));
    }

    private void requestTrainingData(Player player) {
        if (heuristic != null) {
            return;
        }

        RequestTrainingDataNotification request = new RequestTrainingDataNotification(player.getDeckName(), this::answerTrainingData);
        NotificationProxy.notifyObservers(request);
    }

}
