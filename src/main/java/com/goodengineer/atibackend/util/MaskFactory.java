package com.goodengineer.atibackend.util;

public class MaskFactory {

	public enum Direction {

		E(0), SE(1), S(2), SW(3);

		private final int rotationTimes;

		private Direction(int rotationTimes) {
			this.rotationTimes = rotationTimes;
		}

		public int getRotationTimes() {
			return rotationTimes;
		}
	}

	public static double[][] average(int size) {
		return average(size, size);
	}

	public static double[][] average(int width, int height) {
		double[][] mask = new double[width][height];
		double value = 1.0 / (width * height);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				mask[i][j] = value;
			}
		}
		return mask;
	}

	public static double[][] gauss(int size, double sigma) {
		double[][] mask = new double[size][size];
		double gaussCount = 0;
		double corner = 1;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int xDist = (size - 1) / 2 - j;
				int yDist = (size - 1) / 2 - i;
				double exp = Math.exp((Math.pow(xDist, 2) + Math.pow(yDist, 2)) / (- 2 * Math.pow(sigma, 2)));
				double gauss = 1 / (2 * Math.PI * Math.pow(sigma, 2)) * exp / corner;
				if (i == 0 && j == 0) {
					corner = gauss * 0.5;
					gauss = gauss / corner;
				}
				gauss = Math.round(gauss);
				gaussCount += gauss;
				mask[i][j] = gauss;
			}
		}

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				mask[i][j] = mask[i][j] / gaussCount;
			}
		}

		return mask;
	}

	public static double[][] LoG(int size, double sigma) {
		double[][] mask = new double[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int x = (size - 1) / 2 - i;
				int y = (size - 1) / 2 - j;
				double d = (x * x + y * y) / (sigma * sigma);
				double exp = Math.exp(-d / 2.0);
				double c = -1.0 / (Math.sqrt(2 * Math.PI) * Math.pow(sigma, 3));
				double logValue = c * (2 - d) * exp;
				mask[i][j] = logValue;
			}
		}

		return mask;
	}

	public static double[][] hiPass(int size) {
		double[][] mask = new double[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (i == (size - 1) / 2 && j == (size - 1) / 2) {
					mask[i][j] = (size * size - 1);
				} else {
					mask[i][j] = -1;
				}
			}
		}
		return mask;
	}

	public static double[][] prewitt(Direction direction) {
		double[][] matrix = new double[][] { { -1, 0, 1 }, { -1, 0, 1 }, { -1, 0, 1 } };
		rotate(matrix, direction.getRotationTimes());
		return matrix;
	}

	public static double[][] sobel(Direction direction) {
		double[][] matrix = new double[][] { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
		rotate(matrix, direction.getRotationTimes());
		return matrix;
	}

	public static double[][] kirsh(Direction direction) {
		double[][] matrix = new double[][] { { -3, -3, 5 }, { -3, 0, 5 }, { -3, -3, 5 } };
		rotate(matrix, direction.getRotationTimes());
		return matrix;
	}

	public static double[][] itemA(Direction direction) {
		double[][] matrix = new double[][] { { -1, 1, 1 }, { -1, -2, 1 }, { -1, 1, 1 } };
		rotate(matrix, direction.getRotationTimes());
		return matrix;
	}

	public static double[][] laplacian() {
		return new double[][] { { 0, -1, 0 }, { -1, 4, -1 }, { 0, -1, 0 } };
	}

	public static double[][] susan() {
		return new double[][] {
				{0, 0, 1, 1, 1, 0, 0},
				{0, 1, 1, 1, 1, 1, 0},
				{1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1},
				{0, 1, 1, 1, 1, 1, 0},
				{0, 0, 1, 1, 1, 0, 0}
		};
	}

	// matrix needs to be a 3x3 matrix
	private static void rotate(double[][] matrix, int times) {
		for (int i = 0; i < times; i++) {
			rotate(matrix);
		}
	}

	// matrix needs to be a 3x3 matrix
	private static void rotate(double[][] matrix) {
		double aux = matrix[0][0];
		matrix[0][0] = matrix[1][0];
		matrix[1][0] = matrix[2][0];
		matrix[2][0] = matrix[2][1];
		matrix[2][1] = matrix[2][2];
		matrix[2][2] = matrix[1][2];
		matrix[1][2] = matrix[0][2];
		matrix[0][2] = matrix[0][1];
		matrix[0][1] = aux;
	}
	
	public static void main(String[] args) {
		print(gauss(5, 2));
	}

	private static void print(double[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
	}
}
