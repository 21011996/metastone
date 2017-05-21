package net.demilich.metastone.game.behaviour.diplom.datasetPrep;

import net.demilich.metastone.game.behaviour.diplom.utils.Feature;
import net.demilich.metastone.game.behaviour.threat.ThreatLevel;

import java.io.Serializable;

/**
 * Created by ilya2 on 21.05.2017.
 */
public class FeatureAndColor implements Serializable {
    Feature feature;
    ThreatLevel color;

    public FeatureAndColor(Feature feature, ThreatLevel color) {
        this.feature = feature;
        this.color = color;
    }
}
