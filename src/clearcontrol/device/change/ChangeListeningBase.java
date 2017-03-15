package clearcontrol.device.change;

import java.util.concurrent.CopyOnWriteArrayList;

import clearcontrol.core.math.interpolation.SplineInterpolationTable;

/**
 * Base class for providing basic change listener machinery for derived classes.
 * 
 * @author royer
 */
public abstract class ChangeListeningBase<E> implements
                                         HasChangeListenerInterface<E>
{
  CopyOnWriteArrayList<ChangeListener<E>> mListenersList =
                                                         new CopyOnWriteArrayList<>();

  /**
   * Adds a change listener
   * 
   * @param pListener
   *          listener to add
   */
  @Override
  public void addChangeListener(ChangeListener<E> pListener)
  {
    mListenersList.add(pListener);
  }

  /**
   * Removed a change listener
   * 
   * @param pListener
   *          listener to remove
   */
  @Override
  public void removeChangeListener(ChangeListener<E> pListener)
  {
    mListenersList.add(pListener);
  }

  /**
   * Notifies listeners of changes .
   */
  @SuppressWarnings("unchecked")
  @Override
  public void notifyListeners(E pEvent)
  {
    for (ChangeListener<E> lListener : mListenersList)
    {
      lListener.changed(pEvent);
    }
  }

  public abstract void addAtControlPlaneIA(int pControlPlaneIndex,
                                           int pLightSheetIndex,
                                           double lCorrection);

  public abstract void setAtControlPlaneIP(int czi, int l, double v);

  public abstract double getAtControlPlaneIP(int i, int l);

  public abstract void setAtControlPlaneIW(int pControlPlaneIndex,
                                           int pLightSheetIndex,
                                           Double aDouble);

  public abstract void setAtControlPlaneIX(int pControlPlaneIndex,
                                           int pLightSheetIndex,
                                           Double aDouble);

  public abstract void addAtControlPlaneIZ(int pControlPlaneIndex,
                                           int pLightSheetIndex,
                                           double lCorrection);

  public abstract int getNumberOfDevicesDZ();

  public abstract double getAtControlPlaneDZ(int czi, int d);

  public abstract int getNumberOfDevicesIX();

  public abstract double getAtControlPlaneIX(int czi, int i);

  public abstract double getAtControlPlaneIY(int czi, int i);

  public abstract double getAtControlPlaneIZ(int czi, int i);

  public abstract double getAtControlPlaneIA(int czi, int i);

  public abstract double getAtControlPlaneIB(int czi, int i);

  public abstract double getAtControlPlaneIW(int czi, int i);

  public abstract double getAtControlPlaneIH(int czi, int i);

  public abstract SplineInterpolationTable getDZTable();

  public abstract SplineInterpolationTable getIXTable();

  public abstract SplineInterpolationTable getIYTable();

  public abstract int getNumberOfDevicesIY();

  public abstract SplineInterpolationTable getIZTable();

  public abstract int getNumberOfDevicesIZ();

  public abstract SplineInterpolationTable getIATable();

  public abstract int getNumberOfDevicesIA();

  public abstract SplineInterpolationTable getIBTable();

  public abstract int getNumberOfDevicesIB();

  public abstract SplineInterpolationTable getIWTable();

  public abstract int getNumberOfDevicesIW();

  public abstract SplineInterpolationTable getIHTable();

  public abstract int getNumberOfDevicesIH();

  public abstract SplineInterpolationTable getIPTable();

  public abstract int getNumberOfDevicesIP();
}
