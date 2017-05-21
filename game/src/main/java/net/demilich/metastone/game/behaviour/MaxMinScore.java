package net.demilich.metastone.game.behaviour;

/**
 * @author ilya2
 *         created on 10.04.2017
 */
public class MaxMinScore {
    private static double max = Float.MIN_VALUE;
    private static double min = Float.MAX_VALUE;

    public static synchronized void addValue(double value) {
        if (value > max) {
            System.out.println(String.format("%f %f", min, max));
            max = value;
        }
        if (value < min) {
            System.out.println(String.format("%f %f", min, max));
            min = value;
        }
    }
}
