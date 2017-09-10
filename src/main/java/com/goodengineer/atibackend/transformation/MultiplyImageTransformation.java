package com.goodengineer.atibackend.transformation;

import com.goodengineer.atibackend.model.Band;

public class MultiplyImageTransformation implements Transformation {

    private Band srcBand;

    public MultiplyImageTransformation(Band srcBand) {
        this.srcBand = srcBand;
    }

    // TODO: Check if this is what is expected.
    @Override
    public void transform(Band destBand) {
        if (destBand.getWidth() != srcBand.getWidth() || destBand.getHeight() != srcBand.getHeight()) {
            throw new IllegalStateException("Images aren't size compatible.");
        }
        for (int x = 0; x < destBand.getWidth(); x++) {
            for (int y = 0; y < destBand.getHeight(); y++) {
                destBand.setRawPixel(x, y, srcBand.getRawPixel(x, y) * destBand.getRawPixel(x, y));
            }
        }
    }
}
