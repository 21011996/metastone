package net.demilich.metastone.game.behaviour.diplom.utils;

import java.util.function.Function;

public enum Activation {
    SIGMOID,
    TANH,
    ReLU,
    LReLU,
    ELU,
    LINEAR;

    public Function<Double, Double> get() {
        switch (this) {
            case SIGMOID:
                return x -> 1d / (1d + Math.exp(-x));
            case TANH:
                return Math::tanh;
            case ReLU:
                return x -> Math.max(0d, x);
            case LReLU:
                return x -> x < 0d ? 0.01d * x : x;
            case LINEAR:
                return x -> x;
            case ELU:
                return x -> x < 0d ? Math.exp(x) - 1d : x;
        }
        return null;
    }

    public Function<Double, Double> getDiff() {
        final Function<Double, Double> sigma = this.get();
        assert sigma != null;
        switch (this) {
            case SIGMOID:
                return x -> sigma.apply(x) * (1d - sigma.apply(x));
            case TANH:
                return x -> 1d - Math.pow(sigma.apply(x), 2);
            case ReLU:
                return x -> x < 0d ? 0d : 1d;
            case LReLU:
                return x -> x < 0d ? 0.01d : 1.0d;
            case LINEAR:
                return x -> 1d;
            case ELU:
                return x -> x < 0d ? sigma.apply(x) + 1d : 1d;
        }
        return null;
    }


}
