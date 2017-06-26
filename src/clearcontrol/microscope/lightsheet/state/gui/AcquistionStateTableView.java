package clearcontrol.microscope.lightsheet.state.gui;

import java.util.concurrent.ConcurrentHashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import clearcontrol.gui.jfx.custom.tableview.DoubleRow;
import clearcontrol.gui.jfx.custom.tableview.DoubleTableView;
import clearcontrol.gui.jfx.custom.tableview.EditableTableCellHandler;
import clearcontrol.microscope.lightsheet.LightSheetDOF;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Multichart displaying the different acquisition DOFs versus imaging depth
 *
 * @author royer
 */
public class AcquistionStateTableView extends DoubleTableView
{

  private ConcurrentHashMap<String, Integer> mNameToColumnMap =
                                                              new ConcurrentHashMap<>();

  /**
   * Instantiates an acquisition multichart for a given acquisition state
   * 
   * @param pAcquisitionState
   *          acquisition state
   */
  public AcquistionStateTableView(final InterpolatedAcquisitionState pAcquisitionState)
  {
    super(60);

    setMaxWidth(Double.MAX_VALUE);
    setMaxHeight(Double.MAX_VALUE);

    int lNumberOfDetectionArms =
                               pAcquisitionState.getNumberOfDetectionArms();
    int lNumberOfIlluminationArms =
                                  pAcquisitionState.getNumberOfLightSheets();

    mNameToColumnMap.put("CPI", addColumn("CPI", false, false));

    Pair<String, EditableTableCellHandler> lRemoveControlPlaneMenuItem,
        lAddControlPlaneMenuItem;

    lAddControlPlaneMenuItem =
                             Pair.of("Add control plane", (e, c) -> {
                               Double lZ = c.getItem();
                               pAcquisitionState.getInterpolationTables()
                                                .addControlPlaneAfter(lZ);
                             });

    lRemoveControlPlaneMenuItem = Pair.of("Remove control plane",
                                          (e, c) -> {
                                            if (pAcquisitionState.getNumberOfControlPlanes() <= 1)
                                              return;
                                            Double lZ = c.getItem();
                                            pAcquisitionState.getInterpolationTables()
                                                             .removeControlPlane(lZ);
                                          });

    mNameToColumnMap.put("ZR", addColumn("ZR",
                                         true,
                                         false,
                                         lAddControlPlaneMenuItem,
                                         lRemoveControlPlaneMenuItem));

    for (int d = 0; d < lNumberOfDetectionArms; d++)
      mNameToColumnMap.put("DZ" + d, addColumn("DZ" + d, true, true));

    for (int i = 0; i < lNumberOfIlluminationArms; i++)
      mNameToColumnMap.put("IX" + i, addColumn("IX" + i, true, true));

    for (int i = 0; i < lNumberOfIlluminationArms; i++)
      mNameToColumnMap.put("IY" + i, addColumn("IY" + i, true, true));

    for (int i = 0; i < lNumberOfIlluminationArms; i++)
      mNameToColumnMap.put("IZ" + i, addColumn("IZ" + i, true, true));

    for (int i = 0; i < lNumberOfIlluminationArms; i++)
      mNameToColumnMap.put("IA" + i, addColumn("IA" + i, true, true));

    for (int i = 0; i < lNumberOfIlluminationArms; i++)
      mNameToColumnMap.put("IB" + i, addColumn("IB" + i, true, true));

    for (int i = 0; i < lNumberOfIlluminationArms; i++)
      mNameToColumnMap.put("IH" + i, addColumn("IH" + i, true, true));

    for (int i = 0; i < lNumberOfIlluminationArms; i++)
      mNameToColumnMap.put("IW" + i, addColumn("IW" + i, true, true));

    for (int i = 0; i < lNumberOfIlluminationArms; i++)
      mNameToColumnMap.put("IP" + i, addColumn("IP" + i, true, true));

    updateTable(pAcquisitionState);
  }

  /**
   * Updates this table with the given acquisition state
   * 
   * @param pAcquisitionState
   *          acquisition state to use for updating this table
   */
  public void updateTable(InterpolatedAcquisitionState pAcquisitionState)
  {
    ObservableList<DoubleRow> lTableData =
                                         FXCollections.observableArrayList();

    int lNumberOfControlPlanes =
                               pAcquisitionState.getNumberOfControlPlanes();
    int lNumberOfDetectionArms =
                               pAcquisitionState.getNumberOfDetectionArms();
    int lNumberOfIlluminationArms =
                                  pAcquisitionState.getNumberOfLightSheets();

    for (int cpi = 0; cpi < lNumberOfControlPlanes; cpi++)
    {
      DoubleRow lRow = new DoubleRow();
      lTableData.add(lRow);

      {
        int lColumnIndex = mNameToColumnMap.get("CPI");

        lRow.setValue(lColumnIndex, cpi);
      }

      {
        int lColumnIndex = mNameToColumnMap.get("ZR");

        lRow.setValue(lColumnIndex,
                      pAcquisitionState.getControlPlaneZ(cpi));

        final int cpif = cpi;
        lRow.addListener(lColumnIndex,
                         (x,
                          o,
                          n) -> pAcquisitionState.getInterpolationTables()
                                                 .changeControlPlane(cpif,
                                                                     n.doubleValue()));

      }

      for (int d = 0; d < lNumberOfDetectionArms; d++)
      {
        int lColumnIndex = mNameToColumnMap.get("DZ" + d);

        lRow.setValue(lColumnIndex,
                      pAcquisitionState.getInterpolationTables()
                                       .get(LightSheetDOF.DZ,
                                            cpi,
                                            d));

        final int cpif = cpi;
        final int df = d;
        lRow.addListener(lColumnIndex,
                         (x,
                          o,
                          n) -> pAcquisitionState.getInterpolationTables()
                                                 .set(LightSheetDOF.DZ,
                                                      cpif,
                                                      df,
                                                      n.doubleValue()));
      }

      for (LightSheetDOF lLightSheetDOF : LightSheetDOF.values())
        if (lLightSheetDOF != LightSheetDOF.DZ)
        {
          for (int i = 0; i < lNumberOfIlluminationArms; i++)
          {
            int lColumnIndex =
                             mNameToColumnMap.get(lLightSheetDOF.toString()
                                                  + i);

            lRow.setValue(lColumnIndex,
                          pAcquisitionState.getInterpolationTables()
                                           .get(lLightSheetDOF,
                                                cpi,
                                                i));

            final int cpif = cpi;
            final int iff = i;
            lRow.addListener(lColumnIndex,
                             (x,
                              o,
                              n) -> pAcquisitionState.getInterpolationTables()
                                                     .set(lLightSheetDOF,
                                                          cpif,
                                                          iff,
                                                          n.doubleValue()));

          }
        }

    }

    setItems(lTableData);
  }

}
