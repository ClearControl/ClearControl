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

		final File lOutputFolder = new File("/Users/royer/Temp/HistonesCropped_NLM_1_2_100.2Drendered");

		final Recycler<Stack> lStacksRecycler = new Recycler<Stack>(Stack.class);

		final LocalFileStackSource lLocalFileStackSource = new LocalFileStackSource(lRootFolder,
																																								"HistonesCropped_NLM_1_2_100");
		lLocalFileStackSource.setStackRecycler(lStacksRecycler);

		final StackServer3DViewer lStackServer3DViewer = new StackServer3DViewer(lLocalFileStackSource);

		System.out.println(lStackServer3DViewer.getNumberOfStacks());

		final Quaternion lQuaternion = new Quaternion((float) Math.sin(0 / 2),
																									0f,
																									0f,
																									(float) Math.cos(0 / 2));
		lQuaternion.normalize();

		for (int lStackIndex = 0; lStackIndex < lStackServer3DViewer.getNumberOfStacks(); lStackIndex++)
		{
			final float lTheta = 0.1f * lStackIndex;
			lQuaternion.setW((float) Math.cos(lTheta / 2));
			lQuaternion.setX((float) Math.sin(lTheta / 2));

			System.out.format("Rendering frame %d !\n", lStackIndex);
			final File l2DImageFile = new File(	lOutputFolder,
																					"frame" + lStackIndex);
			lStackServer3DViewer.setStackIndex(lStackIndex);
			lStackServer3DViewer.setQuaternion(lQuaternion);
			lStackServer3DViewer.renderToFile(l2DImageFile);
			Thread.sleep(100);
		}

		while (lStackServer3DViewer.isShowing())
		{
			Thread.sleep(100);
		}

		lStackServer3DViewer.close();

	}
}
