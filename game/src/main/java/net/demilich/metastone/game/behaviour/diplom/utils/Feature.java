package net.demilich.metastone.game.behaviour.diplom.utils;

public class Feature {
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

    public void addHead(double v) {
        double[] newX = new double[size() + 1];
        newX[0] = v;
        System.arraycopy(x, 0, newX, 1, x.length);
        x = newX;
    }
}
