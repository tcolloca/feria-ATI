package util;

import static view.ViewConstants.FILE_CHOOSER_FONT;
import static view.ViewConstants.FILE_CHOOSER_HEIGHT;
import static view.ViewConstants.FILE_CHOOSER_WIDTH;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class FileHelper {

  private static final String DEFAULT_EXT = "png";

  /**
   * Returns the name of the file chosen.
   */
  public static File loadImageFile() {
    Optional<File> file = getFile();
    if (file.isPresent()) {
      // TODO: Check.
    	return file.get();
    }
    // TODO
    return null;
  }
  
  public static ArrayList<String> allPathsInFolder() {
	  
	    Optional<File> file = getFile();
	    if (file.isPresent()) {
	      // TODO: Check.
	    	String folderPath = file.get().getAbsolutePath();
	    	folderPath = folderPath.substring(0, folderPath.lastIndexOf("\\"));
	    	return allPathsInFolder(folderPath);
	    }
	    // TODO
	    return null;
  }
  
	private static ArrayList<String> allPathsInFolder(String folderPath) {
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> paths = new ArrayList<>();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
//				save file and hope for an image
				paths.add(listOfFiles[i].getAbsolutePath());
			} else if (listOfFiles[i].isDirectory()) {
//				skip directories
			}
		}
		
		return paths;
	}

  private static Optional<File> getFile() {
    JFileChooser fileChooser = getFileChooser();
    fileChooser.setCurrentDirectory(new File("C:/Users/tomas/OneDrive/projects/ATI-GUI-Desktop"));
    int status = fileChooser.showOpenDialog(null);
    if (status == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();
      if (file != null) {
        return Optional.of(file);
      }
    }
    return Optional.empty();
  }

  /**
   * Returns the name of the file chosen.
   */
  public static Optional<String> saveImage(BufferedImage image) {
    JFileChooser fileChooser = getFileChooser();
    int status = fileChooser.showSaveDialog(null);
    if (status == JFileChooser.APPROVE_OPTION) {
      String fileName = fileChooser.getSelectedFile().getAbsolutePath();
      System.out.println(fileName);
      String ext = DEFAULT_EXT;
      if (fileName.matches(".*\\.(png|bmp|ppm|raw)$")) {
        ext = fileName.split("\\.")[1];
      }
      File outputFile = new File(fileName.split("\\.")[0] + "." + ext);
      try {
        ImageIO.write(image, ext, outputFile);
      } catch (IOException e) {
        // TODO: Auto-generated code.
        e.printStackTrace();
      }
    }
    return Optional.empty();
  }

  private static JFileChooser getFileChooser() {
    JFileChooser chooser = new JFileChooser();
    chooser.setPreferredSize(new Dimension(FILE_CHOOSER_WIDTH, FILE_CHOOSER_HEIGHT));
    setFont(chooser.getComponents());
    return chooser;
  }

  private static void setFont(Component[] components) {
    for (Component component: components) {
      if (component instanceof Container) {
        setFont(((Container) component).getComponents());
      }
      component.setFont(FILE_CHOOSER_FONT);
    }
  }
}
