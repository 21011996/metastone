package net.demilich.metastone.game.behaviour.diplom.pythonBridge;

import net.demilich.metastone.game.behaviour.diplom.utils.TrainUnit;

/**
 * @author ilya2
 *         created on 14.04.2017
 */
public class EntryPoint {
    public NNInterface nnInterface;

    public void registerPython(NNInterface nnInterface) {
        this.nnInterface = nnInterface;
        System.out.println("Connection received");
    }

    public void print() {
        System.out.println("kekeke2");
    }

    public void sendTrainUnit(TrainUnit trainUnit) {
        nnInterface.add(trainUnit.getSFeatures().x, trainUnit.getAction(), trainUnit.getReward(), trainUnit.getSAFeatures().x);
    }
}
