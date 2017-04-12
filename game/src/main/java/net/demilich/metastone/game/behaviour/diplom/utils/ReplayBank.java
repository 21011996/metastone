package net.demilich.metastone.game.behaviour.diplom.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author ilya2
 *         created on 11.04.2017
 */
public class ReplayBank {
    private static ArrayList<TrainUnit> trainUnits = new ArrayList<>();
    private static boolean storeOnDisk = false;
    private static int count = 0;
    private static Random random = new Random();

    public static void addTrainUnit(TrainUnit trainUnit) {
        trainUnits.add(trainUnit);
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
    }

    public static ArrayList<TrainUnit> getBatch(int size) {
        ArrayList<TrainUnit> answer = new ArrayList<>();
        if (size < getSize()) {
            for (int i = 0; i < size; i++) {
                answer.add(trainUnits.get(random.nextInt(trainUnits.size())));
            }
        }
        return answer;
    }

    public static int getSize() {
        return trainUnits.size();
    }
}
