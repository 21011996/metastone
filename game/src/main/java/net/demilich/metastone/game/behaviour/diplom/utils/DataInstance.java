package net.demilich.metastone.game.behaviour.diplom.utils;

public class DataInstance {
    public Feature feature;
    public double[] qValue;

    public DataInstance(Feature feature, double[] label) {
        this.feature = feature;
        this.qValue = label;
    }
}
