package clearcontrol.gui.recycler.demo;

import java.util.Stack;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.gui.recycler.RecyclerPane;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclableFactoryInterface;
import coremem.recycling.RecyclableInterface;
import coremem.recycling.RecyclerInterface;
import coremem.recycling.RecyclerRequestInterface;
import coremem.rgc.FreedException;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RecyclerPaneDemo extends Application	implements
																															AsynchronousExecutorServiceAccess
{

	private class DemoRequest implements RecyclerRequestInterface
	{
		public DemoRequest(String pString)
		{
			value = pString;
		}

		public String value;
	}

	private class DemoFactory	implements
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

		public DemoRecyclable(String pString)
		{
			mString = pString;
		}

		@Override
		public long getSizeInBytes()
		{
			return mString.length() * Character.BYTES;
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
			mString = null;
		}

		@Override
		public boolean isReleased()
		{
			return mString == null;
		}

		@Override
		public void release()
		{
			mString = null;
			mRecycler.release(this);
		}

	}

	@Override
	public void start(Stage stage)
	{
		Group root = new Group();
		Scene scene = new Scene(root, 600, 100);
		stage.setScene(scene);
		stage.setTitle("RecyclerPane Demo");
		// scene.setFill(Color.BLACK);

		DemoFactory lFactory = new DemoFactory();

		BasicRecycler<DemoRecyclable, DemoRequest> lRecycler = new BasicRecycler<>(	lFactory,
																																															250);

		RecyclerPane lInstrumentedRecyclerPane = new RecyclerPane(lRecycler);

		root.getChildren().add(lInstrumentedRecyclerPane);

		executeAsynchronously(() -> {

			try
			{
				Stack<DemoRecyclable> lStack = new Stack<>();

				for (int i = 0; i < 500000; i++)
				{
					if ((i / 100) % 2 == 1)
					{
						//System.out.println("RELEASING!");
						if (!lStack.isEmpty() && Math.random()<0.9)
						{
							DemoRecyclable lRecyclable = lStack.pop();
							lRecyclable.release();
						}
					}
					else
					{
						///System.out.println("REQUESTING!");
						DemoRequest lRequest = new DemoRequest("" + i);

						DemoRecyclable lRecyclable = lRecycler.request(	true,
																														1,
																														TimeUnit.SECONDS,
																														lRequest);

						if (lRecyclable == null)
						{
							//System.out.println("!!NULL!!");
						}
						else
							lStack.push(lRecyclable);
					}

					ThreadUtils.sleep(10, TimeUnit.MILLISECONDS);
				}
				
				System.out.println("Done!");
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}

		});
		
		RecyclerPane.openPaneInWindow("test",lRecycler);

		stage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
