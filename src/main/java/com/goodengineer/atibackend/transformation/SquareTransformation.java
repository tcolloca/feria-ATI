package com.goodengineer.atibackend.transformation;

import com.goodengineer.atibackend.model.Band;

public class SquareTransformation implements Transformation {

	@Override
	public void transform(Band band) {
		Transformation multiplyTransformation = new MultiplyImageTransformation(band);
		multiplyTransformation.transform(band);
	}
}
