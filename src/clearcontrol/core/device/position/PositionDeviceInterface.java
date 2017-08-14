package clearcontrol.core.device.position;

import clearcontrol.core.variable.Variable;

/**
 * 
 *
 * @author royer
 */
public interface PositionDeviceInterface
{
  /**
   * Returns the position variable
   * 
   * @return position variable
   */
  Variable<Integer> getPositionVariable();

  /**
   * Convenience method returning the current position
   * 
   * @return current position
   */
  default int getPosition()
  {
    return getPositionVariable().get();
  }

  /**
   * Convenience method that sets the new current position
   * 
   * @param pPositionIndex
   */
  default void setPosition(int pPositionIndex)
  {
    getPositionVariable().set(pPositionIndex);
  }

  /**
   * Returns the list of valid position
   * 
   * @return array of valid positions
   */
  int[] getValidPositions();

  /**
   * Sets the position name for a given position index.
   * 
   * @param pPositionIndex
   *          position index
   * @param pPositionName
   *          position name
   */
  void setPositionName(int pPositionIndex, String pPositionName);

  /**
   * Returns the position name at a given index.
   * 
   * @param pPositionIndex
   *          position index
   * @return position name
   */
  String getPositionName(int pPositionIndex);
}
