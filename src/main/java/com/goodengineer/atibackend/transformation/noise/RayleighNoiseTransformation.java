package com.goodengineer.atibackend.transformation.noise;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.util.DistributionRandom;

public class RayleighNoiseTransformation implements Transformation {

    private final double percentage;
    private final double epsilon;

    public RayleighNoiseTransformation(double percentage, double epsilon) {
        this.percentage = percentage;
        this.epsilon = epsilon;
    }

    @Override
    public void transform(Band band) {
        for (int x = 0; x < band.getWidth(); x++) {
            for (int y = 0; y < band.getHeight(); y++) {
                if (DistributionRandom.nextUniform(0, 100) <= percentage) {
                    band.setRawPixel(x, y, band.getRawPixel(x, y) * DistributionRandom.nextRayleigh(epsilon));
                }
            }
        }
    }
}
