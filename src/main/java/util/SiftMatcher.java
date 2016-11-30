package util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;

public class SiftMatcher {

	
	  public static int[] match(File objectFile, File sceneFile) throws IOException {


	      System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

	      System.out.println("Started....");
	      System.out.println("Loading images...");
	      System.out.println(objectFile.toString());
	      System.out.println(sceneFile.toString());
		  String[] dirsObj = objectFile.getAbsolutePath().split("/");
		  String objectPath = dirsObj[dirsObj.length - 1];
		  String[] dirsScene = sceneFile.getAbsolutePath().split("/");
		  String scenePath = dirsScene[dirsScene.length - 1];
	      Mat objectImage = Highgui.imread(objectPath, Highgui.CV_LOAD_IMAGE_COLOR);
	      System.out.println("ObjImage rows: " + objectImage.rows());
	      Mat sceneImage = Highgui.imread(scenePath, Highgui.CV_LOAD_IMAGE_COLOR);
	      System.out.println("Scene Image rows: " + sceneImage.rows());
	      
	      MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
	      FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SIFT);
	      System.out.println("Detecting key points...");
	      featureDetector.detect(objectImage, objectKeyPoints);
	      KeyPoint[] keypoints = objectKeyPoints.toArray();
	      System.out.println("keypoints: " + keypoints.length);

	      MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
	      DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
	      System.out.println("Computing descriptors...");
	      descriptorExtractor.compute(objectImage, objectKeyPoints, objectDescriptors);

	      // Create the matrix for output image.
	      Mat outputImage = new Mat(objectImage.rows(), objectImage.cols(), Highgui.CV_LOAD_IMAGE_COLOR);
	      Scalar newKeypointColor = new Scalar(255, 0, 0);

	      System.out.println("Drawing key points on object image...");
	      Features2d.drawKeypoints(objectImage, objectKeyPoints, outputImage, newKeypointColor, 0);

	      // Match object image with the scene image
	      MatOfKeyPoint sceneKeyPoints = new MatOfKeyPoint();
	      MatOfKeyPoint sceneDescriptors = new MatOfKeyPoint();
	      System.out.println("Detecting key points in background image...");
	      featureDetector.detect(sceneImage, sceneKeyPoints);
	      System.out.println("Computing descriptors in background image...");
	      descriptorExtractor.compute(sceneImage, sceneKeyPoints, sceneDescriptors);

	      Mat matchoutput = new Mat(sceneImage.rows() * 2, sceneImage.cols() * 2, Highgui.CV_LOAD_IMAGE_COLOR);
	      Scalar matchestColor = new Scalar(0, 255, 0);

	      List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
	      DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
	      System.out.println("Matching object and scene images...");
	      
	      if (objectDescriptors.empty() || sceneDescriptors.empty()) {
	    	  System.out.println("No keypoints");
	    	  return new int[] {0, 0};
	      }
	      
	      MatOfDMatch matchesRaw = new MatOfDMatch();
	      descriptorMatcher.match(objectDescriptors, sceneDescriptors, matchesRaw);
	      int rawMatchesAmount = matchesRaw.toArray().length;
	      descriptorMatcher.knnMatch(objectDescriptors, sceneDescriptors, matches, 2);

	      System.out.println("Calculating good match list...");
	      LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();

	      float nndrRatio = 0.7f;

	      for (int i = 0; i < matches.size(); i++) {
	          MatOfDMatch matofDMatch = matches.get(i);
	          DMatch[] dmatcharray = matofDMatch.toArray();
	          DMatch m1 = dmatcharray[0];
	          DMatch m2 = dmatcharray[1];

	          if (m1.distance <= m2.distance * nndrRatio) {
	              goodMatchesList.addLast(m1);

	          }
	      }
	      
	      int goodMatchesAmount = goodMatchesList.size();

	      if (goodMatchesList.size() >= 7) {
	          System.out.println("Object Found!!!");
	          System.out.println("Matches: " + goodMatchesList.size());
	      } else {
	          System.out.println("Object Not Found");
	      }
//
//	          List<KeyPoint> objKeypointlist = objectKeyPoints.toList();
//	          List<KeyPoint> scnKeypointlist = sceneKeyPoints.toList();
//
//	          LinkedList<Point> objectPoints = new LinkedList<>();
//	          LinkedList<Point> scenePoints = new LinkedList<>();
//
//	          for (int i = 0; i < goodMatchesList.size(); i++) {
//	              objectPoints.addLast(objKeypointlist.get(goodMatchesList.get(i).queryIdx).pt);
//	              scenePoints.addLast(scnKeypointlist.get(goodMatchesList.get(i).trainIdx).pt);
//	          }
//
//	          MatOfPoint2f objMatOfPoint2f = new MatOfPoint2f();
//	          objMatOfPoint2f.fromList(objectPoints);
//	          MatOfPoint2f scnMatOfPoint2f = new MatOfPoint2f();
//	          scnMatOfPoint2f.fromList(scenePoints);
//
//	          Mat homography = Calib3d.findHomography(objMatOfPoint2f, scnMatOfPoint2f, Calib3d.RANSAC, 3);
//
//	          Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
//	          Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);
//
//	          obj_corners.put(0, 0, new double[]{0, 0});
//	          obj_corners.put(1, 0, new double[]{objectImage.cols(), 0});
//	          obj_corners.put(2, 0, new double[]{objectImage.cols(), objectImage.rows()});
//	          obj_corners.put(3, 0, new double[]{0, objectImage.rows()});
//
//	          System.out.println("Transforming object corners to scene corners...");
//	          Core.perspectiveTransform(obj_corners, scene_corners, homography);
//
//	          Mat img = Highgui.imread(scenePath, Highgui.CV_LOAD_IMAGE_COLOR);
//
//	          Core.line(img, new Point(scene_corners.get(0, 0)), new Point(scene_corners.get(1, 0)), new Scalar(0, 255, 0), 4);
//	          Core.line(img, new Point(scene_corners.get(1, 0)), new Point(scene_corners.get(2, 0)), new Scalar(0, 255, 0), 4);
//	          Core.line(img, new Point(scene_corners.get(2, 0)), new Point(scene_corners.get(3, 0)), new Scalar(0, 255, 0), 4);
//	          Core.line(img, new Point(scene_corners.get(3, 0)), new Point(scene_corners.get(0, 0)), new Scalar(0, 255, 0), 4);
//
//	          System.out.println("Drawing matches image...");
//	          MatOfDMatch goodMatches = new MatOfDMatch();
//	          goodMatches.fromList(goodMatchesList);
//
//	          Features2d.drawMatches(objectImage, objectKeyPoints, sceneImage, sceneKeyPoints, goodMatches, matchoutput, matchestColor, newKeypointColor, new MatOfByte(), 2);
//
//	          Highgui.imwrite("matchoutput.jpg", matchoutput);
//
//	      String filename = "keypoints.jpg";
//	      Highgui.imwrite(filename, outputImage);
//
//	      System.out.println("Ended....");
	      System.out.println(Arrays.toString(new int[]{rawMatchesAmount, goodMatchesAmount}));
	      return new int[] {rawMatchesAmount, goodMatchesAmount};
	  }
}
