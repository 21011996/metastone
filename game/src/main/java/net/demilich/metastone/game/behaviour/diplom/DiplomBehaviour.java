package net.demilich.metastone.game.behaviour.diplom;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.EndTurnAction;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.actions.PhysicalAttackAction;
import net.demilich.metastone.game.behaviour.Behaviour;
import net.demilich.metastone.game.behaviour.diplom.network.Net;
import net.demilich.metastone.game.behaviour.diplom.utils.*;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.entities.minions.Minion;

import java.io.File;
import java.util.*;

import static net.demilich.metastone.game.behaviour.diplom.Consts.FEATURE_SIZE;
import static net.demilich.metastone.game.behaviour.diplom.Consts.NUMBER_OF_ACTIONS;

/**
 * @author ilya2
 *         created on 08.04.2017
 */
public class DiplomBehaviour extends Behaviour {
    private static final Params BEST_PARAMS = new Params(0.001, 0, 1, 0);
    private static final double DICOUNT_REWARD = 0.99;
    public boolean finished = false;
    public int beforeSave = 500;
    Random random = new Random();
    int total = 0;
    int error = 0;
    private boolean learning = true;
    private AttackBackend attackBackend = new AttackBackend();
    //TODO setup trading games and use magic of Q learning
    //Input - FEATURE_SIZE features
    //Output - 7 pick minion + 1 do nothing
    private Net network = new Net(new int[]{FEATURE_SIZE, 64, 64, 64, NUMBER_OF_ACTIONS}, new Activation[]{Activation.SIGMOID, Activation.SIGMOID, Activation.SIGMOID, Activation.SIGMOID, Activation.LINEAR});
    private GameContext start = null;

    public DiplomBehaviour() {
        if (!new File("neunet\\w" + "0" + ".txt").isFile()) {
            network.initWeights();
        } else {
            this.network.initWeights(new File[]{
                    new File("neunet", "w0.txt"),
                    new File("neunet", "w1.txt"),
                    new File("neunet", "w2.txt"),
                    new File("neunet", "w3.txt")
            });
        }
    }

    public DiplomBehaviour(boolean learning) {
        this.learning = learning;
        if (!new File("neunet\\w" + "0" + ".txt").isFile()) {
            network.initWeights();
        } else {
            this.network.initWeights(new File[]{
                    new File("neunet", "w0.txt"),
                    new File("neunet", "w1.txt"),
                    new File("neunet", "w2.txt"),
                    new File("neunet", "w3.txt")
            });
        }
    }

    public void saveNet() {
        beforeSave--;
        if (beforeSave < 0) {
            beforeSave = 500;
            for (int i = 0; i < network.weights.length; i++) {
                network.weights[i].writeFile("neunet\\w" + i + ".txt");
            }
            System.out.println("Saved Net");
            System.out.println("Bank size = " + ReplayBank.getSize());
            ReplayBank.printProfile();
        }
    }

    public void forceSave() {
        beforeSave = -1;
        saveNet();
    }

    public void learn() {
        ArrayList<TrainUnit> trainSet = ReplayBank.getBatch(64);
        int meh = 0;
        for (TrainUnit trainUnit : trainSet) {
            Feature s = trainUnit.getSFeatures();
            int actionIndex = trainUnit.getAction();
            double r = trainUnit.getReward();
            Feature sa = trainUnit.getSAFeatures();

            double[] qs = network.classify(s);

            double[] qsa = network.classify(sa);
            int[] validActions = trainUnit.getValidActions();
            boolean[] invalidActions = new boolean[NUMBER_OF_ACTIONS + 1];
            for (int i : validActions) {
                invalidActions[i + 1] = true;
            }
            for (int i = 0; i < NUMBER_OF_ACTIONS + 1; i++) {
                if (!invalidActions[i]) {
                    qsa[i] = Float.NEGATIVE_INFINITY;
                }
            }
            if (sa.x[0] > 0.0 && sa.x[43] > 0.0) {
                double maxqsa = Arrays.stream(qsa).max().getAsDouble() - 0.5;
                qs[actionIndex + 1] = r + DICOUNT_REWARD * (maxqsa);
                if (maxqsa <= -1000.0) {
                    System.out.println("asdasd " + r + "  " + maxqsa);
                    qs[actionIndex + 1] = r;
                }
            } else {
                qs[actionIndex + 1] = r;
                if (r < -1000.0) {
                    System.out.println("asdasd " + r);
                }
            }
            /*if (qs[actionIndex + 1] < 0 || qs[actionIndex + 1] > 1) {
                System.out.println(qs[actionIndex + 1]);
                System.out.println(r);
                System.out.println(DICOUNT_REWARD * maxqsa);
            }*/
            network.learnStep(new DataInstance(trainUnit.getSFeatures(), qs), BEST_PARAMS);
        }
    }

    /*private double[] getQ(GameContext context, Player player, List<GameAction> validActions, boolean first) {
        double baseScore = new MinionHeuristic().getScore(context, player.getId());
        double[] answer = new double[57];
        Arrays.fill(answer, -1.0);
        answer[56] = 0.0;
        if (true) {
            GameContext simulation = context.clone();
            simulation.getLogic().performGameAction(player.getId(), new EndTurnAction());
            GameAction gameAction;
            do {
                gameAction = new GreedyOptimizeMove(new WeightedHeuristic()).requestAction(simulation, simulation.getActivePlayer(), simulation.getValidActions());
                simulation.getLogic().performGameAction(simulation.getActivePlayerId(), gameAction);
            } while (!(gameAction instanceof EndTurnAction));
            double newScore = new MinionHeuristic().getScore(simulation, player.getId());
            simulation.dispose();
            answer[56] = DICOUNT_REWARD * (newScore - baseScore) / 2000;
        }
        HashMap<GameAction, Integer> actionMap = getValidActions(context, player, validActions);
        for (Map.Entry<GameAction, Integer> entry : actionMap.entrySet()) {
            GameAction action = entry.getKey();
            int slot = entry.getValue();

            GameContext simulation = context.clone();
            simulation.getLogic().performGameAction(player.getId(), action);
            double newScore = new MinionHeuristic().getScore(simulation, player.getId());
            answer[slot] = (newScore - baseScore);
            if (first) {
                double futureQ = Arrays.stream(getQ(simulation, simulation.getPlayer(player.getId()), simulation.getValidActions(), false)).max().getAsDouble();
                answer[slot] += DICOUNT_REWARD * futureQ;
            }
            answer[slot] = answer[slot] / 2000;
            simulation.dispose();
        }
        return answer;
    }*/

    /*private ArrayList<Integer> getValidActions(GameContext context, Player player, List<GameAction> validActions) {
        ArrayList<Integer> answer = new ArrayList<>();
        ArrayList<Minion> ourMinions = (ArrayList<Minion>) player.getMinions();
        ArrayList<Minion> oppMinions = (ArrayList<Minion>) context.getOpponent(player).getMinions();
        for (GameAction action : validActions) {
            if (action instanceof PhysicalAttackAction) {
                PhysicalAttackAction physicalAttackAction = (PhysicalAttackAction) action;
                int attackerId;
                attackerId = physicalAttackAction.getAttackerReference().getId();
                int defenderId = physicalAttackAction.getTargetKey().getId();

                int i = getById(ourMinions, attackerId);
                int j = getById(oppMinions, defenderId);
                if (j == -1) {
                    j = 7;
                }

                answer.add(i * 8 + j);
            }
        }
        return answer;
    }*/

    @Override
    public String getName() {
        return "Diplom Behaviour";
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

    private HashMap<Integer, Integer> convertAttackActionReversed(GameContext context, Player player, List<GameAction> validActions) {
        HashMap<Integer, Integer> answer = new HashMap<>();
        ArrayList<Minion> ourMinions = (ArrayList<Minion>) player.getMinions();
        for (GameAction action : validActions) {
            if (action instanceof PhysicalAttackAction) {
                PhysicalAttackAction physicalAttackAction = (PhysicalAttackAction) action;
                int attackerId = physicalAttackAction.getAttackerReference().getId();

                int i = getById(ourMinions, attackerId);

                answer.put(i, attackerId);
            } else if (action instanceof EndTurnAction) {
                answer.put(NUMBER_OF_ACTIONS - 1, -666);
            }
        }
        return answer;
    }

    private int getById(ArrayList<Minion> minions, int id) {
        for (int i = 0; i < minions.size(); i++) {
            if (minions.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public HashMap<GameAction, Double> softMax(Set<Map.Entry<GameAction, Double>> input) {
        Map.Entry<GameAction, Double>[] answer = new Map.Entry[input.size()];
        input.toArray(answer);
        double sum = 0.0;
        for (int i = 0; i < input.size(); i++) {
            answer[i].setValue(Math.exp(answer[i].getValue()));
            sum += answer[i].getValue();
        }
        HashMap<GameAction, Double> lul = new HashMap<>();
        for (int i = 0; i < input.size(); i++) {
            answer[i].setValue(answer[i].getValue() / sum);
            lul.put(answer[i].getKey(), answer[i].getValue());
        }
        return lul;
    }

    @Override
    public GameAction requestAction(GameContext context, Player player, List<GameAction> validActions) {
        HashMap<Integer, Integer> actionMap = convertAttackActionReversed(context, player, validActions);
        if (actionMap.size() != 0) {
            total++;
            Feature feature = FeautureExtractor.getFeatures3(context, player);
            double[] q = network.classify(feature);
            q = Arrays.stream(q).map(value -> value * 8000.0 - 4000.0).toArray();

            HashMap<Integer, Double> answers = new HashMap<>();
            for (Map.Entry<Integer, Integer> entry : actionMap.entrySet()) {
                answers.put(entry.getValue(), q[entry.getKey() + 1]);
            }

//            //TODO make it more nice
//            if (false) {
//                answers = softMax(answers.entrySet());
//                double summ = 1.0;
//                for (Map.Entry<GameAction, Double> entry : answers.entrySet()) {
//                    if (entry.getValue() >= summ - random.nextDouble()) {
//                        return entry.getKey();
//                    } else {
//                        summ -= entry.getValue();
//                    }
//                }
//            }
            if (learning) {
                if (random.nextDouble() <= 0.3) {
                    return validActions.get(random.nextInt(validActions.size()));
                }
            }

            Integer answerId = answers.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
            if (answerId == -666) {
                return new EndTurnAction();
            }
            return attackBackend.requestAction(context, player, validActions, answerId);
            /*if (index >= 0 && index <= 56) {
                if (actionMap.containsKey(index)) {
                    return actionMap.get(index);
                } else {
                    q[index + 1] = PUNISHMENT;
                    network.learnStep(new DataInstance(FeautureExtractor.getFeatures(context, player), q), BEST_PARAMS);
                    error++;
                    //System.out.println("Coudnt get correct action:" + validActions.toString() + "\n" + Collections.singletonList(actionMap).toString() + "\n" + Arrays.toString(q));
                    //System.out.println(1.0*error/total);
                    return validActions.get(new Random().nextInt(validActions.size()));
                }
            } else {
                System.out.println("Some thing wrong REALLY there:" + validActions.toString() + "\n" + Collections.singletonList(actionMap).toString());
                return validActions.get(0);
            }*/
        } else {
            System.out.println("Some thing wrong there:" + validActions.toString() + "\n" + Collections.singletonList(actionMap).toString());
            return validActions.get(0);
        }
    }
}
