package clearcontrol.microscope.lightsheet.calibrator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import clearcontrol.core.math.functions.PolynomialFunction;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;

public class CalibrationData
{

  private static ObjectMapper sObjectMapper;
  static
  {
    sObjectMapper = new ObjectMapper();
    sObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    sObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                            false);
  }

  public UnivariateAffineFunction[] mLightSheetXFunctions,
      mLightSheetYFunctions, mLightSheetZFunctions,
      mLightSheetWidthFunctions, mLightSheetHeightFunctions,
      mLightSheetAlphaFunctions, mLightSheetBetaFunctions,
      mLightSheetPowerFunctions, mDetectionArmZFunctions;

  public PolynomialFunction[] mLightSheetWidthPowerFunctions,
      mLightSheetHeightPowerFunctions;

  public HashMap<String, LightSheetPositioner> mPositionerMap =
                                                              new HashMap<>();

  public CalibrationData()
  {

  }

  public CalibrationData(LightSheetMicroscope pLightSheetMicroscope)
  {
    super();

    int lNumberOfLightSheets =
                             pLightSheetMicroscope.getDeviceLists()
                                                  .getNumberOfDevices(LightSheetInterface.class);

    int lNumberOfDetectioArms =
                              pLightSheetMicroscope.getDeviceLists()
                                                   .getNumberOfDevices(DetectionArmInterface.class);

    mLightSheetXFunctions =
                          new UnivariateAffineFunction[lNumberOfLightSheets];
    mLightSheetYFunctions =
                          new UnivariateAffineFunction[lNumberOfLightSheets];
    mLightSheetZFunctions =
                          new UnivariateAffineFunction[lNumberOfLightSheets];
    mLightSheetWidthFunctions =
                              new UnivariateAffineFunction[lNumberOfLightSheets];
    mLightSheetHeightFunctions =
                               new UnivariateAffineFunction[lNumberOfLightSheets];
    mLightSheetAlphaFunctions =
                              new UnivariateAffineFunction[lNumberOfLightSheets];
    mLightSheetBetaFunctions =
                             new UnivariateAffineFunction[lNumberOfLightSheets];
    mLightSheetPowerFunctions =
                              new UnivariateAffineFunction[lNumberOfLightSheets];
    mLightSheetWidthPowerFunctions =
                                   new PolynomialFunction[lNumberOfLightSheets];
    mLightSheetHeightPowerFunctions =
                                    new PolynomialFunction[lNumberOfLightSheets];

    mDetectionArmZFunctions =
                            new UnivariateAffineFunction[lNumberOfDetectioArms];
  }

  public void applyTo(LightSheetMicroscope pLightSheetMicroscope)
  {

    for (int l = 0; l < mLightSheetXFunctions.length; l++)
    {
      LightSheetInterface lLightSheetDevice =
                                            pLightSheetMicroscope.getDeviceLists()
                                                                 .getDevice(LightSheetInterface.class,
                                                                            l);

      lLightSheetDevice.getXFunction()
                       .set(new UnivariateAffineFunction(mLightSheetXFunctions[l]));
      lLightSheetDevice.getYFunction()
                       .set(new UnivariateAffineFunction(mLightSheetYFunctions[l]));
      lLightSheetDevice.getZFunction()
                       .set(new UnivariateAffineFunction(mLightSheetZFunctions[l]));
      lLightSheetDevice.getWidthFunction()
                       .set(new UnivariateAffineFunction(mLightSheetWidthFunctions[l]));
      lLightSheetDevice.getHeightFunction()
                       .set(new UnivariateAffineFunction(mLightSheetHeightFunctions[l]));
      lLightSheetDevice.getAlphaFunction()
                       .set(new UnivariateAffineFunction(mLightSheetAlphaFunctions[l]));
      lLightSheetDevice.getBetaFunction()
                       .set(new UnivariateAffineFunction(mLightSheetBetaFunctions[l]));
      lLightSheetDevice.getPowerFunction()
                       .set(new UnivariateAffineFunction(mLightSheetPowerFunctions[l]));
      lLightSheetDevice.getWidthPowerFunction()
                       .set(new PolynomialFunction(mLightSheetWidthPowerFunctions[l].getCoefficients()));
      lLightSheetDevice.getHeightPowerFunction()
                       .set(new PolynomialFunction(mLightSheetHeightPowerFunctions[l].getCoefficients()));
    }

    for (int d = 0; d < mDetectionArmZFunctions.length; d++)
    {
      DetectionArmInterface lDetectionArmDevice =
                                                pLightSheetMicroscope.getDeviceLists()
                                                                     .getDevice(DetectionArmInterface.class,
                                                                                d);

      lDetectionArmDevice.getZFunction()
                         .set(new UnivariateAffineFunction(mDetectionArmZFunctions[d]));
    }

  }

  public void copyFrom(LightSheetMicroscope pLightSheetMicroscope)
  {

    for (int l = 0; l < mLightSheetXFunctions.length; l++)
    {
      LightSheetInterface lLightSheetDevice =
                                            pLightSheetMicroscope.getDeviceLists()
                                                                 .getDevice(LightSheetInterface.class,
                                                                            l);

      mLightSheetXFunctions[l] =
                               new UnivariateAffineFunction(lLightSheetDevice.getXFunction()
                                                                             .get());
      mLightSheetYFunctions[l] =
                               new UnivariateAffineFunction(lLightSheetDevice.getYFunction()
                                                                             .get());
      mLightSheetZFunctions[l] =
                               new UnivariateAffineFunction(lLightSheetDevice.getZFunction()
                                                                             .get());
      mLightSheetWidthFunctions[l] =
                                   new UnivariateAffineFunction(lLightSheetDevice.getWidthFunction()
                                                                                 .get());
      mLightSheetHeightFunctions[l] =
                                    new UnivariateAffineFunction(lLightSheetDevice.getHeightFunction()
                                                                                  .get());
      mLightSheetAlphaFunctions[l] =
                                   new UnivariateAffineFunction(lLightSheetDevice.getAlphaFunction()
                                                                                 .get());
      mLightSheetBetaFunctions[l] =
                                  new UnivariateAffineFunction(lLightSheetDevice.getBetaFunction()
                                                                                .get());
      mLightSheetPowerFunctions[l] =
                                   new UnivariateAffineFunction(lLightSheetDevice.getPowerFunction()
                                                                                 .get());
      mLightSheetWidthPowerFunctions[l] =
                                        new PolynomialFunction(lLightSheetDevice.getWidthPowerFunction()
                                                                                .get()
                                                                                .getCoefficients());
      mLightSheetHeightPowerFunctions[l] =
                                         new PolynomialFunction(lLightSheetDevice.getHeightPowerFunction()
                                                                                 .get()
                                                                                 .getCoefficients());
    }

    for (int d = 0; d < mDetectionArmZFunctions.length; d++)
    {
      DetectionArmInterface lDetectionArmDevice =
                                                pLightSheetMicroscope.getDeviceLists()
                                                                     .getDevice(DetectionArmInterface.class,
                                                                                d);

      mDetectionArmZFunctions[d] =
                                 new UnivariateAffineFunction(lDetectionArmDevice.getZFunction()
                                                                                 .get());
    }
  }

  public void copyFrom(HashMap<String, LightSheetPositioner> pPositionerMap)
  {
    mPositionerMap.clear();
    mPositionerMap.putAll(pPositionerMap);
  }

  public void applyTo(HashMap<String, LightSheetPositioner> pPositionersMap)
  {
    pPositionersMap.clear();
    pPositionersMap.putAll(mPositionerMap);
  }

  public void saveTo(File pFile) throws JsonGenerationException,
                                 JsonMappingException,
                                 IOException
  {
    sObjectMapper.writeValue(pFile, this);
  }

  public static CalibrationData readFrom(File pFile) throws JsonParseException,
                                                     JsonMappingException,
                                                     IOException
  {
    return sObjectMapper.readValue(pFile, CalibrationData.class);
  }

}
