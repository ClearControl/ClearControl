package clearcontrol.devices.stages.devices.sim;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.executors.AsynchronousSchedulerFeature;
import clearcontrol.core.device.sim.SimulationDeviceInterface;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.stages.StageDeviceBase;
import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.devices.stages.StageType;

/**
 * Stage simulator device
 *
 * @author royer
 */
public class StageDeviceSimulator extends StageDeviceBase implements
                                  StageDeviceInterface,
                                  SimulationDeviceInterface,
                                  AsynchronousSchedulerFeature,
                                  LoggingFeature

{
  private static final int cSimulationPeriodInMilliseconds = 10;

  private boolean mConstantSpeed;
  private double mEpsilon = 0.5;
  private double mSpeed = 0.05;
  private final double[] mDirectionVector = new double[4];

  /**
   * Instanciates a stage simulator device
   * 
   * @param pDeviceName
   *          device name
   * @param pStageType
   *          stage type
   */
  public StageDeviceSimulator(String pDeviceName,
                              StageType pStageType)
  {
    this(pDeviceName, pStageType, false);

  }

  /**
   * Instanciates a stage simulator device with given device name, stage type,
   * 
   * @param pDeviceName
   *          device name
   * @param pStageType
   *          stage type
   * @param pConstantSpeed
   *          true-> waits for
   */
  public StageDeviceSimulator(String pDeviceName,
                              StageType pStageType,
                              boolean pConstantSpeed)
  {
    super(pDeviceName, pStageType);
    mConstantSpeed = pConstantSpeed;

    if (mConstantSpeed)
    {
      goConstantSpeed();
    }
    else
    {
      goDynamicSpeed();
    }

  }

  private void goConstantSpeed()
  {
    scheduleAtFixedRate(() -> {
      try
      {
        computeDirectionVector();

        for (int i = 0; i < getNumberOfDOFs(); i++)
          if (mEnableVariables.get(i).get())
          {
            double lTarget = mTargetPositionVariables.get(i).get();
            double lCurrent = mCurrentPositionVariables.get(i).get();
            double lErrorLinear = lTarget - lCurrent;

            double lNewCurrent = lCurrent
                                 + mSpeed * mDirectionVector[i];

            if (abs(lErrorLinear) > mEpsilon)
              mCurrentPositionVariables.get(i).set(lNewCurrent);
            else if (!mReadyVariables.get(i).get())
            {
              mReadyVariables.get(i).set(true);
            }
          }

      }
      catch (Throwable e)
      {
        e.printStackTrace();
      }

    }, cSimulationPeriodInMilliseconds, TimeUnit.MILLISECONDS);
  }

  private void goDynamicSpeed()
  {

    scheduleAtFixedRate(() -> {
      try
      {

        for (int i = 0; i < getNumberOfDOFs(); i++)
          if (mEnableVariables.get(i).get())
          {
            double lTarget = mTargetPositionVariables.get(i).get();
            double lCurrent = mCurrentPositionVariables.get(i).get();
            double lErrorLinear = lTarget - lCurrent;

            double lNewCurrent = lCurrent
                                 + mSpeed * signum(lErrorLinear);
            // double lNewCurrent = lCurrent + mSpeed*mDirectionVector[i];

            if (abs(lErrorLinear) > mEpsilon)
              mCurrentPositionVariables.get(i).set(lNewCurrent);
            else if (!mReadyVariables.get(i).get())
              mReadyVariables.get(i).set(true);

          }
      }
      catch (Throwable e)
      {
        e.printStackTrace();
      }
    }, cSimulationPeriodInMilliseconds, TimeUnit.MILLISECONDS);

  }

  private void computeDirectionVector()
  {
    int n = getNumberOfDOFs();
    if (n == 0)
      return;

    for (int i = 0; i < n; i++)
      if (mEnableVariables.get(i).get())
      {
        if (isSimLogging())
          info("DOF " + i + ", " + getDOFNameByIndex(i));
        mDirectionVector[i] = mTargetPositionVariables.get(i).get()
                              - mCurrentPositionVariables.get(i)
                                                         .get();
        if (isSimLogging())
        {
          info("curr pos: " + mCurrentPositionVariables.get(i).get());
          info("target pos: "
               + mTargetPositionVariables.get(i).get());
        }
      }
    normalize(mDirectionVector);

  }

  private static void normalize(double[] pVector)
  {
    if (pVector.length == 0)
    {
      throw new IllegalArgumentException("Cannot normalise an empty vector! Returning null.");
    }
    else
    {
      double norm = 0.0;
      for (int i = 0; i < pVector.length; i++)
      {
        norm += pVector[i] * pVector[i];
      }
      norm = Math.sqrt(norm);
      if (norm > 0)
        for (int i = 0; i < pVector.length; i++)
        {
          pVector[i] = pVector[i] / norm;
        }

    }
  }

  /**
   * Sets positioning precision
   * 
   * @param pEpsilon
   *          epsilon
   */
  public void setPositioningPrecision(double pEpsilon)
  {
    this.mEpsilon = pEpsilon;
  }

  /**
   * Sets speed
   * 
   * @param pSpeed
   *          speed
   */
  public void setSpeed(double pSpeed)
  {
    this.mSpeed = pSpeed;
  }

  /**
   * Adds a DOF with given name, min and max
   * 
   * @param pDOFName
   *          dof name
   * @param pMin
   *          min
   * @param pMax
   *          max
   */
  public void addDOF(String pDOFName, double pMin, double pMax)
  {
    final int lDOFIndex = mIndexToNameMap.size();

    mIndexToNameMap.put(lDOFIndex, pDOFName);

    mEnableVariables.add(new Variable<Boolean>("Enable" + pDOFName,
                                               false));
    final Variable<Boolean> lEnableVariable =
                                            mEnableVariables.get(lDOFIndex);
    lEnableVariable.addSetListener((o, n) -> {
      if (isSimLogging())
        info("new enable state: " + n);
    });

    mReadyVariables.add(new Variable<Boolean>("Ready" + pDOFName,
                                              true));
    final Variable<Boolean> lReadyVariable =
                                           mReadyVariables.get(lDOFIndex);
    lReadyVariable.addSetListener((o, n) -> {
      if (isSimLogging())
        info("new ready state: " + n);
    });

    mHomingVariables.add(new Variable<Boolean>("Homing" + pDOFName,
                                               false));
    final Variable<Boolean> lHomingVariable =
                                            mHomingVariables.get(lDOFIndex);
    lHomingVariable.addSetListener((o, n) -> {
      if (isSimLogging())
        info("new homing state: " + n);
    });

    mStopVariables.add(new Variable<Boolean>("Stop" + pDOFName,
                                             false));
    final Variable<Boolean> lStopVariable =
                                          mStopVariables.get(lDOFIndex);
    lStopVariable.addSetListener((o, n) -> {
      if (isSimLogging())
        info("new stop state: " + n);
    });

    mResetVariables.add(new Variable<Boolean>("Reset" + pDOFName,
                                              false));
    final Variable<Boolean> lResetVariable =
                                           mResetVariables.get(lDOFIndex);
    lResetVariable.addSetListener((o, n) -> {
      if (isSimLogging())
        info("new reset state: " + n);
    });

    mTargetPositionVariables.add(new Variable<Double>("TargetPosition"
                                                      + pDOFName,
                                                      0.0));
    final Variable<Double> lTargetPositionVariable =
                                                   mTargetPositionVariables.get(lDOFIndex);
    lTargetPositionVariable.addSetListener((o, n) -> {
      if (isSimLogging())
        info("new target position: " + n);
    });

    mCurrentPositionVariables.add(new Variable<Double>("CurrentPosition"
                                                       + pDOFName,
                                                       0.0));
    final Variable<Double> lCurrentPositionVariable =
                                                    mCurrentPositionVariables.get(lDOFIndex);
    lCurrentPositionVariable.addSetListener((o, n) -> {
      if (isSimLogging())
        info("new current position: " + n);
    });

    mMinPositionVariables.add(new Variable<Double>("MinPosition"
                                                   + pDOFName, pMin));
    mMaxPositionVariables.add(new Variable<Double>("MaxPosition"
                                                   + pDOFName, pMax));
    mGranularityPositionVariables.add(new Variable<Double>("GranularityPosition"
                                                           + pDOFName,
                                                           0.1 * mSpeed));

    for (int i = 0; i < getNumberOfDOFs(); i++)
    {
      final int fi = i;

      mTargetPositionVariables.get(fi).addSetListener((o, n) -> {
        mReadyVariables.get(fi).set(false);
      });

      mHomingVariables.get(fi).addEdgeListener(n -> {
        if (n && mEnableVariables.get(fi).get()
            && mReadyVariables.get(fi).get())
        {
          mTargetPositionVariables.get(fi).set(0.0);
          mReadyVariables.get(fi).set(false);
        }
      });

      mResetVariables.get(fi).addEdgeListener(n -> {
        if (n)
          mReadyVariables.get(fi).set(true);
      });

      mStopVariables.get(fi).addEdgeListener(n -> {
        if (n)
        {
          mReadyVariables.get(fi).set(true);
          mTargetPositionVariables.get(fi)
                                  .set(mCurrentPositionVariables.get(fi)
                                                                .get());
        }
      });
    }
  }

  /**
   * Adds XYZR DOFs
   */
  public void addXYZRDOFs()
  {
    addXYZRDOFs(100, 100, 100);
  }

  /**
   * Adds XYZR DOFs
   * 
   * @param pRanges
   *          range to use, i.e. (100,100,100) -> -100<x<100, -100<y<100,
   *          -100<z<100,
   */
  public void addXYZRDOFs(double... pRanges)
  {
    double lRangeX = pRanges[0];
    double lRangeY = pRanges[1];
    double lRangeZ = pRanges[2];

    addDOF("X", -lRangeX, lRangeX);
    addDOF("Y", -lRangeY, lRangeY);
    addDOF("Z", -lRangeZ, lRangeZ);
    addDOF("R", 0, 360);
  }

}
