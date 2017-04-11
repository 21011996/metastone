package net.demilich.metastone.game.behaviour.learning;

import net.demilich.metastone.game.GameContext;

public interface IBrain {

    double getEstimatedUtility(double[] output);

    double[] getOutput(GameContext context, int playerId);

    boolean isLearning();

    void setLearning(boolean learning);

    void learn(GameContext originalState, int playerId, double[] nextOutput, double reward);

    void load(String savePath);

    void save(String savePath);

}