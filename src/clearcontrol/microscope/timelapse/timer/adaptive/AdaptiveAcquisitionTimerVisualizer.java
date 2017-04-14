package clearcontrol.microscope.timelapse.timer.adaptive;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import clearcontrol.gui.plots.MultiPlot;
import clearcontrol.gui.plots.PlotTab;
import clearcontrol.stack.StackInterface;
import ij.ImageJ;
import ij.ImagePlus;

/**
 * Adaptive acquiistion timer visualizer
 *
 * @author royer
 */
public class AdaptiveAcquisitionTimerVisualizer
{

  private MultiPlot mMultiPlotState;

  /**
   * Instanciates an adaptive acquisition timer visualizer
   */
  public AdaptiveAcquisitionTimerVisualizer()
  {
    super();
    new ImageJ();
  }

  /**
   * Clear
   */
  public void clear()
  {
    if (mMultiPlotState != null)
      mMultiPlotState.clear();

    if (mMultiPlotState == null)
    {
      mMultiPlotState = MultiPlot.getMultiPlot(
                                               this.getClass()
                                                   .getSimpleName()
                                               + "State");
      mMultiPlotState.setVisible(true);
    }
  }

  /**
   * Append a metric value at the given position.
   * 
   * @param pPosition
   *          position
   * @param pMetric
   *          metric
   */
  public void append(int pPosition, double pMetric)
  {
    PlotTab lPlot =
                  mMultiPlotState.getPlot("AdaptiveAcquisitionTimerVisualizer");
    lPlot.setLinePlot("metric");
    if (Double.isFinite(pMetric))
      lPlot.addPoint("metric", pPosition, pMetric);
    lPlot.ensureUpToDate();
  }

  /**
   * Visualize stack
   * 
   * @param pStack
   *          stack
   */
  public void visualizeStack(StackInterface pStack)
  {
    @SuppressWarnings("unchecked")
    ImagePlus lShow =
                    ImageJFunctions.show(((OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) pStack.getImage()).copy());
    lShow.setDisplayRange(0, 1000);
  }

}
