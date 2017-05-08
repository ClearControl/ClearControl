package clearcontrol.microscope.lightsheet.adaptor.test;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.NameableWithChangeListener;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.state.LightSheetAcquisitionStateInterface;
import clearcontrol.microscope.lightsheet.state.tables.InterpolationTables;
import clearcontrol.microscope.state.AcquisitionStateInterface;

/**
 * Acquisition state for testing purposes
 *
 * @author royer
 */
public class TestState extends
                       NameableWithChangeListener<AcquisitionStateInterface<LightSheetMicroscopeInterface, LightSheetMicroscopeQueue>>
                       implements
                       LightSheetAcquisitionStateInterface<TestState>
{

  /**
   * Instanciates a test acquisition state
   * 
   * @param pName
   *          state name
   */
  public TestState(String pName)
  {
    super(pName);
  }

  @Override
  public TestState copy(String pName)
  {
    return new TestState(pName);
  }
  
  @Override
  public void prepareAcquisition(long pTimeOut, TimeUnit pTimeUnit)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public LightSheetMicroscopeQueue getQueue(int pCameraIndexMin,
                                            int pCameraIndexMax,
                                            int pLightSheetIndexMin,
                                            int pLightSheetIndexMax,
                                            int pLaserLineIndexMin,
                                            int pLaserLineIndexMax)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public LightSheetMicroscopeQueue getQueue()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InterpolationTables getInterpolationTables()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getBestDetectionArm(int pControlPlaneIndex)
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void applyStateAtControlPlane(LightSheetMicroscopeQueue pQueue,
                                       int pControlPlaneIndex)
  {
    // TODO Auto-generated method stub

  }


  @Override
  public BoundedVariable<Number> getStageXVariable()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public BoundedVariable<Number> getStageYVariable()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public BoundedVariable<Number> getStageZVariable()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Variable<Boolean> getLightSheetOnOffVariable(int pLightSheetIndex)
  {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public Variable<Boolean> getCameraOnOffVariable(int pCameraIndex)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Variable<Boolean> getLaserOnOffVariable(int pLaserLineIndex)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
