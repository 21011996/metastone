package net.demilich.metastone.game.behaviour.diplom.pythonBridge;

import java.util.ArrayList;

/**
 * @author ilya2
 *         created on 14.04.2017
 */
public interface NNInterface {
    void learn();

    void add(double[] s, int a, double r, double[] sa);

    ArrayList<Double> classify(double[] f);
}
