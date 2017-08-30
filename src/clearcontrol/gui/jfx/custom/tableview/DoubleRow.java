package clearcontrol.gui.jfx.custom.tableview;

import java.util.ArrayList;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;

/**
 * Row of doubles
 *
 * @author royer
 */
public class DoubleRow
{
  ArrayList<SimpleDoubleProperty> mDoublePropertyList =
                                                      new ArrayList<>();

  /**
   * Instantiates a row of doubles from a given list of doubles.
   * 
   * @param pRowEntries
   *          row entries
   */
  public DoubleRow(double... pRowEntries)
  {
    ensureEnoughColumns(pRowEntries.length);
    for (int i = 0; i < pRowEntries.length; i++)
    {
      mDoublePropertyList.get(i).set(pRowEntries[i]);
    }
  }

  /**
   * Sets the value for a given column index.
   * 
   * @param pColumnIndex
   *          column index
   * @param pValue
   *          value to set
   */
  public void setValue(int pColumnIndex, double pValue)
  {
    ensureEnoughColumns(pColumnIndex + 1);
    mDoublePropertyList.get(pColumnIndex).set(pValue);
  }

  /**
   * Returns the value for a given column index.
   * 
   * @param pColumnIndex
   *          column index
   * @return double property
   */
  public SimpleDoubleProperty getValue(int pColumnIndex)
  {
    ensureEnoughColumns(pColumnIndex + 1);

    SimpleDoubleProperty lSimpleDoubleProperty =
                                               mDoublePropertyList.get(pColumnIndex);
    /*System.out.format("getValue(%d) = %g \n",
                      pColumnIndex,
                      lSimpleDoubleProperty.get());/**/
    return lSimpleDoubleProperty;
  }

  /**
   * Adds a listener for the given cell in row
   * 
   * @param pColumnIndex
   *          column index
   * @param pListener
   *          change listener
   */
  public void addListener(int pColumnIndex,
                          ChangeListener<? super Number> pListener)
  {
    getValue(pColumnIndex).addListener(pListener);
  }

  private void ensureEnoughColumns(int pNumberOfColumns)
  {
    while (mDoublePropertyList.size() < pNumberOfColumns)
    {
      SimpleDoubleProperty lSimpleDoubleProperty =
                                                 new SimpleDoubleProperty();
      mDoublePropertyList.add(lSimpleDoubleProperty);
      /*lSimpleDoubleProperty.addListener((x,
                                         o,
                                         n) -> System.out.format("changed from %g to %g \n",
                                                                 o,
                                                                 n));/**/
    }

  }
}
