package com.goodengineer.atibackend.model;

import java.util.List;

import com.goodengineer.atibackend.transformation.Transformation;

public interface Image {
	int getWidth();

	int getHeight();

	void transform(Transformation transformation);

	Image clone();

	List<Band> getBands();

	double[] getColor(int x, int y);
}
