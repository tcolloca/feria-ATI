package com.goodengineer.atibackend.util;

import java.util.Random;

public class DistributionRandom {

    private static Random random = new Random();

    public static double nextUniform(double min, double max) {
        return random.nextDouble()*(max - min) + min;
    }

    public static double nextGaussian(double mu, double sigma) {
        return random.nextGaussian()*sigma + mu;
    }

    public static double nextExponential(double lambda) {
        return Math.log(1-random.nextDouble())/(-lambda);
    }

    public static double nextRayleigh(double epsilon) {
        return Math.sqrt(epsilon * Math.log(1 / (1 - random.nextDouble())));
    }
}
