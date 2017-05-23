package net.demilich.metastone.game.behaviour.diplom.datasetPrep;

import net.demilich.metastone.game.behaviour.diplom.network.Net;
import net.demilich.metastone.game.behaviour.diplom.utils.Activation;
import net.demilich.metastone.game.behaviour.diplom.utils.DataInstance;
import net.demilich.metastone.game.behaviour.diplom.utils.Feature;
import net.demilich.metastone.game.behaviour.diplom.utils.Params;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static net.demilich.metastone.game.behaviour.diplom.Consts.FEATURE_SIZE;

/**
 * Created by ilya2 on 20.05.2017.
 */
public class TrainNNStation {
    private static final Params BEST_PARAMS = new Params(0.001, 0, 1, 0);
    String saveName = "1000";
    String dsName = "97195";
    Random random = new Random();
    private Net network = new Net(new int[]{FEATURE_SIZE, 1000, 1}, new Activation[]{Activation.SIGMOID, Activation.SIGMOID, Activation.LINEAR});

    public TrainNNStation() {
        network.initWeights();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new TrainNNStation().run();
    }

    public void saveNet() {
        for (int i = 0; i < network.weights.length; i++) {
            network.weights[i].writeFile("NN\\" + saveName + "w" + i + ".txt");
        }
    }

    public void train(ArrayList<DataPoint> dataPoints) {
        Collections.shuffle(dataPoints);
        int part = dataPoints.size() / 10;
        List<DataPoint> testList = dataPoints.subList(0, part);
        List<DataPoint> trainList = dataPoints.subList(part + 1, dataPoints.size());
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < trainList.size(); i++) {
            indexes.add(i);
        }
        int iter = 0;
        while (true) {
            iter++;
            int i = indexes.get(random.nextInt(indexes.size()));
            DataPoint dataPoint = trainList.get(i);
            if (Math.abs(getOutput(dataPoint.feature) - dataPoint.label) > 0.1) {
                network.learnStep(new DataInstance(dataPoint.feature, new double[]{-1.0, dataPoint.label}), BEST_PARAMS);
                indexes.add(i);
            }

            if (iter % 100000 == 0) {
                double test = testNetwork(testList);
                System.out.println(String.format("%d %f", iter, test));
                if (test < 0.4) {
                    break;
                }
                saveNet();
            }
        }
        saveNet();
    }

    public void run() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream("dataset\\dump\\" + dsName + ".ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList<DataPoint> dataPoints = (ArrayList<DataPoint>) ois.readObject();
        train(dataPoints);
        //sanitytest(dataPoints.get(0).feature, dataPoints.get(100).feature);
    }

    public void sanitytest(Feature x, Feature y) {
        for (int i = 0; i < 10000; i++) {
            network.learnStep(new DataInstance(x, new double[]{-1.0, -1000.0}), BEST_PARAMS);
            network.learnStep(new DataInstance(y, new double[]{-1.0, 1000.0}), BEST_PARAMS);
        }
        System.out.println(getOutput(x));
        System.out.println(getOutput(y));
    }

    public double getOutput(Feature feature) {
        double[] output = network.classify(feature);
        return output[1];
    }

    public double testNetwork(List<DataPoint> dataPoints) {
        double answer = 0.0;
        for (DataPoint dataPoint : dataPoints) {
            double temp = getOutput(dataPoint.feature);
            answer += Math.abs(temp - dataPoint.label);
        }
        return answer / dataPoints.size();
    }
}
