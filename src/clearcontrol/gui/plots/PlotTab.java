package clearcontrol.gui.plots;

import java.awt.HeadlessException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gnu.trove.list.array.TDoubleArrayList;

import org.math.plot.Plot2DPanel;

public class PlotTab
{
  private static final ExecutorService sExecutor =
                                                 Executors.newSingleThreadExecutor();
  private static final Object mLock = new Object();

  private final HashMap<String, TDoubleArrayList> mX;
  private final HashMap<String, TDoubleArrayList> mY;
  private final HashMap<String, Boolean> mIsLinePlot;
  private final Plot2DPanel mPlot;
  private boolean mUpToDate = false;

  public PlotTab(final String pName) throws HeadlessException
  {
    mPlot = new Plot2DPanel();
    mX = new HashMap<String, TDoubleArrayList>();
    mY = new HashMap<String, TDoubleArrayList>();
    mIsLinePlot = new HashMap<String, Boolean>();
  }

  public void addPoint(final String pVariableName, final double pY)
  {
    addPoint(pVariableName, mX.size(), pY);
  }

  public void addPoint(final String pVariableName,
                       final double pX,
                       final double pY)
  {
    synchronized (mLock)
    {
      try
      {
        TDoubleArrayList lX = mX.get(pVariableName);
        if (lX == null)
        {
          lX = new TDoubleArrayList();
          mX.put(pVariableName, lX);
        }

        lX.add(pX);

        TDoubleArrayList lY = mY.get(pVariableName);

        if (lY == null)
        {
          lY = new TDoubleArrayList();
          mY.put(pVariableName, lY);
        }

        lY.add(pY);

        mUpToDate = false;
      }
      catch (final Throwable e)
      {
        e.printStackTrace();
      }
    }
  }

  public void ensureUpToDate()
  {
    final Runnable lEnsureUpToDateRunnable = new Runnable()
    {
      @Override
      public void run()
      {
        synchronized (mLock)
        {
          try
          {
            if (!mUpToDate)
            {
              try
              {
                mPlot.removeAllPlots();
              }
              catch (final Throwable e)
              {
                System.err.println(e.getLocalizedMessage());
              }

              for (final Entry<String, TDoubleArrayList> lEntry : mY.entrySet())
              {
                final String lVariableName = lEntry.getKey();
                final TDoubleArrayList lX = mX.get(lVariableName);
                final TDoubleArrayList lY = lEntry.getValue();

                if (mIsLinePlot.get(lVariableName) != null
                    && mIsLinePlot.get(lVariableName))
                {
                  mPlot.addLinePlot(lVariableName,
                                    lX.toArray(),
                                    lY.toArray());
                }
                else
                {
                  mPlot.addScatterPlot(lVariableName,
                                       lX.toArray(),
                                       lY.toArray());
                }

              }

              mPlot.removeLegend();
              mPlot.addLegend("EAST");

              mUpToDate = true;
            }
          }
          catch (final Throwable e)
          {
            e.printStackTrace();
            System.err.println(PlotTab.class.getSimpleName() + ": "
                               + e.getLocalizedMessage());
          }
        }
      }
    };

    execute(lEnsureUpToDateRunnable);
  }

  public Plot2DPanel getPlot()
  {
    return mPlot;
  }

  public void clearPoints()
  {
    synchronized (mLock)
    {
      mX.clear();
      for (final Entry<String, TDoubleArrayList> lEntry : mY.entrySet())
      {
        final TDoubleArrayList lY = lEntry.getValue();
        lY.clear();
      }
      mY.clear();
    }
  }

  public void execute(final Runnable pRunnable)
  {
    java.awt.EventQueue.invokeLater(pRunnable);
  }

  public boolean isIsLinePlot(String pVariableName)
  {
    return mIsLinePlot.get(pVariableName);
  }

  public void setLinePlot(String pVariableName)
  {
    mIsLinePlot.put(pVariableName, true);
  }

  public void setScatterPlot(String pVariableName)
  {
    mIsLinePlot.put(pVariableName, false);
  }

}
