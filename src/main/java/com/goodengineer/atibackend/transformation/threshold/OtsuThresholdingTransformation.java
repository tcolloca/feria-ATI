package com.goodengineer.atibackend.transformation.threshold;

import java.util.ArrayList;
import java.util.List;

import com.goodengineer.atibackend.ImageUtils;
import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;

public class OtsuThresholdingTransformation implements Transformation {

	@Override
	public void transform(Band band) {
		float[] histogram = ImageUtils.createHistogram(band);
		float[] P1 = new float[histogram.length];
		float[] meansAcum = new float[histogram.length];
		P1[0] = histogram[0];
		meansAcum[0] = 0;
		for (int i = 1; i < P1.length; i++) {
			P1[i] = histogram[i] + P1[i - 1];
			meansAcum[i] = i*histogram[i] + meansAcum[i - 1];
		}
		float Mg = meansAcum[255];
		float max = -1;
		List<Integer> maxTs = new ArrayList<>();
		for (int i = 0; i < P1.length; i++) {
			float top = Mg*P1[i] - meansAcum[i];
			float bottom = P1[i]*(1 - P1[i]) + ((float)1e-4);
			float variance = (top*top)/bottom;
			if (variance > max) {
				max = variance;
				maxTs.clear();
				maxTs.add(i);
			} else if (variance == max) {
				maxTs.add(i);
			}
		}
		int tAcum = 0;
		for (Integer t: maxTs) {
			tAcum += t;
		}
		tAcum /= maxTs.size();
		new ThresholdingTransformation(tAcum).transform(band);
	}
}
