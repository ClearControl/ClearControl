package clearcontrol.microscope.lightsheet.calibrator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import clearcontrol.core.math.functions.PolynomialFunction;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Calibration data
 *
 * @author royer
 */
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

  @SuppressWarnings("javadoc")
  public UnivariateAffineFunction[] mLightSheetXFunctions,
      mLightSheetYFunctions, mLightSheetZFunctions,
      mLightSheetWidthFunctions, mLightSheetHeightFunctions,
      mLightSheetAlphaFunctions, mLightSheetBetaFunctions,
      mLightSheetPowerFunctions, mDetectionArmZFunctions;

  @SuppressWarnings("javadoc")
  public PolynomialFunction[] mLightSheetWidthPowerFunctions,
      mLightSheetHeightPowerFunctions;

  @SuppressWarnings("javadoc")
  public HashMap<String, LightSheetPositioner> mPositionerMap =
                                                              new HashMap<>();

  /**
   * Instantiates a calibration data object
   */
  public CalibrationData()
  {

  }

  /**
   * Instanciates a calibration data object for a given lightsheet microscope
   * 
   * @param pLightSheetMicroscope
   *          lightsheet microscope
   */
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

  /**
   * Aplies this calibration data to a given lightsheet microscope
   * 
   * @param pLightSheetMicroscope
   *          lightsheet microscope
   */
  public void applyTo(LightSheetMicroscope pLightSheetMicroscope)
  {

    for (int l = 0; l < mLightSheetXFunctions.length; l++)
    {
      LightSheetInterface lLightSheetDevice =
                                            pLightSheetMicroscope.getDeviceLists()
                                                                 .getDevice(LightSheetInterface.class,
                                                                            l);
      if (lLightSheetDevice == null)
        continue;

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
      if (lDetectionArmDevice == null)
        continue;

      lDetectionArmDevice.getZFunction()
                         .set(new UnivariateAffineFunction(mDetectionArmZFunctions[d]));
    }

  }

  /**
   * Sets this calibration data to the current calibration of a given lightsheet
   * microscope
   * 
   * @param pLightSheetMicroscope
   *          lightsheet microscope
   */
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

  /**
   * Adds the provided list of positioners into this
   * 
   * @param pPositionerMap
   *          positioner map
   */
  public void copyFrom(Map<String, LightSheetPositioner> pPositionerMap)
  {
    mPositionerMap.clear();
    mPositionerMap.putAll(pPositionerMap);
  }

  /**
   * Adds the list of positioners in this calibration to the provided map
   * 
   * @param pPositionersMap
   *          positioner map
   */
  public void copyTo(Map<String, LightSheetPositioner> pPositionersMap)
  {
    pPositionersMap.clear();
    pPositionersMap.putAll(mPositionerMap);
  }

  /**
   * Writes this calibration data to a gievn file
   * 
   * @param pFile
   *          file
   */
  public void saveTo(File pFile)
  {
    try
    {
      sObjectMapper.writeValue(pFile, this);
    }
    catch (IOException e)
    {
      throw new RuntimeException("Problem while writing calibration information to file: "
                                 + pFile.getAbsolutePath(), e);
    }
  }

  /**
   * Reads this calibration data from a given file
   * 
   * @param pFile
   *          file
   * @return calibration data
   */
  public static CalibrationData readFrom(File pFile)
  {
    try
    {
      return sObjectMapper.readValue(pFile, CalibrationData.class);
    }
    catch (IOException e)
    {
      throw new RuntimeException("Problem while reading calibration information to file: "
                                 + pFile.getAbsolutePath(), e);
    }

  }

}
