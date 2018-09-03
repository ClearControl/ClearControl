package clearcontrol.gui.video.util;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.jfx.var.textfield.NumberVariableTextField;
import clearcontrol.gui.video.video2d.videowindow.VideoWindow;
import clearvolume.renderer.ClearVolumeRendererInterface;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG
 * (http://mpi-cbg.de) November 2017
 */
public class MinMaxControlDialog extends Dialog<Pair<String, String>>
{
  public MinMaxControlDialog(String title,
                             BoundedVariable<Double> lMinGreyValue,
                             BoundedVariable<Double> lMaxGreyValue,
                             BoundedVariable<Double> lGamma,
                             BoundedVariable<Double> lBrightness)
  {

    setTitle("Visualisation min / max");

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    NumberVariableTextField<Double> lMinTextField =
                                                  new NumberVariableTextField<Double>(lMinGreyValue.getName(),
                                                                                      lMinGreyValue);
    NumberVariableTextField<Double> lMaxTextField =
                                                  new NumberVariableTextField<Double>(lMaxGreyValue.getName(),
                                                                                      lMaxGreyValue);
    NumberVariableTextField<Double> lGammaTextField =
                                                    new NumberVariableTextField<Double>(lGamma.getName(),
                                                                                        lGamma);
    NumberVariableTextField<Double> lBrightnessTextField =
                                                         new NumberVariableTextField<Double>(lBrightness.getName(),
                                                                                             lBrightness);

    grid.add(lMinTextField.getLabel(), 0, 0);
    grid.add(lMinTextField.getTextField(), 1, 0);
    grid.add(lMaxTextField.getLabel(), 0, 1);
    grid.add(lMaxTextField.getTextField(), 1, 1);
    grid.add(lGammaTextField.getLabel(), 0, 2);
    grid.add(lGammaTextField.getTextField(), 1, 2);
    grid.add(lBrightnessTextField.getLabel(), 0, 3);
    grid.add(lBrightnessTextField.getTextField(), 1, 3);

    getDialogPane().setContent(grid);

    getDialogPane().getButtonTypes().addAll(ButtonType.OK);
  }

  public static void showDialog(ClearVolumeRendererInterface pClearVolumeRendererInterface)
  {

    final ClearVolumeRendererInterface mClearVolumeRendererInterface =
                                                                     pClearVolumeRendererInterface;

    BoundedVariable<Double> lMinGreyValue =
                                          new BoundedVariable<Double>("Min",
                                                                      mClearVolumeRendererInterface.getTransferRangeMin())
                                          {
                                            @Override
                                            public Double setEventHook(Double pOldReference,
                                                                       Double pNewReference)
                                            {
                                              Platform.runLater(new Runnable()
                                              {
                                                @Override
                                                public void run()
                                                {
                                                  mClearVolumeRendererInterface.setTransferFunctionRangeMin(pNewReference);
                                                }
                                              });

                                              return super.setEventHook(pOldReference,
                                                                        pNewReference);
                                            }
                                          };
    BoundedVariable<Double> lMaxGreyValue =
                                          new BoundedVariable<Double>("Max",
                                                                      mClearVolumeRendererInterface.getTransferRangeMax())
                                          {
                                            @Override
                                            public Double setEventHook(Double pOldReference,
                                                                       Double pNewReference)
                                            {
                                              Platform.runLater(new Runnable()
                                              {
                                                @Override
                                                public void run()
                                                {
                                                  mClearVolumeRendererInterface.setTransferFunctionRangeMax(pNewReference);
                                                }
                                              });

                                              return super.setEventHook(pOldReference,
                                                                        pNewReference);
                                            }
                                          };
    BoundedVariable<Double> lGamma =
                                   new BoundedVariable<Double>("Gamma",
                                                               mClearVolumeRendererInterface.getGamma())
                                   {
                                     @Override
                                     public Double setEventHook(Double pOldReference,
                                                                Double pNewReference)
                                     {
                                       Platform.runLater(new Runnable()
                                       {
                                         @Override
                                         public void run()
                                         {
                                           mClearVolumeRendererInterface.setGamma(pNewReference);
                                         }
                                       });

                                       return super.setEventHook(pOldReference,
                                                                 pNewReference);
                                     }
                                   };

    BoundedVariable<Double> lBrightness =
                                        new BoundedVariable<Double>("Brightness",
                                                                    mClearVolumeRendererInterface.getBrightness())
                                        {
                                          @Override
                                          public Double setEventHook(Double pOldReference,
                                                                     Double pNewReference)
                                          {
                                            Platform.runLater(new Runnable()
                                            {
                                              @Override
                                              public void run()
                                              {
                                                mClearVolumeRendererInterface.setBrightness(pNewReference);
                                              }
                                            });

                                            return super.setEventHook(pOldReference,
                                                                      pNewReference);
                                          }
                                        };

    try
    {
      Platform.runLater(new Runnable()
      {
        @Override
        public void run()
        {
          new MinMaxControlDialog("Visualisation options",
                                  lMinGreyValue,
                                  lMaxGreyValue,
                                  lGamma,
                                  lBrightness).show();
        }
      });
    }
    catch (IllegalStateException e)
    {
      e.printStackTrace();
    }
  }

  public static void showDialog(final VideoWindow pVideoWindow)
  {

    BoundedVariable<Double> lMinGreyValue =
                                          new BoundedVariable<Double>("Min",
                                                                      pVideoWindow.getMinIntensity())
                                          {
                                            @Override
                                            public Double setEventHook(Double pOldReference,
                                                                       Double pNewReference)
                                            {
                                              Platform.runLater(new Runnable()
                                              {
                                                @Override
                                                public void run()
                                                {
                                                  pVideoWindow.setMinIntensity(pNewReference);
                                                }
                                              });

                                              return super.setEventHook(pOldReference,
                                                                        pNewReference);
                                            }
                                          };
    BoundedVariable<Double> lMaxGreyValue =
                                          new BoundedVariable<Double>("Max",
                                                                      pVideoWindow.getMaxIntensity())
                                          {
                                            @Override
                                            public Double setEventHook(Double pOldReference,
                                                                       Double pNewReference)
                                            {
                                              Platform.runLater(new Runnable()
                                              {
                                                @Override
                                                public void run()
                                                {
                                                  pVideoWindow.setMaxIntensity(pNewReference);
                                                }
                                              });

                                              return super.setEventHook(pOldReference,
                                                                        pNewReference);
                                            }
                                          };
    BoundedVariable<Double> lGamma =
                                   new BoundedVariable<Double>("Gamma",
                                                               pVideoWindow.getGamma())
                                   {
                                     @Override
                                     public Double setEventHook(Double pOldReference,
                                                                Double pNewReference)
                                     {
                                       Platform.runLater(new Runnable()
                                       {
                                         @Override
                                         public void run()
                                         {
                                           pVideoWindow.setGamma(pNewReference);
                                         }
                                       });

                                       return super.setEventHook(pOldReference,
                                                                 pNewReference);
                                     }
                                   };

    BoundedVariable<Double> lBrightness =
                                        new BoundedVariable<Double>("(not functional)",
                                                                    0.0)
                                        {
                                          @Override
                                          public Double setEventHook(Double pOldReference,
                                                                     Double pNewReference)
                                          {
                                            Platform.runLater(new Runnable()
                                            {
                                              @Override
                                              public void run()
                                              {

                                              }
                                            });

                                            return super.setEventHook(pOldReference,
                                                                      pNewReference);
                                          }
                                        };
    try
    {
      Platform.runLater(new Runnable()
      {
        @Override
        public void run()
        {
          new MinMaxControlDialog("Visualisation options",
                                  lMinGreyValue,
                                  lMaxGreyValue,
                                  lGamma,
                                  lBrightness).show();
        }
      });
    }
    catch (IllegalStateException e)
    {
      e.printStackTrace();
    }
  }
}
