package com.goodengineer.atibackend.transformation;

import java.util.LinkedList;

import com.goodengineer.atibackend.model.Band;

public class CompoundTransformation implements Transformation {

	private LinkedList<Transformation> transformations;

	public CompoundTransformation() {
		transformations = new LinkedList<>();
	}

	@Override
	public void transform(Band band) {
		for (Transformation transformation: transformations) {
			transformation.transform(band);
		}
	}

	public void addTransformation(Transformation transformation) {
		transformations.add(transformation);
	}

	/**
	 * This method will help implement the undo feature
	 */
	public void removeLastTransformation() {
		if (!transformations.isEmpty())
			transformations.removeLast();
	}
	
	public void clearTransformations() {
		transformations.clear();
	}
	
	public boolean isEmpty() {
		return transformations.isEmpty();
	}
}
