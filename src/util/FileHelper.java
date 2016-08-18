package util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static view.ViewConstants.*;

public class FileHelper {

  private static String lastFileUploadedExt;

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

  private static Optional<File> getFile() {
    JFileChooser fileChooser = getFileChooser();
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
      String ext = "bmp";
      if (fileName.matches(".*\\.(png|bmp|ppm|raw)$")) {
        ext = fileName.split("\\.")[1];
        System.out.println(ext);
      }
      System.out.println(fileName.split("\\.")[0] + "." + ext);
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
