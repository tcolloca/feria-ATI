package com.goodengineer.atibackend.transformation.threshold;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.util.FilterUtils;
import com.goodengineer.atibackend.util.Statistics;

import model.ImageManager;

public class SauvolaThresholdingTransformation implements Transformation {

	private final double k;
	private final int R;
	private final int b;
	private final ImageManager imageManager;
	
	public SauvolaThresholdingTransformation(ImageManager imageManager, double k, int r, int b) {
		super();
		this.k = k;
		R = r;
		this.b = b;
		this.imageManager = imageManager;
	}

	public SauvolaThresholdingTransformation(double k, int r, int b) {
		this(null, k, r, b);
	}
	
	@Override
	public void transform(Band band) {
		Band original = band.clone();
		int i = 0;
		
		ExecutorService executor = Executors.newFixedThreadPool(5);
		CompletionService<Void> completionService = new ExecutorCompletionService<>(executor);
		
		int threads = 4;
		
		int length = (int) Math.ceil(band.getWidth() / (double) threads);
		for (int x = 0; x < band.getWidth(); x += length) {
			int endCol = Math.min(band.getWidth(), x + length);
			completionService.submit(new PaintPixelRunnable(imageManager, original, band, x, endCol), null);
		}
		
		int taken = 0;
		while (taken++ < threads) {
			try {
				completionService.take().get();
			} catch (InterruptedException | ExecutionException e) {
			}
		}
		
		executor.shutdown();
	}
	
	public class PaintPixelRunnable implements Runnable {
		private ImageManager imageManager;
		private Band original;
		private Band band;
		private int startCol;
		private int endCol;

		public PaintPixelRunnable(ImageManager imageManager, Band original, Band band, int startCol, int endCol) {
			super();
			this.imageManager = imageManager;
			this.original = original;
			this.band = band;
			this.startCol = startCol;
			this.endCol = endCol;
		}

		@Override
		public void run() {
			for (int x = startCol; x < endCol; x++) {
				for (int y = 0; y < band.getHeight(); y++) {
					double[] pixels = FilterUtils.getPixelsInMask(original, x, y, b);
					Statistics stats = new Statistics(pixels);
					double threshold = stats.getMean() + Math.round(1 + k * (stats.getVariance() / R - 1));
					if (original.getPixel(x, y) < threshold + 3) {
						band.setPixel(x, y, 0);
					} else {
						band.setPixel(x, y, 255);
					}
				}
			}
			imageManager.update(band);
		}
		
		
	} 
}
