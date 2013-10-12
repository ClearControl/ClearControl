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
		final File lRootFolder = new File("D:/Loic");

		final String lDataSetName = "HisGFP_11Oct2013_spec1_try1_NLM_2_3_110";

		final Recycler<Stack> lStacksRecycler = new Recycler<Stack>(Stack.class);

		final LocalFileStackSource lLocalFileStackSource = new LocalFileStackSource(lRootFolder,
		                                                                            lDataSetName);
		
		final File lOutputFolder = new File("D:/Loic",lDataSetName);
		
		lLocalFileStackSource.setStackRecycler(lStacksRecycler);

		final StackServer3DViewer lStackServer3DViewer = new StackServer3DViewer(lLocalFileStackSource);

		lStackServer3DViewer.setScaleZ(1.5);
		lStackServer3DViewer.setGamma(0.3);
		lStackServer3DViewer.setMin(0.001);
		lStackServer3DViewer.setMax(0.045);

		System.out.println(lStackServer3DViewer.getNumberOfStacks());

		final Quaternion lQuaternion = new Quaternion();
		lQuaternion.normalize();

		final File lMovieFile = new File(lOutputFolder, "render.raw");
		if (lMovieFile.exists())
			lMovieFile.delete();

		for (int lStackIndex = 0; lStackIndex < lStackServer3DViewer.getNumberOfStacks(); lStackIndex++)
		{
			final float lTheta = 0.01f * lStackIndex;
			lQuaternion.setW((float) Math.cos(lTheta / 2));
			lQuaternion.setY((float) Math.sin(lTheta / 2));

			System.out.format("Rendering frame %d !\n", lStackIndex);

			lStackServer3DViewer.setStackIndex(lStackIndex);
			lStackServer3DViewer.setQuaternion(lQuaternion);

			Thread.sleep(300);
			lStackServer3DViewer.renderToFile(lMovieFile);

		}

		while (lStackServer3DViewer.isShowing())
		{
			Thread.sleep(100);
		}

		lStackServer3DViewer.close();

	}
}
