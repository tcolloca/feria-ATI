package util.pf;

import com.goodengineer.atibackend.model.ColorImage;
import com.google.common.io.Files;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SarImageLoader {

  public static ColorImage readSarImage(File file) throws IOException {
    byte[] bytes;
    bytes = Files.toByteArray(file);
    String hdrFile;
    String absPath = file.getAbsolutePath();
    if (fileExists(absPath.concat(".hdr"))) {
      hdrFile = absPath.concat(".hdr");
    } else {
      hdrFile = absPath.substring(0, absPath.length() - 4).concat(".hdr");
    }
    SarParams params = readParams(hdrFile);
    int width = params.get("samples", Integer.class);
    int height = params.get("lines", Integer.class);
    int dataType = params.get("data type", Integer.class);
    int byteOrder = params.get("byte order", Integer.class);
    double[][] m = new double[width][height];
    for (int l = 0; l < height; l++) {
      for (int s = 0; s < width; s++) {
        double num;
        if (dataType == 1) { // 8-bit unsigned int
          num = bytes[l * width + s];
          if (byteOrder == 1) {
            num = ~((int) num) & 0xff;
          }
        } else if (dataType == 4) { // Float
          byte[] floatBytes = new byte[4];
          for (int b = 0; b < 4; b++) {
            floatBytes[b] = bytes[l * width * 4 + s * 4 + b];
          }
          ByteOrder order = byteOrder == 0 ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
          num = ByteBuffer.wrap(floatBytes).order(order).getFloat();
        } else {
          throw new NotImplementedException();
        }
        m[s][height - 1 - l] = num;
      }
    }
    return new SarImage(new SarBand(m, "Gray"));
  }

  private static SarParams readParams(String path) throws FileNotFoundException {
    Scanner s = new Scanner(new FileInputStream(path));
    SarParams params = new SarParams();
    while (s.hasNextLine()) {
      String line = s.nextLine();
      String[] param = line.split("=", 2);
      if (param.length == 2) {
        params.put(param[0].trim(), param[1].trim());
      }
    }
    return params;
  }

  private static class SarParams {
    private final Map<String, String> params;

    public SarParams() {
      this.params = new HashMap<>();
    }

    private void put(String key, String value) {
      params.put(key, value);
    }

    private <T> T get(String key, Class<T> clazz) {
      if (String.class.equals(clazz)) {
        return clazz.cast(params.get(key));
      } else if (Integer.class.equals(clazz)) {
        return clazz.cast(Integer.parseInt(params.get(key)));
      }
      throw new NotImplementedException();
    }
  }

  private static boolean fileExists(String path) {
    File f = new File(path);
    return f.exists();
  }
}
