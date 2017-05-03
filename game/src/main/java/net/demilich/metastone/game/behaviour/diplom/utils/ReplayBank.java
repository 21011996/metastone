package net.demilich.metastone.game.behaviour.diplom.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static net.demilich.metastone.game.behaviour.diplom.Consts.*;

/**
 * @author ilya2
 *         created on 11.04.2017
 */
public class ReplayBank {
    public static ArrayList<TrainUnit> trainUnits = new ArrayList<>();
    public static int[] counter;
    public static double maxValue = 0.0;
    private static boolean storeOnDisk = false;
    private static int count = 0;
    private static Random random = new Random();

    public static boolean addTrainUnit(TrainUnit trainUnit) {
        boolean save = false;
        if (count % 10000 == 0 && count > LEARNING_START + 1) {
            save = true;
        }
        if (counter == null) {
            counter = new int[NUMBER_OF_ACTIONS];
        }
        trainUnits.add(trainUnit);
        if (trainUnits.size() > BUFFER_SIZE) {
            trainUnits.remove(0);
        }
        counter[trainUnit.getAction()]++;
        count++;
        if (storeOnDisk) {
            try {
                ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream("dataset\\" + count + ".ser"));
                oout.writeObject(trainUnit);
                oout.flush();
                oout.close();
            } catch (IOException e) {
                System.out.println("Couldn't store file " + "dataset\\" + count + ".ser");
            }
        }
        return save;
    }

    public static void printProfile() {
        System.out.println(Arrays.toString(counter));
    }

    public static ArrayList<TrainUnit> getBatch(int size) {
        ArrayList<TrainUnit> answer = new ArrayList<>();
        if (getSize() >= LEARNING_START) {
            while (answer.size() < size) {
                TrainUnit trainUnit = trainUnits.get(random.nextInt(trainUnits.size()));
                answer.add(trainUnit);
            }
        }
        return answer;
    }

    public static int getSize() {
        return count;
    }
}
