package com.goodengineer.atibackend.transformation.noise;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.util.DistributionRandom;

public class ExponentialNoiseTransformation implements Transformation {

    private final double percentage;
    private final double lambda;

    public ExponentialNoiseTransformation(double percentage, double lambda) {
        this.percentage = percentage;
        this.lambda = lambda;
    }

    @Override
    public void transform(Band band) {
        for (int x = 0; x < band.getWidth(); x++) {
            for (int y = 0; y < band.getHeight(); y++) {
                if (DistributionRandom.nextUniform(0, 100) <= percentage) {
                    band.setRawPixel(x, y, band.getRawPixel(x, y) * DistributionRandom.nextExponential(lambda));
                }
            }
        }
    }
}
