package net.demilich.metastone.game.behaviour.diplom.utils;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.EndTurnAction;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.actions.PhysicalAttackAction;
import net.demilich.metastone.game.behaviour.diplom.qutils.MinionHeuristic;
import net.demilich.metastone.game.entities.minions.Minion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @author ilya2
 *         created on 11.04.2017
 */
public class TrainUnit implements Serializable {
    private Feature sFeatures;
    private int action;
    private double reward;
    private Feature sAFeatures;
    private int[] validActions;

    public TrainUnit(GameContext context, Player player, List<GameAction> validActions, GameAction taken) {
        Pair<Feature, HashMap<Integer, Integer>> featurePair = FeautureExtractor.getFeatures(context, player);
        this.sFeatures = featurePair.left;
        HashMap<Integer, Integer> offset = featurePair.right;
        if (taken instanceof EndTurnAction) {
            this.action = 56;
        } else {
            this.action = convertAttackAction(context, player, validActions, offset).get(taken);
        }
        this.reward = simulateGetReward(context, player, taken, offset) - new MinionHeuristic().getScore(context, player.getId());
        //this.reward = (reward) / 8000.0;
    }

    public TrainUnit(Feature sFeatures, int action, double reward, Feature sAFeatures, int[] validActions) {
        this.sFeatures = sFeatures;
        this.action = action;
        this.reward = reward;
        this.sAFeatures = sAFeatures;
        this.validActions = validActions;
    }

    private double simulateGetReward(GameContext context, Player player, GameAction taken, HashMap<Integer, Integer> offset) {
        if (taken instanceof EndTurnAction) {
            GameContext simulation = context.clone();
            simulation.performAction(player.getId(), new EndTurnAction());
            simulation.playOneTurn();
            this.sAFeatures = FeautureExtractor.getFeatures(simulation, player, offset);
            double score = new MinionHeuristic().getScore(simulation, player.getId());
            Collection<Integer> temp = convertAttackAction(simulation, simulation.getActivePlayer(), simulation.getValidActions(), offset).values();
            Object[] lul = temp.toArray();
            this.validActions = new int[temp.size()];
            for (int i = 0; i < temp.size(); i++) {
                this.validActions[i] = (int) lul[i];
            }
            simulation.dispose();
            return score;

        } else {
            GameContext simulation = context.clone();
            simulation.getLogic().performGameAction(player.getId(), taken);
            double score = new MinionHeuristic().getScore(simulation, player.getId());
            this.sAFeatures = FeautureExtractor.getFeatures(simulation, player, offset);
            Collection<Integer> temp = convertAttackAction(simulation, simulation.getActivePlayer(), simulation.getValidActions(), offset).values();
            Object[] lul = temp.toArray();
            this.validActions = new int[temp.size()];
            for (int i = 0; i < temp.size(); i++) {
                this.validActions[i] = (int) lul[i];
            }
            simulation.dispose();
            return score;
        }
    }

    private ArrayList<Integer> getValidActions(GameContext context, Player player, List<GameAction> validActions) {
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
            } else if (action instanceof EndTurnAction) {
                answer.add(56);
            }
        }
        return answer;
    }

    private HashMap<GameAction, Integer> convertAttackAction(GameContext context, Player player, List<GameAction> validActions, HashMap<Integer, Integer> offset) {
        HashMap<GameAction, Integer> answer = new HashMap<>();
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
                if (j == 7) {
                    try {
                        int adddef = (offset.get(attackerId) - 1) / 6;
                        answer.put(action, (adddef * 8) + j);
                    } catch (Exception e) {
                        System.out.println("lul");
                    }
                } else {
                    try {
                        int adddef = (offset.get(attackerId) - 1) / 6;
                        int addatt = (offset.get(defenderId) - 2 - 7 * 6) / 6;
                        answer.put(action, (adddef * 8) + (addatt));
                    } catch (Exception e) {
                        System.out.println("lul");
                    }
                }
            } else {
                if (action instanceof EndTurnAction) {
                    answer.put(action, 56);
                }
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

    public int[] getValidActions() {
        return validActions;
    }

    public Feature getSFeatures() {
        return sFeatures;
    }

    public int getAction() {
        return action;
    }

    public double getReward() {
        return reward;
    }

    public Feature getSAFeatures() {
        return sAFeatures;
    }
}
