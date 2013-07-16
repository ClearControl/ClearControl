package gui.plots;

import gnu.trove.list.array.TDoubleArrayList;

import java.awt.HeadlessException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.math.plot.Plot2DPanel;

public class PlotTab {
	private static final ExecutorService sExecutor = Executors
			.newSingleThreadExecutor();
	private static final Object mLock = new Object();

	private final TDoubleArrayList mX = new TDoubleArrayList();
	private final HashMap<String, TDoubleArrayList> mCorrectionVariables;
	private final Plot2DPanel mPlot;
	private boolean mUpToDate = false;

	private boolean mIsLinePlot = false;

	public PlotTab(final String pName) throws HeadlessException {
		mPlot = new Plot2DPanel();
		mCorrectionVariables = new HashMap<String, TDoubleArrayList>();
	}

	public void addPoint(String pVariableName, final double pY) {
		addPoint(pVariableName, mX.size(), pY);
	}

	public void addPoint(String pVariableName, double pX, double pY) {
		synchronized (mLock) {
			try {
				TDoubleArrayList lY = mCorrectionVariables.get(pVariableName);

				if (lY == null) {
					lY = new TDoubleArrayList();
					mCorrectionVariables.put(pVariableName, lY);
				}

				lY.add(pY);
				if (mX.size() < lY.size())
					mX.add(pX);

				mUpToDate = false;
			} catch (final Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public void ensureUpToDate() {
		Runnable lEnsureUpToDateRunnable = new Runnable() {
			@Override
			public void run() {
				synchronized (mLock) {
					try {
						if (!mUpToDate) {
							mPlot.removeAllPlots();

							for (Entry<String, TDoubleArrayList> lEntry : mCorrectionVariables
									.entrySet()) {
								final String lCorrectionVariableName = lEntry
										.getKey();
								final TDoubleArrayList lY = lEntry.getValue();

								if (!isDataComplete(lY))
									continue;

								if (mIsLinePlot && mX.size() >= 3)

									mPlot.addLinePlot(lCorrectionVariableName,
											mX.toArray(), lY.toArray());
								else
									mPlot.addScatterPlot(
											lCorrectionVariableName,
											mX.toArray(), lY.toArray());

							}

							mPlot.removeLegend();
							mPlot.addLegend("EAST");

							mUpToDate = true;
						}
					} catch (final Throwable e) {
						System.err.println(PlotTab.class.getSimpleName() + ": "
								+ e.getLocalizedMessage());
					}
				}
			}
		};

		execute(lEnsureUpToDateRunnable);
	}

	protected boolean isDataComplete() {

		for (Entry<String, TDoubleArrayList> lEntry : mCorrectionVariables
				.entrySet()) {

			final TDoubleArrayList lY = lEntry.getValue();
			final boolean lIsDataComplete = isDataComplete(lY);
			if (lIsDataComplete)
				return false;
		}

		return true;
	}

	protected boolean isDataComplete(final TDoubleArrayList pY) {

		return pY.size() == mX.size();
	}

	public Plot2DPanel getPlot() {
		return mPlot;
	}

	public void clearPoints() {
		synchronized (mLock) {
			mX.clear();
			for (Entry<String, TDoubleArrayList> lEntry : mCorrectionVariables
					.entrySet()) {
				final TDoubleArrayList lY = lEntry.getValue();
				lY.clear();
			}
			mCorrectionVariables.clear();
		}
	}

	public void execute(Runnable pRunnable) {
		java.awt.EventQueue.invokeLater(pRunnable);
	}

	public boolean isIsLinePlot() {
		return mIsLinePlot;
	}

	public void setLinePlot() {
		mIsLinePlot = true;
	}

	public void setScatterPlot() {
		mIsLinePlot = false;
	}

}
