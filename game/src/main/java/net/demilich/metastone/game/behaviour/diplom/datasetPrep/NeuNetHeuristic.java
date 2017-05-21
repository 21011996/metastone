package net.demilich.metastone.game.behaviour.diplom.datasetPrep;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.behaviour.diplom.network.Net;
import net.demilich.metastone.game.behaviour.diplom.utils.Activation;
import net.demilich.metastone.game.behaviour.diplom.utils.Feature;
import net.demilich.metastone.game.behaviour.diplom.utils.FeautureExtractor;
import net.demilich.metastone.game.behaviour.heuristic.IGameStateHeuristic;
import net.demilich.metastone.game.behaviour.threat.ThreatBasedHeuristic;
import net.demilich.metastone.game.behaviour.threat.ThreatLevel;

import java.io.File;
import java.util.HashMap;

import static net.demilich.metastone.game.behaviour.diplom.Consts.FEATURE_SIZE;

/**
 * Created by ilya2 on 20.05.2017.
 */
public class NeuNetHeuristic implements IGameStateHeuristic {
    private static HashMap<Integer, Double> calledValues = new HashMap<>();

    private Net green = new Net(new int[]{FEATURE_SIZE, 100, 100, 100, 1}, new Activation[]{Activation.SIGMOID, Activation.SIGMOID, Activation.SIGMOID, Activation.SIGMOID, Activation.LINEAR});
    private Net yellow = new Net(new int[]{FEATURE_SIZE, 100, 100, 100, 1}, new Activation[]{Activation.SIGMOID, Activation.SIGMOID, Activation.SIGMOID, Activation.SIGMOID, Activation.LINEAR});
    private Net red = new Net(new int[]{FEATURE_SIZE, 100, 100, 100, 1}, new Activation[]{Activation.SIGMOID, Activation.SIGMOID, Activation.SIGMOID, Activation.SIGMOID, Activation.LINEAR});

    public NeuNetHeuristic() {
        this.green.initWeights(new File[]{
                new File("NN", "greenw0.txt"),
                new File("NN", "greenw1.txt"),
                new File("NN", "greenw2.txt"),
                new File("NN", "greenw3.txt")
        });
        this.yellow.initWeights(new File[]{
                new File("NN", "yelloww0.txt"),
                new File("NN", "yelloww1.txt"),
                new File("NN", "yelloww2.txt"),
                new File("NN", "yelloww3.txt")
        });
        this.red.initWeights(new File[]{
                new File("NN", "redw0.txt"),
                new File("NN", "redw1.txt"),
                new File("NN", "redw2.txt"),
                new File("NN", "redw3.txt")
        });
    }

    @Override
    public double getScore(GameContext context, int playerId) {
        if (calledValues.containsKey(context.hashCode())) {
            return calledValues.get(context.hashCode());
        } else {
            ThreatLevel threatLevel = ThreatBasedHeuristic.calcuateThreatLevel(context, playerId);
            double answer = getOutput(FeautureExtractor.getFeatures(context, context.getPlayer(playerId)).left, threatLevel);
            switch (threatLevel) {
                case GREEN:
                    answer += 50.0;
                    break;
                case YELLOW:
                    answer += 30.0;
                    break;
                case RED:
                    answer += 10.0;
                    break;
            }
            calledValues.put(context.hashCode(), answer);
            return answer;
        }
    }

    public double getOutput(Feature feature, ThreatLevel threatLevel) {
        switch (threatLevel) {
            case GREEN:
                return green.classify(feature)[1];
            case YELLOW:
                return yellow.classify(feature)[1];
            case RED:
                return red.classify(feature)[1];
            default:
                System.out.println("qtd");
                return -666.0;
        }
    }

    @Override
    public void onActionSelected(GameContext context, int playerId) {

    }
}
