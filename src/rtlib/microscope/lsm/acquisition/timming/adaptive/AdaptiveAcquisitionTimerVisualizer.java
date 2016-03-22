package rtlib.microscope.lsm.acquisition.timming.adaptive;

import ij.ImageJ;
import ij.ImagePlus;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.gui.plots.MultiPlot;
import rtlib.gui.plots.PlotTab;
import rtlib.stack.StackInterface;

public class AdaptiveAcquisitionTimerVisualizer
{

	private MultiPlot mMultiPlotState;

	
	public AdaptiveAcquisitionTimerVisualizer()
	{
		super();
		new ImageJ();
	}

	public void clear()
	{
		if (mMultiPlotState != null)
			mMultiPlotState.clear();

		if (mMultiPlotState == null)
		{
			mMultiPlotState = MultiPlot.getMultiPlot(this.getClass()
															.getSimpleName() + "State");
			mMultiPlotState.setVisible(true);
		}
	}

	public void append(int pPosition, double pMetric)
	{
		PlotTab lPlot = mMultiPlotState.getPlot("AdaptiveAcquisitionTimerVisualizer");
		lPlot.setLinePlot("metric");
		if (Double.isFinite(pMetric))
			lPlot.addPoint("metric", pPosition, pMetric);
		lPlot.ensureUpToDate();
	}
	
	public void visualizeStack(StackInterface<UnsignedShortType, ShortOffHeapAccess> pStack)
	{
		ImagePlus lShow = ImageJFunctions.show(pStack.getImage().copy());
		lShow.setDisplayRange(0, 1000);
	}

}
