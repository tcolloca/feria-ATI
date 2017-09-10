package com.goodengineer.atibackend.transformation.key_points;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.util.KeypointsUtils;
import com.goodengineer.atibackend.util.Point;

import java.util.List;

public class HarrisTransformation implements Transformation {

	private final double percentage;

	public HarrisTransformation(double percentage) {
		this.percentage = percentage;
	}

	@Override
	public void transform(Band band) {
		List<Point> keyPoints = Harris.findKeyPoints(band, percentage);
		KeypointsUtils.paintPoints(band, new int[] {0, 0, 255}, keyPoints);
	}
}
