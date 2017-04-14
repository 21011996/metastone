package net.demilich.metastone.game.behaviour.diplom.pythonBridge;

import net.demilich.metastone.game.behaviour.diplom.utils.TrainUnit;
import py4j.GatewayServer;

/**
 * @author ilya2
 *         created on 14.04.2017
 */
public class PythonBridge {
    public static EntryPoint entryPoint;
    public NNInterface nnInterface;

    public static void main(int lul) {
        System.out.println(NNInterface.class.getCanonicalName());
        EntryPoint entryPoint = new EntryPoint();
        PythonBridge.entryPoint = entryPoint;
        GatewayServer gatewayServer = new GatewayServer(entryPoint, 1234);
        gatewayServer.start();
        System.out.println("Required connection");
    }

    public void registerPython(NNInterface nnInterface) {
        this.nnInterface = nnInterface;
        System.out.println("Connection received");
    }

    public void print() {
        System.out.println("kekeke");
    }

    public void sendTrainUnit(TrainUnit trainUnit) {
        nnInterface.add(trainUnit.getSFeatures().x, trainUnit.getAction(), trainUnit.getReward(), trainUnit.getSAFeatures().x);
    }
}
