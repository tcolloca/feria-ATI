package util.pf;

import com.goodengineer.atibackend.model.Band;
import com.goodengineer.atibackend.model.ColorImage;
import com.goodengineer.atibackend.model.Image;
import com.goodengineer.atibackend.transformation.Transformation;

import java.util.Arrays;
import java.util.List;

public class SarImage extends ColorImage {

  SarBand band;

  public SarImage(SarBand band) {
    super(null, null, null);
    this.band = band;
  }

  @Override
  public void transform(Transformation transformation) {
    transformation.transform(band);
  }

  @Override
  public Image clone() {
    SarBand band = this.band.clone();
    return new SarImage(band);
  }

  @Override
  public int getWidth() {
    return band.getWidth();
  }

  @Override
  public int getHeight() {
    return band.getHeight();
  }

  public int getRed(int x, int y) {
    return band.getPixel(x, y);
  }

  public int getGreen(int x, int y) {
    return band.getPixel(x, y);
  }

  public int getBlue(int x, int y) {
    return band.getPixel(x, y);
  }
  
  @Override
  public int getGray(int x, int y) {
	  return band.getPixel(x, y);
  }

  public List<Band> getBands() {
    return Arrays.asList(band, band, band);
  }

}
