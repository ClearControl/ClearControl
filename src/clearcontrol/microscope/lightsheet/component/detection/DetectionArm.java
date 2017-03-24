package clearcontrol.microscope.lightsheet.component.detection;

import java.util.concurrent.Future;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.device.QueueableVirtualDevice;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.core.variable.bounded.BoundedVariable;

/**
 * Light sheet microscope detection arm
 *
 * @author royer
 */
public class DetectionArm extends QueueableVirtualDevice implements
                          DetectionArmInterface,
                          LoggingInterface
{

  private final BoundedVariable<Number> mDetectionFocusZ =
                                                         new BoundedVariable<Number>("FocusZ",
                                                                                     0.0);

  private final Variable<UnivariateAffineFunction> mZFunction =
                                                              new Variable<>("DetectionZFunction",
                                                                             new UnivariateAffineFunction());

  /**
   * Instanciates a lightsheet microscope detection arm
   * 
   * @param pName
   *          detection arm name
   */
  @SuppressWarnings("unchecked")
  public DetectionArm(String pName)
  {
    super(pName);

    resetFunctions();
    resetBounds();

    @SuppressWarnings("rawtypes")
    final VariableSetListener lVariableListener = (o, n) -> {
      // System.out.println(getName() + ": new Z value: " + n);
      notifyListeners(this);
    };

    getVariableStateQueues().registerVariable(mDetectionFocusZ);
    mDetectionFocusZ.addSetListener(lVariableListener);

    final VariableSetListener<UnivariateAffineFunction> lFunctionVariableListener =
                                                                                  (o,
                                                                                   n) -> {
                                                                                    info("new Z function: "
                                                                                         + n);
                                                                                    resetBounds();
                                                                                    notifyListeners(this);
                                                                                  };

    mZFunction.addSetListener(lFunctionVariableListener);

    notifyListeners(this);
  }

  @Override
  public void resetFunctions()
  {

  }

  @Override
  public void resetBounds()
  {

    MachineConfiguration.getCurrentMachineConfiguration()
                        .getBoundsForVariable("device.lsm.detection."
                                              + getName()
                                              + ".z.bounds",
                                              mDetectionFocusZ,
                                              mZFunction.get());

  }

  @Override
  public BoundedVariable<Number> getZVariable()
  {
    return mDetectionFocusZ;
  }

  @Override
  public Variable<UnivariateAffineFunction> getZFunction()
  {
    return mZFunction;
  }

  @Override
  public Future<Boolean> playQueue()
  {
    return null;
  }
}
