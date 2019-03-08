package clearcontrol.devices.signalgen.measure;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.devices.signalgen.staves.StaveInterface;

/**
 * Measure interface
 *
 * @author Loic Royer (2015)
 *
 */
public interface MeasureInterface extends NameableInterface
{

  /**
   * Returns a field_for_field copy of this measure.
   * 
   * @return field_for_field copy
   */
  MeasureInterface duplicate();

  /**
   * Sets stave at index.
   * 
   * @param pStaveIndex
   *          stave index
   * @param pStave
   *          stave
   */
  void setStave(int pStaveIndex, StaveInterface pStave);

  /**
   * Sets stave at index if not already set. Returns the set or already set
   * stave.
   * 
   * @param pStaveIndex
   *          stave index
   * @param pNewStave
   *          new stave
   * @return currently set stave
   */
  public <O extends StaveInterface> O ensureSetStave(int pStaveIndex,
                                                     O pNewStave);

  /**
   * Returns the stave for a given index
   * 
   * @param pStaveIndex
   *          stave index
   * @return stave
   */
  StaveInterface getStave(int pStaveIndex);

  /**
   * Number of staves in Measure
   * 
   * @return number of staves
   */
  int getNumberOfStaves();

  /**
   * Returns the duration of the stave in the provided time unit.
   * 
   * @param pDuration
   *          duration of the measure
   * @param pTimeUnit
   *          time unit
   */
  void setDuration(long pDuration, TimeUnit pTimeUnit);

  /**
   * Returns the duration of the stave in the provided time unit.
   * 
   * @param pTimeUnit
   *          time unit
   * @return time
   */
  long getDuration(TimeUnit pTimeUnit);

  /**
   * Sets whether this measure can be triggered (synced).
   * 
   * @param pSync
   *          true if sync, false if not.
   */
  public void setSync(boolean pSync);

  /**
   * Returns if syncing is activated for this measure.
   * 
   * @return true if syncing is activated for this
   */
  boolean isSync();

  /**
   * Sets whether this measure should sync to a rising or falling edge.
   * 
   * @param pSyncOnRisingEdge
   *          true if sync on raising edge, false if sync on falling edge
   */
  public void setSyncOnRisingEdge(boolean pSyncOnRisingEdge);

  /**
   * Returns the sync condition.
   * 
   * @return true if sync on rising edge, false if on falling edge
   */
  boolean isSyncOnRisingEdge();

  /**
   * Sets the sync channel
   * 
   * @param pSyncChannel
   *          sync channel
   */
  public void setSyncChannel(int pSyncChannel);

  /**
   * Returns the sync channel index.
   * 
   * @return sync channel index
   */
  int getSyncChannel();

}
