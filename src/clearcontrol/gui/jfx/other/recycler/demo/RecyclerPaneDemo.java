package clearcontrol.gui.jfx.other.recycler.demo;

import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorFeature;
import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.gui.jfx.other.recycler.RecyclerPanel;
import coremem.exceptions.FreedException;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclableFactoryInterface;
import coremem.recycling.RecyclableInterface;
import coremem.recycling.RecyclerInterface;
import coremem.recycling.RecyclerRequestInterface;

/**
 * Recycler panel demo
 *
 * @author royer
 */
public class RecyclerPaneDemo extends Application
                              implements AsynchronousExecutorFeature
{

  private class DemoRequest implements RecyclerRequestInterface
  {
    public String value;

    public DemoRequest(String pString)
    {
      value = pString;
    }
  }

  private class DemoFactory implements
                            RecyclableFactoryInterface<DemoRecyclable, DemoRequest>
  {
    @Override
    public DemoRecyclable create(DemoRequest pParameters)
    {
      return new DemoRecyclable(pParameters.value);
    }
  }

  private class DemoRecyclable implements
                               RecyclableInterface<DemoRecyclable, DemoRequest>
  {
    RecyclerInterface<DemoRecyclable, DemoRequest> mRecycler;
    String mString;
    AtomicBoolean mReleased = new AtomicBoolean(false);

    public DemoRecyclable(String pString)
    {
      mString = pString;
    }

    @Override
    public long getSizeInBytes()
    {
      return (mString == null
              || mString.isEmpty()) ? 0
                                    : mString.length()
                                      * Character.BYTES;
    }

    @Override
    public void free()
    {

    }

    @Override
    public boolean isFree()
    {
      return true;
    }

    @Override
    public void complainIfFreed() throws FreedException
    {
    }

    @Override
    public boolean isCompatible(DemoRequest pParameters)
    {
      return true;
    }

    @Override
    public void setRecycler(RecyclerInterface<DemoRecyclable, DemoRequest> pRecycler)
    {
      mRecycler = pRecycler;
    }

    @Override
    public void recycle(DemoRequest pParameters)
    {
      mString = pParameters.value;
    }

    @Override
    public void setReleased(boolean pIsReleased)
    {
      mReleased.set(pIsReleased);
    }

    @Override
    public boolean isReleased()
    {
      return mReleased.get();
    }

    @Override
    public void release()
    {
      setReleased(true);
      mRecycler.release(this);
    }

  }

  @Override
  public void start(Stage stage)
  {
    Group root = new Group();
    Scene scene = new Scene(root,
                            RecyclerPanel.cPrefWidth,
                            RecyclerPanel.cPrefHeight);
    stage.setScene(scene);
    stage.setTitle("RecyclerPane Demo");
    // scene.setFill(Color.BLACK);

    DemoFactory lFactory = new DemoFactory();

    BasicRecycler<DemoRecyclable, DemoRequest> lRecycler =
                                                         new BasicRecycler<>(lFactory,
                                                                             250);

    RecyclerPanel lInstrumentedRecyclerPane =
                                            new RecyclerPanel(lRecycler);

    root.getChildren().add(lInstrumentedRecyclerPane);

    executeAsynchronously(() -> {

      try
      {
        Stack<DemoRecyclable> lStack = new Stack<>();

        for (int i = 0; i < 500000; i++)
        {
          if ((i / 100) % 2 == 1)
          {
            // System.out.println("RELEASING!");
            if (!lStack.isEmpty() && Math.random() < 0.9)
            {
              DemoRecyclable lRecyclable = lStack.pop();
              lRecyclable.release();
            }
          }
          else
          {
            // /System.out.println("REQUESTING!");
            DemoRequest lRequest = new DemoRequest("req" + i);

            DemoRecyclable lRecyclable = lRecycler.get(true,
                                                       1,
                                                       TimeUnit.SECONDS,
                                                       lRequest);

            if (lRecyclable == null)
            {
              // System.out.println("!!NULL!!");
            }
            else
              lStack.push(lRecyclable);
          }

          ThreadSleep.sleep(10, TimeUnit.MILLISECONDS);
        }

        System.out.println("Done!");
      }
      catch (Throwable e)
      {
        e.printStackTrace();
      }

    });

    RecyclerPanel.openPaneInWindow("test", lRecycler);

    stage.show();
  }

  /**
   * Main
   * 
   * @param args
   *          NA
   */
  public static void main(String[] args)
  {
    launch(args);
  }
}
