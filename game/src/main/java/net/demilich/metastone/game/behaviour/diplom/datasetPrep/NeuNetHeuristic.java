package net.demilich.metastone.game.behaviour.diplom.datasetPrep;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.behaviour.diplom.network.Net;
import net.demilich.metastone.game.behaviour.diplom.utils.Activation;
import net.demilich.metastone.game.behaviour.diplom.utils.Feature;
import net.demilich.metastone.game.behaviour.diplom.utils.FeautureExtractor;
import net.demilich.metastone.game.behaviour.heuristic.IGameStateHeuristic;

import java.io.File;

import static net.demilich.metastone.game.behaviour.diplom.Consts.FEATURE_SIZE;

/**
 * Created by ilya2 on 20.05.2017.
 */
public class NeuNetHeuristic implements IGameStateHeuristic {

    String prefix = "simple";

    private Net network = new Net(new int[]{FEATURE_SIZE, 300, 1}, new Activation[]{Activation.SIGMOID, Activation.SIGMOID, Activation.LINEAR});

    public NeuNetHeuristic() {
        this.network.initWeights(new File[]{
                new File("NN", prefix + "w0.txt"),
                new File("NN", prefix + "w1.txt")
        });
    }

    @Override
    public double getScore(GameContext context, int playerId) {
        double answer = getOutput(FeautureExtractor.getFeatures(context, context.getPlayer(playerId)).left);
            return answer;
    }

    public double getOutput(Feature feature) {
        return network.classify(feature)[1];
    }

    @Override
    public void onActionSelected(GameContext context, int playerId) {

    }
}
