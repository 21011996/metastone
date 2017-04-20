package net.demilich.metastone.game.behaviour.diplom.utils;

import java.io.Serializable;

public class Feature implements Serializable {
    public double[] x;
    public int size;

    public Feature(int size) {
        this.x = new double[size];
        this.size = size;
    }

    public Feature(double[] features) {
        this.size = features.length;
        x = new double[size];
        System.arraycopy(features, 0, x, 0, size);
    }

    public Feature(Feature other) {
        this.size = other.size;
        x = new double[other.size()];
        System.arraycopy(other.x, 0, x, 0, size());
    }

    public void set(int i, double v) {
        x[i] = v;
    }

    public int size() {
        return x.length;
    }

    public double get(int i) {
        return x[i];
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        stringBuilder.append(x[i] * 60.0);
        for (int j = 0; j < 7; j++) {
            i++;
            stringBuilder.append(String.format(" (%d/%d)A%dT%dD%d", (int) (x[i + 1] * 12.0), (int) (x[i] * 12.0), x[i + 2] == 0.0 ? 0 : 1, x[i + 3] == 0.0 ? 0 : 1, x[i + 4] == 0.0 ? 0 : 1));
            i += 5;
        }
        i++;
        stringBuilder.append(x[i] * 60.0);
        for (int j = 0; j < 7; j++) {
            i++;
            stringBuilder.append(String.format(" (%d/%d)A%dT%dD%d", (int) (x[i + 1] * 12.0), (int) (x[i] * 12.0), x[i + 2] == 0.0 ? 0 : 1, x[i + 3] == 0.0 ? 0 : 1, x[i + 4] == 0.0 ? 0 : 1));
            i += 5;
        }
        return stringBuilder.toString();
    }

    public void addHead(double v) {
        double[] newX = new double[size() + 1];
        newX[0] = v;
        System.arraycopy(x, 0, newX, 1, x.length);
        x = newX;
    }
}
