package util.pf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.goodengineer.atibackend.transformation.DecreaseResolutionTransformation;

import model.ImageManager;
import util.FileHelper;
import util.SiftMatcher;
import util.Statistics;

public class SarExperiments {

	private static final int EXPERIMENTS = 200;
	private final ImageManager imageManager;

	public SarExperiments(ImageManager imageManager) {
		super();
		this.imageManager = imageManager;
	}

	public void runAlphaAndGamma() throws IOException {
		double alpha1 = -1.5;
		double gamma1 = 1;

		double gamma2 = 1;
		double alpha2 = -3;

		double[] alphas = new double[] { -3, -4, -5, -10 };
		double[] gammas = new double[] { 0.1, 1, 10, 100, 1000 };
		int[] Ls = new int[] { 1, 3, 5, 8 };

		String shapesPath = "E:\\BACKUP Data\\Tom\\projects\\ATI-GUI-Desktop\\shape_original.png";
		File shapesFile = new File(shapesPath);
		File csvFile = new File("experiments.csv");
		csvFile.createNewFile();
		PrintWriter writer = new PrintWriter(csvFile);

//		for (double alpha : alphas) {
//			for (int L : Ls) {
//				writer.write(String.format("alpha=%g;gamma=%g;L=%d;\n", alpha, gamma2, L));
//				List<Double> goodMatches = new ArrayList<>();
//				List<Double> badMatches = new ArrayList<>();
//				for (int i = 0; i < EXPERIMENTS; i++) {
//					imageManager.setImageFile(shapesFile);
//					imageManager.createSyntheticImageFromOriginal(L, alpha1, gamma1, alpha, gamma2);
//					BufferedImage noiseImage = imageManager.getModifiableBufferedImage();
//					String noiseImagePath = String.format("shapes/shapes_%g_%g_%d_%d.png", alpha, gamma2, L, i);
//					FileHelper.saveImage(noiseImage, noiseImagePath);
//					int[] matches = SiftMatcher.match(shapesFile, new File(noiseImagePath));
//					badMatches.add((double) matches[0]);
//					goodMatches.add((double) matches[1]);
//				}
//				Statistics statsBad = new Statistics(badMatches.toArray(new Double[badMatches.size()]));
//				Statistics statsGood = new Statistics(goodMatches.toArray(new Double[goodMatches.size()]));
//				double badMatchesAvg = statsBad.getMean();
//				double goodMatchesAvg = statsGood.getMean();
//				double badMatchesStdev = statsBad.getStdDev();
//				double goodMatchesStdev = statsGood.getStdDev();
//
//				writer.write(String.format("%g;%g;%g;%g\n", badMatchesAvg, goodMatchesAvg, badMatchesStdev,
//						goodMatchesStdev));
//			}
//		}

		for (double gamma : gammas) {
			for (int L : Ls) {
				writer.write(String.format("alpha=%g;gamma=%g;L=%d;\n", alpha2, gamma, L));
				List<Double> goodMatches = new ArrayList<>();
				List<Double> badMatches = new ArrayList<>();
				for (int i = 0; i < EXPERIMENTS; i++) {
					imageManager.setImageFile(shapesFile);
					imageManager.createSyntheticImageFromOriginal(L, alpha1, gamma1, alpha2, gamma);
					BufferedImage noiseImage = imageManager.getModifiableBufferedImage();
					String noiseImagePath = String.format("shapes/shapes_%g_%g_%d_%d.png", alpha2, gamma, L, i);
					FileHelper.saveImage(noiseImage, noiseImagePath);
					int[] matches = SiftMatcher.match(shapesFile, new File(noiseImagePath));
					badMatches.add((double) matches[0]);
					goodMatches.add((double) matches[1]);
				}
				Statistics statsBad = new Statistics(badMatches.toArray(new Double[badMatches.size()]));
				Statistics statsGood = new Statistics(goodMatches.toArray(new Double[goodMatches.size()]));
				double badMatchesAvg = statsBad.getMean();
				double goodMatchesAvg = statsGood.getMean();
				double badMatchesStdev = statsBad.getStdDev();
				double goodMatchesStdev = statsGood.getStdDev();

				writer.write(String.format("%g;%g;%g;%g\n", badMatchesAvg, goodMatchesAvg, badMatchesStdev,
						goodMatchesStdev));
			}
		}
		writer.close();
	}

	public void runResolution() throws IOException {

		String munichPath = "E:\\BACKUP Data\\Tom\\projects\\ATI-GUI-Desktop\\munich.png";
		File munichFile = new File(munichPath);
		String munichSlicePath = "E:\\BACKUP Data\\Tom\\projects\\ATI-GUI-Desktop\\munich_slice.png";
		File munichSliceFile = new File(munichSlicePath);
		File csvFile = new File("experiments_res.csv");
		csvFile.createNewFile();
		PrintWriter writer = new PrintWriter(csvFile);

		for (int resolution = 2; resolution <= 16;  resolution++) {
			writer.write(String.format("res=%d;\n", resolution));
			imageManager.setImageFile(munichSliceFile);
			imageManager.applyTransformation(new DecreaseResolutionTransformation(resolution));
			BufferedImage newImage = imageManager.getModifiableBufferedImage();
			String newImagePath = String.format("resolutions/res_%d.png", resolution);
			FileHelper.saveImage(newImage, newImagePath);
			int[] matches = SiftMatcher.match(munichFile, new File(newImagePath));

			writer.write(String.format("%d;%d;\n", matches[0], matches[1]));
		}
		writer.close();
	}
}
