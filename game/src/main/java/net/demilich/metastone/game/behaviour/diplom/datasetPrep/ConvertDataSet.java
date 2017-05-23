package net.demilich.metastone.game.behaviour.diplom.datasetPrep;

import net.demilich.metastone.game.behaviour.diplom.utils.Feature;
import net.demilich.metastone.game.behaviour.threat.ThreatLevel;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ilya2 on 20.05.2017.
 */
public class ConvertDataSet {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ArrayList<DataPoint> green = new ArrayList<>();
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        File folder = new File("dataset\\big");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                FileInputStream fi = new FileInputStream(file);
                ObjectInputStream oi = new ObjectInputStream(fi);
                ArrayList<DataPoint> list = (ArrayList<DataPoint>) oi.readObject();
                list = list.stream().filter(lul -> lul.label != Double.NEGATIVE_INFINITY && lul.label != Double.POSITIVE_INFINITY).collect(Collectors.toCollection(ArrayList::new));
                for (DataPoint dataPoint : list) {
                    double value = dataPoint.label;
                    if (value != Double.NEGATIVE_INFINITY && value != Double.POSITIVE_INFINITY) {
                        max = Math.max(max, value);
                        min = Math.min(min, value);
                    } else {
                        if (value == Double.NEGATIVE_INFINITY) {
                            System.out.println("lle");
                            dataPoint.label = -250.0;
                        } else {
                            System.out.println("lle2");
                            dataPoint.label = 250.0;
                        }
                    }
                }
                green.addAll(list);
            }
        }
        System.out.println(max + " " + min);
        printSet(green, "");
    }

    public static void printSet(List<DataPoint> dataPoints, String color) throws IOException {
        Collections.shuffle(dataPoints);
        FileOutputStream fos = new FileOutputStream("dataset\\dump\\" + color + dataPoints.size() + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(dataPoints);
        oos.close();
        fos.close();
    }

    public static ArrayList<DataPoint> markFeatures(List<Feature> features, double starter) {
        ArrayList<DataPoint> answer = new ArrayList<>();
        double j = starter;
        for (int i = features.size() - 1; i >= 0; i--) {
            answer.add(new DataPoint(features.get(i), j));
            j *= 0.8;
        }
        return answer;
    }

    public static double compareColor(ThreatLevel o1, ThreatLevel o2) {
        switch (o1) {
            case GREEN:
                switch (o2) {
                    case GREEN:
                        return 0.0;
                    case YELLOW:
                        return -10.0;
                    case RED:
                        return -20.0;
                }
            case YELLOW:
                switch (o2) {
                    case GREEN:
                        return 10.0;
                    case YELLOW:
                        return 0.0;
                    case RED:
                        return -10.0;
                }
            case RED:
                switch (o2) {
                    case GREEN:
                        return 20.0;
                    case YELLOW:
                        return 10.0;
                    case RED:
                        return 0.0;
                }
            default:
                return -666.0;
        }
    }
}
