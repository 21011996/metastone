package net.demilich.metastone.game.behaviour.diplom.datasetPrep;

import net.demilich.metastone.game.behaviour.diplom.utils.Feature;

import java.io.Serializable;

/**
 * Created by ilya2 on 20.05.2017.
 */
public class DataPoint implements Serializable {
    public Feature feature;
    public double label;

    public DataPoint(Feature feature, double label) {
        this.feature = feature;
        this.label = label;
    }
}
