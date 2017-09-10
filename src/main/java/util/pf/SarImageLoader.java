package util.pf;

import com.goodengineer.atibackend.model.ColorImage;
import com.goodengineer.atibackend.util.MaskFactory;
import com.google.common.io.Files;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SarImageLoader {

  public static ColorImage readSarImage(File file, int reducerWidth, int reducerHeight,
      String type) throws IOException {
    byte[] bytes;
    System.out.println(file.getAbsolutePath());
    bytes = Files.toByteArray(file);
    System.out.println(bytes.length);
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
    
    System.out.println(String.format("h : %d, w: %d", height, width));
    
    double[][] m = new double[width][height];
    for (int l = 0; l < height; l++) {
      for (int s = 0; s < width; s++) {
        double num;
        switch (dataType) {
          case 1: // 8-bit unsigned int
            num = readNum(bytes, l, s, width, 1, 1, byteOrder, Short.class);
            break;
          case 2: // 16-bit signed integer
            num = readNum(bytes, l, s, width, 2, 1, byteOrder, Short.class);
            break;
          case 3: // 32-bit signed integer
            num = readNum(bytes, l, s, width, 4, 1, byteOrder, Integer.class);
            break;
          case 4: // 32-bit single-precision floating-point
            num = readNum(bytes, l, s, width, 4, 1, byteOrder, Float.class);
            break;
          case 5: // 64-bit double-precision floating-point
            num = readNum(bytes, l, s, width, 8, 1, byteOrder, Double.class);
            break;
          case 6: // Real-imaginary pair of 32-bit single-precision floating-point
            num = readNum(bytes, l, s, width, 4, 2, byteOrder, Float.class);
            break;
          case 9: // Real-imaginary pair of 64-bit double-precision floating-point
            num = readNum(bytes, l, s, width, 8, 2, byteOrder, Double.class);
            break;
          default:
            throw new NotImplementedException();
        }
        m[s][height - 1 - l] = num;
      }
    }
    double[][] reducedMatrix = reduce(m, reducerWidth, reducerHeight, type);
    return new SarImage(new SarBand(reducedMatrix, "Gray"));
  }

  private static double readNum(byte[] bytes, int l, int s, int width, int bytesAmount,
                                int numsAmount, int byteOrder, Class<? extends Number> type) {
    byte[][] nums = new byte[numsAmount][bytesAmount];
    for (int i = 0; i < numsAmount; i++) {
      for (int b = 0; b < bytesAmount; b++) {
        int offset = numsAmount * bytesAmount;
        int n = l * width * offset + s * offset + i * bytesAmount + b;
        if (n > 51168008 - 11) System.out.println(n + 8);
        nums[i][b] = bytes[8 + l * width * offset + s * offset + i * bytesAmount + b];
      }
    }
    ByteOrder order = byteOrder == 0 ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    double[] convertedNums = new double[numsAmount];
    if (bytesAmount == 1) {
      for (int i = 0; i < numsAmount; i++) {
//      convertedNums[i] = byteOrder == 0 ? nums[i][0] : ~nums[i][0] & 0xff;
        convertedNums[i] = ByteBuffer.wrap(new byte[]{0, nums[i][0]}).order(order).getShort();
      }
    } else if (type.equals(Integer.class)) {
      for (int i = 0; i < numsAmount; i++) {
        convertedNums[i] = ByteBuffer.wrap(nums[i]).order(order).getInt();
      }
    } else if (type.equals(Long.class)) {
      for (int i = 0; i < numsAmount; i++) {
        convertedNums[i] = ByteBuffer.wrap(nums[i]).order(order).getLong();
      }
    } else if (type.equals(Float.class)) {
      for (int i = 0; i < numsAmount; i++) {
        convertedNums[i] = ByteBuffer.wrap(nums[i]).order(order).getFloat();
      }
    } else if (type.equals(Double.class)) {
      for (int i = 0; i < numsAmount; i++) {
        convertedNums[i] = ByteBuffer.wrap(nums[i]).order(order).getDouble();
      }
    } else {
      throw new NotImplementedException();
    }
    if (numsAmount == 1) {
      return convertedNums[0];
    } else {
      return Math.sqrt(convertedNums[0] * convertedNums[0] + convertedNums[1] * convertedNums[1]);
    }
  }

  private static double[][] reduce(double[][] m, int reducerWidth, int reducerHeight,
      String type) {
    if (reducerWidth == 1 && reducerHeight == 1) {
      return m;
    }
    int width = m.length;
    int height = m[0].length;
    int reducedWidth = width / reducerWidth;
    int reducedHeight = height / reducerHeight;
    double[][] reducedM = new double[reducedWidth][reducedHeight];
    switch (type) {
      case "avg":
        double[][] avgMask = MaskFactory.average(reducerWidth, reducerHeight);
        for (int x = 0; x < reducedWidth; x++) {
          for (int y = 0; y < reducedHeight; y++) {
            double avg = 0;
            for (int k = 0; k < reducerWidth; k++) {
              for (int h = 0; h < reducerHeight; h++) {
                avg += avgMask[k][h] * m[x * reducerWidth + k][y * reducerHeight + h];
              }
            }
            reducedM[x][y] = avg;
          }
        }
        break;
      case "med":
        for (int x = 0; x < reducedWidth; x++) {
          for (int y = 0; y < reducedHeight; y++) {
            double[] arr = new double[reducerWidth * reducerHeight];
            for (int k = 0; k < reducerWidth; k++) {
              for (int h = 0; h < reducerHeight; h++) {
                arr[k * reducerHeight + h] += m[x * reducerWidth + k][y * reducerHeight + h];
              }
            }
            reducedM[x][y] = getMedian(arr);
          }
        }
        break;
      case "int":
        double val;
        for (int x = 0; x < reducedWidth; x++) {
          for (int y = 0; y < reducedHeight; y++) {
            double intensity = 0;
            for (int k = 0; k < reducerWidth; k++) {
              for (int h = 0; h < reducerHeight; h++) {
                val = m[x * reducerWidth + k][y * reducerHeight + h];
                intensity += val * val;
              }
            }
            reducedM[x][y] = intensity / (reducerWidth * reducerHeight);
          }
        }
        break;
      default:
        throw new NotImplementedException();
    }
    return reducedM;
  }

  private static double getMedian(double[] numArray) {
    Arrays.sort(numArray);
    int length = numArray.length;
    return length % 2 == 0
        ? (numArray[length / 2 - 1] + numArray[length / 2]) / 2 : numArray[length / 2];
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
    s.close();
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
