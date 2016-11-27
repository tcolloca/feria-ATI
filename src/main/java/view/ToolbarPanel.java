package view;

import static view.ViewConstants.TOOLBAR_SPACING;



import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;



import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;



import javax.imageio.ImageIO;



import model.ImageManager;
import util.BufferedImageColorImageTranslator;
import util.FileHelper;
import util.SiftMatcher;
import util.ToolbarImages;



import com.goodengineer.atibackend.model.ColorImage;
import com.goodengineer.atibackend.plates.PlateRecognitionTransformation;
import com.goodengineer.atibackend.transformation.CircleHoughTransformation;
import com.goodengineer.atibackend.transformation.ConstrastTransformation;
import com.goodengineer.atibackend.transformation.DynamicRangeCompressionTransformation;
import com.goodengineer.atibackend.transformation.EqualizationTransformation;
import com.goodengineer.atibackend.transformation.LineHoughTransformation;
import com.goodengineer.atibackend.transformation.MultiplyImageTransformation;
import com.goodengineer.atibackend.transformation.NegativeTransformation;
import com.goodengineer.atibackend.transformation.PowerTransformation;
import com.goodengineer.atibackend.transformation.ScaleTransformation;
import com.goodengineer.atibackend.transformation.SubstractImageTransformation;
import com.goodengineer.atibackend.transformation.SumImageTransformation;
import com.goodengineer.atibackend.transformation.Transformation;
import com.goodengineer.atibackend.transformation.canny.CannyTransformation;
import com.goodengineer.atibackend.transformation.filter.FilterAndZeroCrossTransformation;
import com.goodengineer.atibackend.transformation.filter.FilterTransformation;
import com.goodengineer.atibackend.transformation.filter.MedianFilterTransformation;
import com.goodengineer.atibackend.transformation.filter.MultiFilterTransformation;
import com.goodengineer.atibackend.transformation.filter.difusion.BorderDetector;
import com.goodengineer.atibackend.transformation.filter.difusion.DifusionTransformation;
import com.goodengineer.atibackend.transformation.filter.difusion.IsotropicBorderDetector;
import com.goodengineer.atibackend.transformation.filter.difusion.LeclercBorderDetector;
import com.goodengineer.atibackend.transformation.filter.difusion.LorentzianBorderDetector;
import com.goodengineer.atibackend.transformation.filter.pixelRules.MaxPixelRule;
import com.goodengineer.atibackend.transformation.filter.pixelRules.NormPixelRule;
import com.goodengineer.atibackend.transformation.flip.HorizontalFlipTransformation;
import com.goodengineer.atibackend.transformation.flip.VerticalFlipTransformation;
import com.goodengineer.atibackend.transformation.key_points.HarrisTransformation;
import com.goodengineer.atibackend.transformation.key_points.SusanTransformation;
import com.goodengineer.atibackend.transformation.noise.ExponentialNoiseTransformation;
import com.goodengineer.atibackend.transformation.noise.GaussNoiseTransformation;
import com.goodengineer.atibackend.transformation.noise.RayleighNoiseTransformation;
import com.goodengineer.atibackend.transformation.noise.SaltAndPepperNoiseTransformation;
import com.goodengineer.atibackend.transformation.threshold.GlobalThresholdingTransformation;
import com.goodengineer.atibackend.transformation.threshold.OtsuThresholdingTransformation;
import com.goodengineer.atibackend.transformation.threshold.ThresholdingTransformation;
import com.goodengineer.atibackend.util.MaskFactory;
import com.goodengineer.atibackend.util.MaskFactory.Direction;
import com.goodengineer.atibackend.video.ObjectTracker;

public class ToolbarPanel {

  private final HBox hBox = new HBox();
  private final HBox hFiltersBox = new HBox();
  private final VBox vBox = new VBox();
  private final ImageManager imageManager;
  private final InfoPanel infoPanel;
  
  private List<String> allPaths;
  private ObjectTracker tracker;
  private boolean play;
  private int currentImage;

  ToolbarPanel(ImageManager imageManager, InfoPanel infoPanel) {
    this.imageManager = imageManager;
    this.infoPanel = infoPanel;

    hBox.setSpacing(TOOLBAR_SPACING);
    hBox.setAlignment(Pos.CENTER);
    hBox.setStyle("-fx-background-color: #DDD;");
    hBox.setVisible(true);

    hBox.getChildren().addAll(
        negativeButton(),
        thresholdButton(),
        thresholdGlobalButton(),
        thresholdOtsuButton(),
        contrastButton(),
        dynamicRangeButton(),
        equalizeButton(),
        addButton(),
        subtractButton(),
        multiplyButton(),
        scalarButton(),
        powerButton(),
        horizontalFlipButton(),
        verticalFlipButton(),
        gaussNoiseButton(),
        expNoiseButton(),
        rayleighNoiseButton(),
        saltAndPepperNoiseButton());

    hFiltersBox.setSpacing(TOOLBAR_SPACING);
    hFiltersBox.setAlignment(Pos.CENTER);
    hFiltersBox.setStyle("-fx-background-color: #DDD;");
    hFiltersBox.setVisible(true);

    hFiltersBox.getChildren().addAll(
        averageFilterButton(),
        medianFilterButton(),
        gaussianFilterButton(),
        hipassFilterButton(),
        sobelFilterButton(),
        prewittFilterButton(),
        kirshFilterButton(),
        itemAFilterButton(),
        laplaceFilterButton(),
        LoGFilterButton(),
        isotropicFilterButton(),
        anisotropicFilterButton(),
        harrisKeypointsButton(),
        susanKeypointsButton(),
        siftKeypointsButton(),
        cannyButton(),
        lineHoughButton(),
        circleHoughButton(),
        trackLoadPathsButton(),
        trackObjectButton(),
        trackRightArrowButton(),
        playButton(),
        stopButton(),
        recognizePlatesButton());

    vBox.getChildren().add(hBox);
    vBox.getChildren().add(hFiltersBox);
  }

  private Node negativeButton() {
    return new ToolbarButton("Negative", ToolbarImages.NEGATIVE,
        actionEvent -> imageManager.applyTransformation(new NegativeTransformation())).getNode();
  }

  private Node thresholdButton() {
    return new ToolbarButton("Threshold", ToolbarImages.THRESHOLD,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Threshold", Arrays.asList(
              new Field("Threshold value:", "127")));
          dialog.show();
          int threshold = dialog.getResult(0, Integer.class);
          imageManager.applyTransformation(new ThresholdingTransformation(threshold));
        }).getNode();
  }

  private Node thresholdGlobalButton() {
    return new ToolbarButton("Global Threshold", ToolbarImages.THRESHOLD_GLOBAL,
            actionEvent -> {
              CustomInputTextDialog dialog = new CustomInputTextDialog("Global Threshold", Arrays.asList(
                  new Field("Delta threshold:", "5")));
              dialog.show();
              int dt = dialog.getResult(0, Integer.class);
              imageManager.applyTransformation(new GlobalThresholdingTransformation(dt));
            })
        .getNode();
  }

  private Node thresholdOtsuButton() {
    return new ToolbarButton("Otsu Threshold", ToolbarImages.THRESHOLD_OTSU,
        actionEvent -> imageManager.applyTransformation(new OtsuThresholdingTransformation()))
        .getNode();
  }

  private Node contrastButton() {
    return new ToolbarButton("Contrast", ToolbarImages.CONTRAST,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Contrast", Arrays.asList(
              new Field("r1 value:", "53"),
              new Field("r2 value:", "210")));
          dialog.show();
          int r1 = dialog.getResult(0, Integer.class);
          int r2 = dialog.getResult(1, Integer.class);
          imageManager.applyTransformation(new ConstrastTransformation(r1, r2));
        }).getNode();
  }

  private Node dynamicRangeButton() {
    return new ToolbarButton("Dynamic Range", ToolbarImages.DYNAMIC_RANGE,
        actionEvent ->
          imageManager.applyTransformation(new DynamicRangeCompressionTransformation()))
        .getNode();
  }

  private Node equalizeButton() {
    return new ToolbarButton("Equalize", ToolbarImages.EQUALIZE,
        actionEvent -> imageManager.applyTransformation(new EqualizationTransformation()))
        .getNode();
  }

  private Node addButton() {
    return new ToolbarButton("Add Images", ToolbarImages.ADD,
        actionEvent -> {
          File imageFile = FileHelper.loadImageFile();
          try {
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            ColorImage colorImage = new BufferedImageColorImageTranslator()
                .translateForward(bufferedImage);
            imageManager.applyTransformation(
                new SumImageTransformation(colorImage.getBands().get(0)),
                ImageManager.BandType.RED);
            imageManager.applyTransformation(
                new SumImageTransformation(colorImage.getBands().get(1)),
                ImageManager.BandType.GREEN);
            imageManager.applyTransformation(
                new SumImageTransformation(colorImage.getBands().get(2)),
                ImageManager.BandType.BLUE);
          } catch (IOException e) {
            // TODO
            throw new RuntimeException(e);
          }
        }).getNode();
  }

  private Node subtractButton() {
    return new ToolbarButton("Subtract Images", ToolbarImages.SUBSTRACT,
        actionEvent -> {
          File imageFile = FileHelper.loadImageFile();
          try {
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            ColorImage colorImage = new BufferedImageColorImageTranslator()
                .translateForward(bufferedImage);
            imageManager.applyTransformation(
                new SubstractImageTransformation(colorImage.getBands().get(0)),
                ImageManager.BandType.RED);
            imageManager.applyTransformation(
                new SubstractImageTransformation(colorImage.getBands().get(1)),
                ImageManager.BandType.GREEN);
            imageManager.applyTransformation(
                new SubstractImageTransformation(colorImage.getBands().get(2)),
                ImageManager.BandType.BLUE);
          } catch (IOException e) {
            // TODO
            throw new RuntimeException(e);
          }
        }).getNode();
  }

  private Node multiplyButton() {
    return new ToolbarButton("Multiply Images", ToolbarImages.MULTIPLY,
        actionEvent -> {
          File imageFile = FileHelper.loadImageFile();
          try {
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            ColorImage colorImage = new BufferedImageColorImageTranslator()
                .translateForward(bufferedImage);
            imageManager.applyTransformation(
                new MultiplyImageTransformation(colorImage.getBands().get(0)),
                ImageManager.BandType.RED);
            imageManager.applyTransformation(
                new MultiplyImageTransformation(colorImage.getBands().get(1)),
                ImageManager.BandType.GREEN);
            imageManager.applyTransformation(
                new MultiplyImageTransformation(colorImage.getBands().get(2)),
                ImageManager.BandType.BLUE);
          } catch (IOException e) {
            // TODO
            throw new RuntimeException(e);
          }
        }).getNode();
  }

  private Node scalarButton() {
    return new ToolbarButton("Scalar", ToolbarImages.SCALAR,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Scalar", Arrays.asList(
              new Field("Scalar:", "3")));
          dialog.show();
          int scalar = dialog.getResult(0, Integer.class);
          imageManager.applyTransformation(new ScaleTransformation(scalar));
        }).getNode();
  }

  private Node powerButton() {
    return new ToolbarButton("Power", ToolbarImages.POWER,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Gamma", Arrays.asList(
              new Field("Gamma:", "0.4")));
          dialog.show();
          double gamma = dialog.getResult(0, Double.class);
          imageManager.applyTransformation(new PowerTransformation(gamma));
        }).getNode();
  }

  private Node horizontalFlipButton() {
    return new ToolbarButton("Horizontal Flip", ToolbarImages.FLIP_H,
        actionEvent -> {
          imageManager.applyTransformation(new HorizontalFlipTransformation());
        }).getNode();
  }

  private Node verticalFlipButton() {
    return new ToolbarButton("Vertical Flip", ToolbarImages.FLIP_V,
        actionEvent -> {
          imageManager.applyTransformation(new VerticalFlipTransformation());
        }).getNode();
  }
  
//  private Node replaceColorButton() {
////	  infoPanel.applyTransformation(new RectBorderTransformation(selectionX, selectionY,
////	          selectionWidth + selectionX - 1, selectionHeight + selectionY - 1,
////	          ColorHelper.convertToRgb(((Color) newColorBox.getFill())
//	  // TODO
//  }

  private Node gaussNoiseButton() {
    return new ToolbarButton("Gaussian Noise", ToolbarImages.NOISE_GAUSS,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Gaussian Noise", Arrays.asList(
              new Field("Percentage:", "17.5"),
              new Field("Sigma:", "1.5"),
              new Field("Mu:", "0.0")));
          dialog.show();
          double percentage = dialog.getResult(0, Double.class);
          double sigma = dialog.getResult(1, Double.class);
          double mu = dialog.getResult(2, Double.class);
          imageManager.applyTransformation(new GaussNoiseTransformation(percentage, mu, sigma),
              ImageManager.BandType.GRAY);
        }).getNode();
  }

  private Node expNoiseButton() {
    return new ToolbarButton("Exponential Noise", ToolbarImages.NOISE_EXP,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Exponential Noise",
              Arrays.asList(
                new Field("Percentage:", "17.5"),
                new Field("Lambda:", "1.5")));
          dialog.show();
          double percentage = dialog.getResult(0, Double.class);
          double lambda = dialog.getResult(1, Double.class);
          imageManager.applyTransformation(new ExponentialNoiseTransformation(percentage, lambda),
              ImageManager.BandType.GRAY);
        }).getNode();
  }

  private Node rayleighNoiseButton() {
    return new ToolbarButton("Rayleigh Noise", ToolbarImages.NOISE_RAYLEIGH,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Rayleigh Noise",
              Arrays.asList(
                  new Field("Percentage:", "17.5"),
                  new Field("epsilon:", "1.5")));
          dialog.show();
          double percentage = dialog.getResult(0, Double.class);
          double epsilon = dialog.getResult(1, Double.class);
          imageManager.applyTransformation(new RayleighNoiseTransformation(percentage, epsilon),
              ImageManager.BandType.GRAY);
        }).getNode();
  }

  private Node saltAndPepperNoiseButton() {
    return new ToolbarButton("Salt & Pepper Noise", ToolbarImages.NOISE_SALT_AND_PEPPER,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Salt & Pepper Noise",
              Arrays.asList(
                  new Field("Percentage:", "17.5"),
                  new Field("salt:", "0.4"),
                  new Field("pepper:", "0.6")));
          dialog.show();
          double percentage = dialog.getResult(0, Double.class);
          double salt = dialog.getResult(1, Double.class);
          double pepper = dialog.getResult(2, Double.class);
          imageManager.applyTransformation(
              new SaltAndPepperNoiseTransformation(percentage, salt, pepper),
              ImageManager.BandType.GRAY);
        }).getNode();
  }

  private Node averageFilterButton() {
    return new ToolbarButton("Average Filter", ToolbarImages.FILTER_AVERAGE,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Average Filter", Arrays.asList(
                  new Field("Size:", "3")));
          dialog.show();
          int size = dialog.getResult(0, Integer.class);
          imageManager.applyTransformation(new FilterTransformation(MaskFactory.average(size)));
        }).getNode();
  }

  private Node medianFilterButton() {
    return new ToolbarButton("Median Filter", ToolbarImages.FILTER_MEDIAN,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Median Filter", Arrays.asList(
              new Field("Size:", "3")));
          dialog.show();
          int size = dialog.getResult(0, Integer.class);
          imageManager.applyTransformation(new MedianFilterTransformation(size));
        }).getNode();
  }

  private Node gaussianFilterButton() {
    return new ToolbarButton("Gaussian Filter", ToolbarImages.FILTER_GAUSS,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Gaussian Filter", Arrays.asList(
              new Field("Size:", "5"),
              new Field("Sigma:", "1.5")));
          dialog.show();
          int size = dialog.getResult(0, Integer.class);
          double sigma = dialog.getResult(1, Double.class);
          imageManager.applyTransformation(
              new FilterTransformation(MaskFactory.gauss(size, sigma)));
        }).getNode();
  }

  private Node hipassFilterButton() {
    return new ToolbarButton("HiPass Filter", ToolbarImages.FILTER_HIPASS,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("HiPass Filter", Arrays.asList(
              new Field("Size:", "3")));
          dialog.show();
          int size = dialog.getResult(0, Integer.class);
          imageManager.applyTransformation(
              new FilterTransformation(MaskFactory.hiPass(size)));
        }).getNode();
  }

  private Node sobelFilterButton() {
    return new ToolbarButton("Sobel Filter", ToolbarImages.FILTER_SOBEL,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Sobel Filter", Arrays.asList(
              new Field("Directional:", "false")));
          dialog.show();
          boolean isDirectional = Boolean.parseBoolean(dialog.getResult(0, String.class));
          MultiFilterTransformation.PixelRule rule = isDirectional ? new MaxPixelRule() : new NormPixelRule();
          List<double[][]> masks = isDirectional ?
              Arrays.asList(
                MaskFactory.sobel(Direction.E),
                MaskFactory.sobel(Direction.SE),
                MaskFactory.sobel(Direction.S),
                  MaskFactory.sobel(Direction.SW))
              : Arrays.asList(MaskFactory.sobel(Direction.E), MaskFactory.sobel(Direction.S));
          imageManager.applyTransformation(
              new MultiFilterTransformation(rule, masks));
        }).getNode();
  }

  private Node prewittFilterButton() {
    return new ToolbarButton("Prewitt Filter", ToolbarImages.FILTER_PREWITT,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Prewitt Filter", Arrays.asList(
              new Field("Directional:", "false")));
          dialog.show();
          boolean isDirectional = Boolean.parseBoolean(dialog.getResult(0, String.class));
          MultiFilterTransformation.PixelRule rule = isDirectional ? new MaxPixelRule() : new NormPixelRule();
          List<double[][]> masks = isDirectional ?
              Arrays.asList(
                  MaskFactory.prewitt(Direction.E),
                  MaskFactory.prewitt(Direction.SE),
                  MaskFactory.prewitt(Direction.S),
                  MaskFactory.prewitt(Direction.SW))
              : Arrays.asList(MaskFactory.prewitt(Direction.E), MaskFactory.prewitt(Direction.S));
          imageManager.applyTransformation(
              new MultiFilterTransformation(rule, masks));
        }).getNode();
  }

  private Node kirshFilterButton() {
    return new ToolbarButton("Kirsh Filter", ToolbarImages.FILTER_KIRSH,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Kirsh Filter", Arrays.asList(
              new Field("Directional:", "false")));
          dialog.show();
          boolean isDirectional = Boolean.parseBoolean(dialog.getResult(0, String.class));
          MultiFilterTransformation.PixelRule rule = isDirectional ? new MaxPixelRule() : new NormPixelRule();
          List<double[][]> masks = isDirectional ?
              Arrays.asList(
                  MaskFactory.kirsh(Direction.E),
                  MaskFactory.kirsh(Direction.SE),
                  MaskFactory.kirsh(Direction.S),
                  MaskFactory.kirsh(Direction.SW))
              : Arrays.asList(MaskFactory.kirsh(Direction.E), MaskFactory.kirsh(Direction.S));
          imageManager.applyTransformation(
              new MultiFilterTransformation(rule, masks));
        }).getNode();
  }

  private Node itemAFilterButton() {
    return new ToolbarButton("Item a) Filter", ToolbarImages.FILTER_A,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Item a) Filter", Arrays.asList(
              new Field("Directional:", "false")));
          dialog.show();
          boolean isDirectional = Boolean.parseBoolean(dialog.getResult(0, String.class));
          MultiFilterTransformation.PixelRule rule = isDirectional ? new MaxPixelRule() : new NormPixelRule();
          List<double[][]> masks = isDirectional ?
              Arrays.asList(
                  MaskFactory.itemA(Direction.E),
                  MaskFactory.itemA(Direction.SE),
                  MaskFactory.itemA(Direction.S),
                  MaskFactory.itemA(Direction.SW))
              : Arrays.asList(MaskFactory.itemA(Direction.E), MaskFactory.itemA(Direction.S));
          imageManager.applyTransformation(
              new MultiFilterTransformation(rule, masks));
        }).getNode();
  }

  private Node laplaceFilterButton() {
    return new ToolbarButton("Laplace Filter", ToolbarImages.FILTER_LAPLACE,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Laplace Filter", Arrays.asList(
              new Field("Threshold:", "0")));
          dialog.show();
          double threshold = dialog.getResult(0, Double.class);
          imageManager.applyTransformation(
              new FilterAndZeroCrossTransformation(threshold, MaskFactory.laplacian()));
        }).getNode();
  }

  private Node LoGFilterButton() {
    return new ToolbarButton("LoG Filter", ToolbarImages.FILTER_LOG,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("LoG Filter", Arrays.asList(
              new Field("Threshold:", "0"),
              new Field("Size:", "3"),
              new Field("Sigma:", "1.5")));
          dialog.show();
          double threshold = dialog.getResult(0, Double.class);
          int size = dialog.getResult(1, Integer.class);
          double sigma = dialog.getResult(2, Double.class);
          imageManager.applyTransformation(
              new FilterAndZeroCrossTransformation(threshold, MaskFactory.LoG(size, sigma)));
        }).getNode();
  }

  private Node isotropicFilterButton() {
    return new ToolbarButton("Isotropic Diffusion", ToolbarImages.FILTER_ISOTROPIC,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Isotropic Diffusion", Arrays.asList(
              new Field("t:", "1")));
          dialog.show();
          int t = dialog.getResult(0, Integer.class);
          imageManager.applyTransformation(new DifusionTransformation(t, new IsotropicBorderDetector()));
        }).getNode();
  }

  private Node anisotropicFilterButton() {
    return new ToolbarButton("Anistropic Diffusion", ToolbarImages.FILTER_ANISOTROPIC,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Anisotropic Diffusion", Arrays.asList(
              new Field("t:", "5"),
              new Field("sigma:", "3.5"),
              new Field("Lorentzian or Leclerc:", "lo/le")));
          dialog.show();
          int t = dialog.getResult(0, Integer.class);
          double sigma = dialog.getResult(1, Double.class);
          BorderDetector borderDetector = dialog.getResult(2, String.class) == "lo" ?
              new LorentzianBorderDetector(sigma) : new LeclercBorderDetector(sigma);
          imageManager.applyTransformation(new DifusionTransformation(t, borderDetector));
        }).getNode();
  }

  private Node harrisKeypointsButton() {
    return new ToolbarButton("Harris Keypoints", ToolbarImages.KEYPOINT_HARRIS,
        actionEvent -> {
          CustomInputTextDialog dialog = new CustomInputTextDialog("Harris Keypoints", Arrays.asList(
              new Field("Threshold (Percentage of Maximum):", "50.0")));
          dialog.show();
          double percentage = dialog.getResult(0, Double.class);
          imageManager.applyTransformation(new HarrisTransformation(percentage));
        }).getNode();
  }

    private Node susanKeypointsButton() {
        return new ToolbarButton("Susan Keypoints", ToolbarImages.KEYPOINT_SUSAN,
                actionEvent -> {
                    CustomInputTextDialog dialog = new CustomInputTextDialog("Susan Keypoints", Arrays.asList(
                            new Field("Threshold:", "5"),
                            new Field("Corner limit:", "0.75")));
                    dialog.show();
                    int threshold = dialog.getResult(0, Integer.class);
                    double cornerLimit = dialog.getResult(1, Double.class);
                    imageManager.applyTransformation(new SusanTransformation(threshold, cornerLimit));
                }).getNode();
    }
   
    private Node siftKeypointsButton() {
        return new ToolbarButton("Sift Keypoints", ToolbarImages.KEYPOINT_SIFT,
                actionEvent -> {
                	File objectImageFile = FileHelper.loadImageFile();
                	File sceneImageFile = FileHelper.loadImageFile();
                	System.out.println(sceneImageFile.getAbsolutePath());
                	try {
						SiftMatcher.match(objectImageFile, sceneImageFile);
                        File matchoutputFile = new File("matchoutput.jpg");
                        imageManager.setImageFile(matchoutputFile);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }).getNode();
    }
    
    private Node cannyButton() {
        return new ToolbarButton("Canny", ToolbarImages.CANNY,
                actionEvent -> {
                    CustomInputTextDialog dialog = new CustomInputTextDialog("Canny transformation", Arrays.asList(
                    		new Field("size:", "7"),
                    		new Field("sigma:", "2"),
                            new Field("l1:", "85"),
                            new Field("l2:", "170")));
                        dialog.show();
                        int size = dialog.getResult(0, Integer.class);
                        double sigma = dialog.getResult(1, Double.class);
                        int l1 = dialog.getResult(2, Integer.class);
                        int l2 = dialog.getResult(3, Integer.class);
                	imageManager.applyTransformation(new CannyTransformation(size, sigma, l1, l2));
                }).getNode();
    }
    
    private Node lineHoughButton() {
        return new ToolbarButton("Line Hough Transform", ToolbarImages.HOUGH_LINE,
                actionEvent -> {
                	CustomInputTextDialog dialog = new CustomInputTextDialog("Line Hough Transform", Arrays.asList(
                            new Field("Angle Count:", "50"),
                            new Field("Distance Count:", "50"), 
                            new Field("Epsilon:", "1.0"),
                            new Field("Threshold:", "60")));
                	dialog.show();
                	int angleCount = dialog.getResult(0, Integer.class);
                	int distCount = dialog.getResult(1, Integer.class);
                	double eps = dialog.getResult(2, Double.class);
                	int threshold = dialog.getResult(3, Integer.class);
                	imageManager.applyTransformation(new LineHoughTransformation(angleCount, distCount,
                			eps, threshold));
                }).getNode();
    }
    
    private Node circleHoughButton() {
        return new ToolbarButton("Circle Hough Transform", ToolbarImages.HOUGH_CIRCLE,
                actionEvent -> {
                	CustomInputTextDialog dialog = new CustomInputTextDialog("Circle Hough Transform", Arrays.asList(
                            new Field("Width Count:", "50"),
                            new Field("Height Count:", "50"), 
                            new Field("Radius Count:", "50"),
                            new Field("Radius Start:", "5"), 
                            new Field("Radius Start:", "100"),
                            new Field("Epsilon:", "30.0"),
                            new Field("Threshold:", "60")));
                	dialog.show();
                	int widthCount = dialog.getResult(0, Integer.class);
                	int heightCount = dialog.getResult(1, Integer.class);
                	int radiusCount = dialog.getResult(2, Integer.class);
                	int radiusStart = dialog.getResult(3, Integer.class);
                	int radiusEnd = dialog.getResult(4, Integer.class);
                	double eps = dialog.getResult(5, Double.class);
                	int threshold = dialog.getResult(6, Integer.class);
                	imageManager.applyTransformation(new CircleHoughTransformation(widthCount, heightCount,
                			radiusCount, radiusStart, radiusEnd, eps, threshold));
                }).getNode();
    }
    
    private Node trackLoadPathsButton() {
        return new ToolbarButton("Track Object", ToolbarImages.OPEN,
                actionEvent -> {
                	tracker = null;
                	allPaths = FileHelper.allPathsInFolder();
                    File imageFile = new File(allPaths.get(0));
                    imageManager.setImageFile(imageFile);
                    imageManager.refresh();
                    currentImage = 0;
                }).getNode();
    }
    
    private Node trackObjectButton() {
        return new ToolbarButton("Track Object", ToolbarImages.TRACK,
                actionEvent -> {
                	if (tracker == null) {
                        tracker = new ObjectTracker(imageManager.getModifiableBackendImage().clone(), 
                    			infoPanel.selectionX, infoPanel.selectionY, 
                    			infoPanel.selectionWidth, infoPanel.selectionHeight, 1);
                	} else {
                        File imageFile = new File(allPaths.get(currentImage));
                        imageManager.setImageFile(imageFile);
                	}
                	try {
                		tracker.track();
                		tracker.paintBorder(imageManager.getModifiableBackendImage());
                		imageManager.refresh();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }).getNode();
    }
    
    private Node trackRightArrowButton() {
        return new ToolbarButton("Next Frame", ToolbarImages.SKIP,
                actionEvent -> {
                	currentImage++;
                	if (currentImage >= allPaths.size()) return;
                    File imageFile = new File(allPaths.get(currentImage));
                    imageManager.setImageFile(imageFile);
                    tracker.setImage(imageManager.getModifiableBackendImage().clone());
                    tracker.paintBorder(imageManager.getModifiableBackendImage());
                    imageManager.refresh();
                }).getNode();
    }
    
    private Node playButton() {
        return new ToolbarButton("Play Tracking Video", ToolbarImages.PLAY,
                actionEvent -> {
                	new Thread(() -> {
                		play = true;
	                	while (++currentImage < allPaths.size() && play) {
		                    File imageFile = new File(allPaths.get(currentImage));
		                    imageManager.setImageFile(imageFile);
		                    tracker.setImage(imageManager.getModifiableBackendImage().clone());
		                    tracker.track();
		                    tracker.paintBorder(imageManager.getModifiableBackendImage());
		                    imageManager.refresh();
	                	}
                	}).start();
                }).getNode();
    }
    
    private Node stopButton() {
        return new ToolbarButton("Stop Tracking Video", ToolbarImages.PAUSE,
                actionEvent -> {
                	play = false;
                }).getNode();
    }
    
    private Node recognizePlatesButton() {
        return new ToolbarButton("Recognize Plates", ToolbarImages.PLATES,
                actionEvent -> {
                	Transformation PRT = new PlateRecognitionTransformation();
                	allPaths = FileHelper.allPathsInFolder();
                	for (String imageFile : allPaths) {
                		imageManager.setImageFile(new File(imageFile));
                		imageManager.getValueBand();
                		try {
                			imageManager.applyTransformation(PRT);
                		} catch (Exception e) {
                			e.printStackTrace();
                		}
                	}
                }).getNode();
    }

  public Node getNode() {
    return vBox;
  }
}
