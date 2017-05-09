package clearcontrol.microscope.lightsheet.component.lightsheet;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.microscope.lightsheet.component.lightsheet.si.StructuredIlluminationPatternInterface;

/**
 * Lighthsheet parameter interface
 *
 * @author royer
 */
public interface LightSheetParameterInterface
{
  // These variables should be synced with camera variables:

  /**
   * Returns the variable holding the image height
   * 
   * @return image height variable
   */
  public Variable<Long> getImageHeightVariable();

  /**
   * Returns the variable holding the effective exposure in seconds
   * 
   * @return effective exposure in seconds variable
   */
  public BoundedVariable<Number> getEffectiveExposureInSecondsVariable();

  /**
   * Returns the variable holding the finalisation time in seconds
   * 
   * @return effective exposure in seconds variable
   */
  public BoundedVariable<Number> getFinalisationTimeInSecondsVariable();

  /**
   * Returns the variable holding the overscan value
   * 
   * @return overscan value variable
   */
  public BoundedVariable<Number> getOverScanVariable();

  /**
   * Returns the variable holding the readout time in microseconds per line
   * 
   * @return readout time in microseconds per line variable
   */
  public BoundedVariable<Number> getReadoutTimeInMicrosecondsPerLineVariable();

  // These variables can be set freeely:

  /**
   * Returns the variable holding the x position
   * 
   * @return x position variable
   */
  public BoundedVariable<Number> getXVariable();

  /**
   * Returns the variable holding the y position
   * 
   * @return y position variable
   */
  public BoundedVariable<Number> getYVariable();

  /**
   * Returns the variable holding the z position
   * 
   * @return z position variable
   */
  public BoundedVariable<Number> getZVariable();

  /**
   * Returns the variable holding the alpha angle
   * 
   * @return alpha angle variable
   */
  public BoundedVariable<Number> getAlphaInDegreesVariable();

  /**
   * Returns the variable holding the beta angle
   * 
   * @return beta angle variable
   */
  public BoundedVariable<Number> getBetaInDegreesVariable();

  /**
   * Returns the variable holding the width
   * 
   * @return width variable
   */
  public BoundedVariable<Number> getWidthVariable();

  /**
   * Returns the variable holding the height
   * 
   * @return height variable
   */
  public BoundedVariable<Number> getHeightVariable();

  /**
   * Returns the variable holding the power
   * 
   * @return power variable
   */
  public BoundedVariable<Number> getPowerVariable();

  /**
   * Returns the variable that holds the boolean flag for adapting power to with
   * and height
   * 
   * @return boolean flag for adapting power to with and height variable
   */
  public Variable<Boolean> getAdaptPowerToWidthHeightVariable();

  /**
   * Returns the laser on/off variable for a given laser index
   * 
   * @param pLaserIndex
   *          laser index
   * @return on/off variable
   */
  public Variable<Boolean> getLaserOnOffArrayVariable(int pLaserIndex);

  /**
   * Returns the SI pattern on/off variable or a given laser index
   * 
   * @param pLaserIndex
   *          laser index
   * @return SI pattern on/off variable
   */
  public Variable<Boolean> getSIPatternOnOffVariable(int pLaserIndex);

  /**
   * Returns the SI pattern variable for a given laser index
   * 
   * @param pLaserIndex
   *          laser index
   * @return SI pattern variable
   */
  public Variable<StructuredIlluminationPatternInterface> getSIPatternVariable(int pLaserIndex);

}
