package net.demilich.metastone.game.behaviour.diplom.network;

import net.demilich.metastone.game.behaviour.diplom.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

public class Net {
    public static final Logger logger = LoggerFactory.getLogger(Net.class);

    private static final int CV = 5;

    private static final double EPSILON = 1E-7;
    private static final int DELTA_SIZE = 5;
    private static final int MAX_ITERATIONS = 1000000;

    private static final double[] RATES = new double[]{7E-1};
    private static final double[] REGS = new double[]{0};
    private static final double[] IMPROVE = new double[]{0};

    public final int[] sizes;
    public final Activation[] sigmas;
    public Weight[] weights;

    public Net(int[] sizes, Activation[] sigmas) {
        this.sizes = sizes;
        this.sigmas = sigmas;
        logger.debug("Net was created with sizes: {}", Arrays.toString(sizes));
    }

    public Net(int[] sizes, Activation sigmas) {
        this.sizes = sizes;
        this.sigmas = new Activation[sizes.length];
        Arrays.fill(this.sigmas, sigmas);
        initWeights();
        logger.debug("Net was created with sizes: {}", Arrays.toString(sizes));
    }

    public void initWeights() {
        logger.debug("Initializing weights");
        weights = new Weight[sizes.length - 1];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = new Weight(sizes[i] + 1, sizes[i + 1] + 1);
        }
    }

    public void initWeights(Weight[] weights) {
        this.weights = weights;
    }

    public void initWeights(File[] files) {
        logger.debug("Initializing weights");
        weights = new Weight[sizes.length - 1];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = new Weight(sizes[i] + 1, sizes[i + 1] + 1, files[i]);
        }
    }

    public double[] evaluateLayer(double[] input, Weight w, int outputSize, Activation sigma) {
        double[] ui = new double[outputSize + 1];
        ui[0] = -1d;
        for (int h = 1; h <= outputSize; h++) {
            double sum = 0d;
            for (int j = 0; j < input.length; j++) {
                sum += w.get(j, h) * input[j];
            }
            ui[h] = sigma.get().apply(sum);
        }
        return ui;
    }

    public double learnStep(DataInstance data, Params params) {
        // forward step
        Feature xit = new Feature(data.feature);
        xit.addHead(-1d);

        double[][] ui = new double[sizes.length][];
        ui[0] = xit.x;
        for (int i = 1; i < sizes.length; i++) {
            ui[i] = evaluateLayer(ui[i - 1], weights[i - 1], sizes[i], sigmas[i]);
        }

        double[][] errors = new double[sizes.length][];

        double qi = 0d;
        errors[errors.length - 1] = new double[ui[ui.length - 1].length];
        for (int m = 0; m < ui[ui.length - 1].length; m++) {
            //TODO ensure this works
            errors[errors.length - 1][m] = ui[ui.length - 1][m] - data.qValue[m];
            qi += errors[errors.length - 1][m] * errors[errors.length - 1][m];
        }

        for (int i = ui.length - 2; i >= 0; i--) {
            double[] sm = new double[sizes[i + 1] + 1];
            for (int m = 0; m < sizes[i + 1] + 1; m++) {
                double s = 0d;
                for (int hs = 0; hs < ui[i].length; hs++) {
                    s += weights[i].get(hs, m) * ui[i][hs];
                }
                sm[m] = sigmas[i + 1].getDiff().apply(s);
            }
            errors[i] = new double[ui[i].length + 1];
            for (int h = 0; h < ui[i].length; h++) {
                double sum = 0d;
                for (int m = 0; m < sizes[i + 1] + 1; m++) {
                    sum += errors[i + 1][m] * sm[m] * weights[i].get(h, m);
                }
                errors[i][h] = sum;
            }
        }

        for (int i = ui.length - 2; i >= 0; i--) {
            double[] sm = new double[sizes[i + 1] + 1];
            for (int m = 0; m < sizes[i + 1] + 1; m++) {
                double s = 0d;
                for (int hs = 0; hs < ui[i].length; hs++) {
                    s += weights[i].get(hs, m) * ui[i][hs];
                }
                sm[m] = sigmas[i + 1].getDiff().apply(s);
            }
            for (int h = 0; h < ui[i].length; h++) {
                for (int m = 0; m < sizes[i + 1] + 1; m++) {
                    weights[i].set(h, m, weights[i].get(h, m) * (1 - params.rate * params.reg) - params.rate * errors[i + 1][m] * sm[m] * ui[i][h]);
                }
            }

        }
        return qi;
    }

    public double[] classify(Feature x) {

        Feature xit = new Feature(x);
        xit.addHead(-1d);

        double[][] ui = new double[sizes.length][];
        ui[0] = xit.x;
        for (int i = 1; i < sizes.length; i++) {
            ui[i] = evaluateLayer(ui[i - 1], weights[i - 1], sizes[i], sigmas[i]);
        }

        return ui[ui.length - 1];
    }


}