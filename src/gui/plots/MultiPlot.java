package gui.plots;

import java.awt.BorderLayout;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MultiPlot
{
	public static ImageIcon sIcon;

	static HashMap<String, MultiPlot> sNameToMultiPlotMap = new HashMap<String, MultiPlot>();

	public static MultiPlot getMultiPlot(String pName)
	{
		MultiPlot lMultiPlot = sNameToMultiPlotMap.get(pName);

		if (lMultiPlot == null)
		{
			lMultiPlot = new MultiPlot(pName);
			sNameToMultiPlotMap.put(pName, lMultiPlot);
		}

		return lMultiPlot;
	}

	private String mName;
	private JFrame mFrame;
	private JTabbedPane mTabbedPane;
	private HashMap<String, PlotTab> mNameToPlotMap = new HashMap<String, PlotTab>();

	public MultiPlot(String pName)
	{
		mName = pName;
		mFrame = new JFrame(pName);
		mFrame.setSize(512, 320);
		mFrame.getContentPane().setLayout(new BorderLayout(0, 0));
		mFrame.setIconImage(sIcon.getImage());

		mTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		mFrame.getContentPane().add(mTabbedPane, BorderLayout.CENTER);
		mFrame.setVisible(true);
	}

	public PlotTab getPlot(String pName)
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

}
