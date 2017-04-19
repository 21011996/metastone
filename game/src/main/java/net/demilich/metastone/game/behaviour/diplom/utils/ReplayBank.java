package net.demilich.metastone.game.behaviour.diplom.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
        if (trainUnits.size() % 10000 == 0) {
            save = true;
        }
        if (counter == null) {
            counter = new int[57];
        }
        trainUnits.add(trainUnit);
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
        int counter = 0;
        if (size < getSize()) {
            while (answer.size() < size) {
                TrainUnit trainUnit = trainUnits.get(random.nextInt(trainUnits.size()));
                if (trainUnit.getAction() == 56 && counter < 4) {
                    counter++;
                    answer.add(trainUnit);
                } else if (trainUnit.getAction() != 56) {
                    answer.add(trainUnit);
                }
            }
        }
        return answer;
    }

    public static int getSize() {
        return trainUnits.size();
    }
}
