package clearcontrol.microscope.lightsheet.acquisition.tables.gui;

import clearcontrol.gui.plots.MultiPlot;
import clearcontrol.gui.plots.PlotTab;
import clearcontrol.microscope.lightsheet.acquisition.tables.InterpolationTables;

public class InterpolationTablesEvolutionVisualizer
{

	private MultiPlot mMultiPlotStateEvolution;

	private long mTimePoint = 0;

	public InterpolationTablesEvolutionVisualizer()
	{
		super();

	}

	public void addState(InterpolationTables pAcquisitionState)
	{
		int lNumberOfControlPlanes = pAcquisitionState.getNumberOfControlPlanes();

		if (mMultiPlotStateEvolution == null)
		{
			mMultiPlotStateEvolution = MultiPlot.getMultiPlot(this.getClass()
																														.getSimpleName() + "State evolution");
			mMultiPlotStateEvolution.clear();
			mMultiPlotStateEvolution.setVisible(true);
		}

		for (int d = 0; d < pAcquisitionState.getNumberOfDevicesDZ(); d++)
		{
			PlotTab lPlot = mMultiPlotStateEvolution.getPlot(String.format(	"DZ D=%d",
																																			d));

			for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
			{
				String lCurveName = String.format("cp=%d", czi);
				lPlot.setLinePlot(lCurveName);
				double lValue = pAcquisitionState.getAtControlPlaneDZ(czi, d);
				if (Double.isFinite(lValue))
					lPlot.addPoint(lCurveName, mTimePoint, lValue);
			}

			lPlot.ensureUpToDate();

		}

		for (int i = 0; i < pAcquisitionState.getNumberOfDevicesIX(); i++)
		{
			PlotTab lPlotIX = mMultiPlotStateEvolution.getPlot(String.format(	"IX LS=%d",
																																				i));

			for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
			{
				String lCurveName = String.format("cp=%d", czi);
				lPlotIX.setLinePlot(lCurveName);
				double lValue = pAcquisitionState.getAtControlPlaneIX(czi, i);
				if (Double.isFinite(lValue))
					lPlotIX.addPoint(lCurveName, mTimePoint, lValue);
			}

			PlotTab lPlotIY = mMultiPlotStateEvolution.getPlot(String.format(	"IY LS=%d",
																																				i));

			for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
			{
				String lCurveName = String.format("cp=%d", czi);
				lPlotIY.setLinePlot(lCurveName);
				double lValue = pAcquisitionState.getAtControlPlaneIY(czi, i);
				if (Double.isFinite(lValue))
					lPlotIY.addPoint(lCurveName, mTimePoint, lValue);
			}

			PlotTab lPlotIZ = mMultiPlotStateEvolution.getPlot(String.format(	"IZ LS=%d",
																																				i));

			for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
			{
				String lCurveName = String.format("cp=%d", czi);
				lPlotIZ.setLinePlot(lCurveName);
				double lValue = pAcquisitionState.getAtControlPlaneIZ(czi, i);
				if (Double.isFinite(lValue))
					lPlotIZ.addPoint(lCurveName, mTimePoint, lValue);
			}

			PlotTab lPlotIA = mMultiPlotStateEvolution.getPlot(String.format(	"IA LS=%d",
																																				i));

			for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
			{
				String lCurveName = String.format("cp=%d", czi);
				lPlotIA.setLinePlot(lCurveName);
				double lValue = pAcquisitionState.getAtControlPlaneIA(czi, i);
				if (Double.isFinite(lValue))
					lPlotIA.addPoint(lCurveName, mTimePoint, lValue);
			}

			PlotTab lPlotIB = mMultiPlotStateEvolution.getPlot(String.format(	"IB LS=%d",
																																				i));

			for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
			{
				String lCurveName = String.format("cp=%d", czi);
				lPlotIB.setLinePlot(lCurveName);
				double lValue = pAcquisitionState.getAtControlPlaneIB(czi, i);
				if (Double.isFinite(lValue))
					lPlotIB.addPoint(lCurveName, mTimePoint, lValue);
			}

			PlotTab lPlotIW = mMultiPlotStateEvolution.getPlot(String.format(	"IW LS=%d",
																																				i));

			for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
			{
				String lCurveName = String.format("cp=%d", czi);
				lPlotIW.setLinePlot(lCurveName);
				double lValue = pAcquisitionState.getAtControlPlaneIW(czi, i);
				if (Double.isFinite(lValue))
					lPlotIW.addPoint(lCurveName, mTimePoint, lValue);
			}

			PlotTab lPlotIH = mMultiPlotStateEvolution.getPlot(String.format(	"IH LS=%d",
																																				i));

			for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
			{
				String lCurveName = String.format("cp=%d", czi);
				lPlotIH.setLinePlot(lCurveName);
				double lValue = pAcquisitionState.getAtControlPlaneIH(czi, i);
				if (Double.isFinite(lValue))
					lPlotIH.addPoint(lCurveName, mTimePoint, lValue);
			}

			PlotTab lPlotIP = mMultiPlotStateEvolution.getPlot(String.format(	"IP LS=%d",
																																				i));

			for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
			{
				String lCurveName = String.format("cp=%d", czi);
				lPlotIP.setLinePlot(lCurveName);
				double lValue = pAcquisitionState.getAtControlPlaneIP(czi, i);
				if (Double.isFinite(lValue))
					lPlotIP.addPoint(lCurveName, mTimePoint, lValue);
			}

			lPlotIX.ensureUpToDate();
			lPlotIY.ensureUpToDate();
			lPlotIZ.ensureUpToDate();
			lPlotIA.ensureUpToDate();
			lPlotIB.ensureUpToDate();
			lPlotIW.ensureUpToDate();
			lPlotIH.ensureUpToDate();
			lPlotIP.ensureUpToDate();

		}

		mTimePoint++;
	}

}
