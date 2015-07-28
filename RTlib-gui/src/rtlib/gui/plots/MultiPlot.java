package rtlib.gui.plots;

import java.awt.BorderLayout;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

public class MultiPlot
{
	public static ImageIcon sIcon;

	static HashMap<String, MultiPlot> sNameToMultiPlotMap = new HashMap<String, MultiPlot>();

	public static MultiPlot getMultiPlot(final String pName)
	{
		MultiPlot lMultiPlot = sNameToMultiPlotMap.get(pName);

		if (lMultiPlot == null)
		{
			lMultiPlot = new MultiPlot(pName);
			sNameToMultiPlotMap.put(pName, lMultiPlot);
		}

		return lMultiPlot;
	}

	private final String mName;
	private final JFrame mFrame;
	private final JTabbedPane mTabbedPane;
	private final HashMap<String, PlotTab> mNameToPlotMap = new HashMap<String, PlotTab>();

	public MultiPlot(final String pName)
	{
		mName = pName;
		mFrame = new JFrame(pName);
		mFrame.setSize(512, 320);
		mFrame.getContentPane().setLayout(new BorderLayout(0, 0));
		if (sIcon != null)
		{
			mFrame.setIconImage(sIcon.getImage());
		}

		mTabbedPane = new JTabbedPane(SwingConstants.TOP);
		mFrame.getContentPane().add(mTabbedPane, BorderLayout.CENTER);
		mFrame.setVisible(true);
	}

	public PlotTab getPlot(final String pName)
	{
		PlotTab lPlotTab = mNameToPlotMap.get(pName);

		if (lPlotTab == null)
		{
			lPlotTab = new PlotTab(pName);
			mNameToPlotMap.put(pName, lPlotTab);
			mTabbedPane.addTab(pName, lPlotTab.getPlot());
		}

		return lPlotTab;
	}

	public void clear()
	{
		mTabbedPane.removeAll();
		mNameToPlotMap.clear();
	}

	public void setVisible(final boolean pIsVisible)
	{
		mFrame.setVisible(pIsVisible);
	}

	public boolean isVisible()
	{
		return mFrame.isVisible();
	}

}
