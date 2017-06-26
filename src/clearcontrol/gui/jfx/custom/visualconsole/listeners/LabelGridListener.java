package clearcontrol.gui.jfx.custom.visualconsole.listeners;

/**
 * Label grid listener
 *
 * @author royer
 */
public interface LabelGridListener
{

  /**
   * Adds an entry to the grid
   * 
   * @param pTabName
   *          tab name
   * @param pClear
   *          clear
   * @param pFontSize
   *          font size
   * @param pColumnName
   *          column name
   * @param pRowName
   *          row name
   * @param pX
   *          x coordinate in grid
   * @param pY
   *          y coordinate in grid
   * @param pString
   *          text to put in grid cell
   */
  void addEntry(String pTabName,
                boolean pClear,
                String pColumnName,
                String pRowName,
                int pFontSize,
                int pX,
                int pY,
                String pString);

}
