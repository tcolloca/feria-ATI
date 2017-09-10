package com.goodengineer.atibackend.transformation.noise;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.util.DistributionRandom;

public class SaltAndPepperNoiseTransformation implements Transformation {

    private final double percentage;
    private final double salt;
    private final double pepper;

    public SaltAndPepperNoiseTransformation(double percentage, double salt, double pepper) {
        this.percentage = percentage;
        this.salt = salt;
        this.pepper = pepper;
    }

    @Override
    public void transform(Band band) {
        double min = band.getValidMin();
        double max = band.getValidMax();
        for (int x = 0; x < band.getWidth(); x++) {
            for (int y = 0; y < band.getHeight(); y++) {
                if (DistributionRandom.nextUniform(0, 100) <= percentage) {
                    double p = DistributionRandom.nextUniform(0, 1);
                    double color = band.getRawPixel(x, y);
                    if (p <= salt) {
                        color = max;
                    } else if (p >= pepper) {
                        color = min;
                    }
                    band.setRawPixel(x, y, color);
                }
            }
        }
    }
}
