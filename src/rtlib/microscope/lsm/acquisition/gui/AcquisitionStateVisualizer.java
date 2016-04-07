package rtlib.microscope.lsm.acquisition.gui;

import rtlib.gui.plots.MultiPlot;
import rtlib.gui.plots.PlotTab;
import rtlib.microscope.lsm.acquisition.AcquisitionState;
import rtlib.microscope.lsm.acquisition.interpolation.InterpolationTable;

public class AcquisitionStateVisualizer
{

	private MultiPlot mMultiPlotState;

	public AcquisitionStateVisualizer()
	{
		super();
	}

	public void setState(AcquisitionState pAcquisitionState)
	{

		if (mMultiPlotState == null)
		{
			mMultiPlotState = MultiPlot.getMultiPlot(this.getClass()
																										.getSimpleName() + "State");
			mMultiPlotState.clear();
			mMultiPlotState.setVisible(true);
		}

		plotTable(pAcquisitionState,
							"DZ",
							pAcquisitionState.getDZTable(),
							pAcquisitionState.getNumberOfDevicesDZ());

		plotTable(pAcquisitionState,
							"IX",
							pAcquisitionState.getIXTable(),
							pAcquisitionState.getNumberOfDevicesIX());
		plotTable(pAcquisitionState,
							"IY",
							pAcquisitionState.getIYTable(),
							pAcquisitionState.getNumberOfDevicesIY());
		plotTable(pAcquisitionState,
							"IZ",
							pAcquisitionState.getIZTable(),
							pAcquisitionState.getNumberOfDevicesIZ());

		plotTable(pAcquisitionState,
							"IA",
							pAcquisitionState.getIATable(),
							pAcquisitionState.getNumberOfDevicesIA());
		plotTable(pAcquisitionState,
							"IB",
							pAcquisitionState.getIBTable(),
							pAcquisitionState.getNumberOfDevicesIB());

		plotTable(pAcquisitionState,
							"IW",
							pAcquisitionState.getIWTable(),
							pAcquisitionState.getNumberOfDevicesIW());
		plotTable(pAcquisitionState,
							"IH",
							pAcquisitionState.getIHTable(),
							pAcquisitionState.getNumberOfDevicesIH());

		plotTable(pAcquisitionState,
							"IP",
							pAcquisitionState.getIPTable(),
							pAcquisitionState.getNumberOfDevicesIP());

	}

	public void plotTable(AcquisitionState pAcquisitionState,
												String lName,
												InterpolationTable lTable,
												int lNumberOfDevices)
	{
		for (int d = 0; d < lNumberOfDevices; d++)
		{
			PlotTab lPlot = mMultiPlotState.getPlot(String.format(lName + " index=%d",
																														d));
			lPlot.clearPoints();

			lPlot.setLinePlot("interpolated " + lName);
			double lStepZ = 0.005 * (pAcquisitionState.getMaxZ() - pAcquisitionState.getMinZ());

			for (double z = pAcquisitionState.getMinZ(); z <= pAcquisitionState.getMaxZ(); z += lStepZ)
			{
				double lValue = lTable.getInterpolatedValue(d, z);
				if (Double.isFinite(lValue))
					lPlot.addPoint("interpolated " + lName, z, lValue);
			}

			lPlot.setScatterPlot("control points " + lName);
			int lNumberOfControlPlanes = pAcquisitionState.getNumberOfControlPlanes();

			for (int czi = 0; czi < lNumberOfControlPlanes; czi++)
			{
				double lValue = lTable.getRow(czi).getY(d);
				double z = pAcquisitionState.getZ(czi);
				if (Double.isFinite(lValue))
					lPlot.addPoint("control points " + lName, z, lValue);
			}

			lPlot.ensureUpToDate();

		}
	}

}
