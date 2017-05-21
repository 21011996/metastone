package net.demilich.metastone.game.behaviour.diplom.datasetPrep;

import net.demilich.metastone.game.behaviour.diplom.utils.Feature;
import net.demilich.metastone.game.behaviour.threat.ThreatLevel;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ilya2 on 20.05.2017.
 */
public class ConvertDataSet {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ArrayList<DataPoint> green = new ArrayList<>();
        ArrayList<DataPoint> yellow = new ArrayList<>();
        ArrayList<DataPoint> red = new ArrayList<>();
        File folder = new File("dataset\\big");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                FileInputStream fi = new FileInputStream(file);
                ObjectInputStream oi = new ObjectInputStream(fi);
                ArrayList<FeatureAndColor> list = (ArrayList<FeatureAndColor>) oi.readObject();
                List<Feature> temp = new ArrayList<>();
                ThreatLevel last = ThreatLevel.GREEN;
                int i = list.size();
                for (FeatureAndColor fac : list) {
                    i--;
                    temp.add(fac.feature);
                    if (fac.color != last) {
                        switch (last) {
                            case GREEN:
                                green.addAll(markFeatures(temp, compareColor(last, fac.color)));
                                break;
                            case YELLOW:
                                yellow.addAll(markFeatures(temp, compareColor(last, fac.color)));
                                break;
                            case RED:
                                red.addAll(markFeatures(temp, compareColor(last, fac.color)));
                                break;
                        }
                        last = fac.color;
                        temp = new ArrayList<>();
                        temp.add(fac.feature);
                    }
                    if (i == 0) {
                        switch (last) {
                            case GREEN:
                                green.addAll(markFeatures(temp, file.getName().contains("_l") ? -10.0 : 10.0));
                                break;
                            case YELLOW:
                                yellow.addAll(markFeatures(temp, file.getName().contains("_l") ? -10.0 : 10.0));
                                break;
                            case RED:
                                red.addAll(markFeatures(temp, file.getName().contains("_l") ? -10.0 : 10.0));
                                break;
                        }
                    }
                }
            }
        }
        printSet(green, "green");
        printSet(yellow, "yellow");
        printSet(red, "red");
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
