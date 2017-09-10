package com.goodengineer.atibackend.plates;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.goodengineer.atibackend.model.Band;

class ComponentFinder {

	private static final boolean USE_8_NEIGH = false;
	
	static List<Component> findComponents(Band band, int color) {
		List<Component> components = new ArrayList<>();
		boolean[][] pixelsVisited = new boolean[band.getWidth()][band.getHeight()]; 
		for (int x = 0; x < band.getWidth(); x++) {
			for (int y = 0; y < band.getHeight(); y++) {
				if (!pixelsVisited[x][y] && band.getPixel(x, y) == color) {
					Component component = new Component(band);
					Queue<int[]> pixelsQueue = new LinkedList<>();
					pixelsQueue.offer(new int[]{x, y});
					while (!pixelsQueue.isEmpty()) {
						int[] pixel = pixelsQueue.poll();
						int px = pixel[0];
						int py = pixel[1];
						if (!pixelsVisited[px][py]) {
							pixelsVisited[px][py] = true;
							component.addPixel(px, py);
							for (int[] neigh : band.neighbours(px, py, USE_8_NEIGH)) {
								if (!pixelsVisited[neigh[0]][neigh[1]]) {
									if (band.getPixel(neigh[0], neigh[1]) == color) {
										pixelsQueue.add(neigh);
									} else {
										pixelsVisited[neigh[0]][neigh[1]] = true;
									}
								}
							}
						}
					}
					components.add(component);
				} else {
					pixelsVisited[x][y] = true;
				}
			}
		}
		return components;
	}
}
