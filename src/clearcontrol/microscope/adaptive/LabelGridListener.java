package clearcontrol.microscope.adaptive;

import clearcontrol.microscope.adaptive.modules.AdaptationModuleInterface;

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
   * @param pModule
   * @param pName
   * @param pClear
   * @param pColumnName
   * @param pRowName
   * @param pX
   * @param pY
   * @param pString
   */
  void addEntry(AdaptationModuleInterface<?> pModule,
                String pName,
                boolean pClear,
                String pColumnName,
                String pRowName,
                int pX,
                int pY,
                String pString);

}
