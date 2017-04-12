package net.demilich.metastone.game.behaviour.diplom.utils;

public class Params {
    public double step;
    public double reg;
    public double lambda;
    public double improve;

    public Params() {
    }

    public Params(double step, double reg, double lambda, double improve) {
        this.step = step;
        this.reg = reg;
        this.lambda = lambda;
        this.improve = improve;
    }

    public Params(Params params) {
        this(params.step, params.reg, params.lambda, params.improve);
    }

    @Override
    public String toString() {
        return String.format("Params: step = %.6f, reg = %.6f, lambda = %.9f, improve = %.6f", step, reg, lambda, improve);
    }
}
