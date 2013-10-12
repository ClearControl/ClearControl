package stackserver.viewer.test;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import recycling.Recycler;
import stack.Stack;
import stackserver.LocalFileStackSource;
import stackserver.viewer.StackServer3DViewer;

import com.jogamp.graph.math.Quaternion;

public class StackServer3DViewerTests
{

	@Test
	public void test() throws IOException, InterruptedException
	{
		final File lRootFolder = new File("/Users/royer/Temp/");

		final File lOutputFolder = new File("/Users/royer/Temp/");

		final Recycler<Stack> lStacksRecycler = new Recycler<Stack>(Stack.class);

		final LocalFileStackSource lLocalFileStackSource = new LocalFileStackSource(lRootFolder,
																																								"Foo");
		lLocalFileStackSource.setStackRecycler(lStacksRecycler);

		final StackServer3DViewer lStackServer3DViewer = new StackServer3DViewer(lLocalFileStackSource);

		lStackServer3DViewer.setScaleZ(1);
		lStackServer3DViewer.setGamma(0.1);

		System.out.println(lStackServer3DViewer.getNumberOfStacks());

		final Quaternion lQuaternion = new Quaternion();
		lQuaternion.normalize();

		final File lMovieFile = new File(lOutputFolder, "output.raw");
		if (lMovieFile.exists())
			lMovieFile.delete();

		for (int lStackIndex = 0; lStackIndex < lStackServer3DViewer.getNumberOfStacks(); lStackIndex++)
		{
			final float lTheta = 0.1f * lStackIndex;
			lQuaternion.setW((float) Math.cos(lTheta / 2));
			lQuaternion.setX((float) Math.sin(lTheta / 2));

			System.out.format("Rendering frame %d !\n", lStackIndex);

			lStackServer3DViewer.setStackIndex(lStackIndex);
			lStackServer3DViewer.setQuaternion(lQuaternion);

			Thread.sleep(200);
			lStackServer3DViewer.renderToFile(lMovieFile);

		}

		while (lStackServer3DViewer.isShowing())
		{
			Thread.sleep(100);
		}

		lStackServer3DViewer.close();

	}
}
