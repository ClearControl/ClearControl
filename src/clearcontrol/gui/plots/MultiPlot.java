package clearcontrol.gui.plots;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MultiPlot
{
  public static ImageIcon sIcon;

  static HashMap<String, MultiPlot> sNameToMultiPlotMap =
                                                        new HashMap<String, MultiPlot>();

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
  private final HashMap<String, PlotTab> mNameToPlotMap =
                                                        new HashMap<String, PlotTab>();

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

      final PlotTab lFinalPlotTab = lPlotTab;
      try
      {
        SwingUtilities.invokeAndWait(() -> {

          mTabbedPane.addTab(pName, lFinalPlotTab.getPlot());

          mTabbedPane.setSelectedIndex(mTabbedPane.getTabCount() - 1);
        });
      }
      catch (InvocationTargetException | InterruptedException e)
      {
        e.printStackTrace();
      }
    }

    return lPlotTab;
  }

  public void clear()
  {
    try
    {
      SwingUtilities.invokeAndWait(() -> {
        mTabbedPane.removeAll();
        for (Map.Entry<String, PlotTab> lEntry : mNameToPlotMap.entrySet())
        {
          lEntry.getValue().clearPoints();
          lEntry.getValue().ensureUpToDate();
        }
        mNameToPlotMap.clear();
      });
    }
    catch (InvocationTargetException | InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  public void setVisible(final boolean pIsVisible)
  {
    SwingUtilities.invokeLater(() -> {
      mFrame.setVisible(pIsVisible);
    });
  }

  public boolean isVisible()
  {
    return mFrame.isVisible();
  }

}
