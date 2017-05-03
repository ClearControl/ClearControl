package clearcontrol.microscope.lightsheet.component.lightsheet.gui;

import clearcontrol.gui.jfx.var.customvarpanel.CustomVariablePane;
import clearcontrol.gui.jfx.var.onoffarray.OnOffArrayPane;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;

/**
 * Light sheet panel
 *
 * @author royer
 */
public class LightSheetPanel extends CustomVariablePane
{

  /**
   * Instanciates a light sheet panel
   * 
   * @param pLightSheetInterface
   *          light sheet
   */
  public LightSheetPanel(LightSheetInterface pLightSheetInterface)
  {
    super();

    {
      addTab("DOFs");

      addSliderForVariable("X :",
                           pLightSheetInterface.getXVariable(),
                           null).setUpdateIfChanging(true);

      addSliderForVariable("Y :",
                           pLightSheetInterface.getYVariable(),
                           null).setUpdateIfChanging(true);

      addSliderForVariable("Z :",
                           pLightSheetInterface.getZVariable(),
                           null).setUpdateIfChanging(true);

      addSliderForVariable("Alpha :",
                           pLightSheetInterface.getAlphaInDegreesVariable(),
                           null).setUpdateIfChanging(true);

      addSliderForVariable("Beta :",
                           pLightSheetInterface.getBetaInDegreesVariable(),
                           null).setUpdateIfChanging(true);

      addSliderForVariable("Width :",
                           pLightSheetInterface.getWidthVariable(),
                           null).setUpdateIfChanging(true);

      addSliderForVariable("Height :",
                           pLightSheetInterface.getHeightVariable(),
                           null).setUpdateIfChanging(true);

      addSliderForVariable("Power :",
                           pLightSheetInterface.getPowerVariable(),
                           null).setUpdateIfChanging(true);

      OnOffArrayPane lLaserOnOffArray = addOnOffArray("Laser :");

      int lNumberOfLaserDigitalControls =
                                        pLightSheetInterface.getNumberOfLaserDigitalControls();
      for (int l = 0; l < lNumberOfLaserDigitalControls; l++)
      {
        lLaserOnOffArray.addSwitch("L" + l,
                                   pLightSheetInterface.getLaserOnOffArrayVariable(l));
      }
    }

    {
      addTab("Functions");

      addFunctionPane("X function",
                      pLightSheetInterface.getXFunction());
      addFunctionPane("Y function",
                      pLightSheetInterface.getYFunction());
      addFunctionPane("Z function",
                      pLightSheetInterface.getZFunction());

      addFunctionPane("Alpha function",
                      pLightSheetInterface.getAlphaFunction());
      addFunctionPane("Beta function",
                      pLightSheetInterface.getBetaFunction());

      addFunctionPane("Width function",
                      pLightSheetInterface.getWidthFunction());
      addFunctionPane("Height function",
                      pLightSheetInterface.getHeightFunction());

      addFunctionPane("Power function",
                      pLightSheetInterface.getPowerFunction());
    }

    {
      addTab("Bounds");

      addBoundedVariable("X :", pLightSheetInterface.getXVariable());

      addBoundedVariable("Y :", pLightSheetInterface.getYVariable());

      addBoundedVariable("Z :", pLightSheetInterface.getZVariable());

      addBoundedVariable("Alpha :",
                         pLightSheetInterface.getAlphaInDegreesVariable());

      addBoundedVariable("Beta :",
                         pLightSheetInterface.getBetaInDegreesVariable());

      addBoundedVariable("Width :",
                         pLightSheetInterface.getWidthVariable());

      addBoundedVariable("Height :",
                         pLightSheetInterface.getHeightVariable());

      addBoundedVariable("Power :",
                         pLightSheetInterface.getPowerVariable());
    }

    {
      addTab("Advanced");

      addSliderForVariable("EffectiveExposure :",
                           pLightSheetInterface.getEffectiveExposureInSecondsVariable(),
                           1e-6,
                           1,
                           0.001,
                           0.1).setUpdateIfChanging(true);

      /*addSliderForVariable("LineExposure :",
                           pLightSheetInterface.getLineExposureInMicrosecondsVariable(),
                           1.0,
                           1000000.0,
                           1.0,
                           500000.0).setUpdateIfChanging(true);/**/

      addSliderForVariable("Overscan :",
                           pLightSheetInterface.getOverScanVariable(),
                           0.0,
                           2.0,
                           0.01,
                           0.1).setUpdateIfChanging(true);

      addSliderForVariable("Readout Time :",
                           pLightSheetInterface.getReadoutTimeInMicrosecondsPerLineVariable(),
                           0.0,
                           10.0,
                           0.0,
                           1.0).setUpdateIfChanging(true);
    }

  }

}
