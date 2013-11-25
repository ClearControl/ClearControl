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
		final File lRootFolder = new File("/Volumes/green-carpet/SPIM/DataSets/Loic");

		final String lDataSetName = "Volumetric  Scattered Light Experiement 2";

		final Recycler<Stack> lStacksRecycler = new Recycler<Stack>(Stack.class);

		final LocalFileStackSource lLocalFileStackSource = new LocalFileStackSource(lRootFolder,
																																								lDataSetName);

		final File lOutputFolder = new File(lRootFolder, lDataSetName);

		lLocalFileStackSource.setStackRecycler(lStacksRecycler);

		final StackServer3DViewer lStackServer3DViewer = new StackServer3DViewer(	lLocalFileStackSource,
																																							512,
																																							512);

		lStackServer3DViewer.setScaleZ(1.5);
		lStackServer3DViewer.setScaleZ(.8);

		lStackServer3DViewer.setGamma(0.4);
		lStackServer3DViewer.setMin(0.0012);
		lStackServer3DViewer.setMax(0.1);

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

			Thread.sleep(200);
			lStackServer3DViewer.renderToFile(lMovieFile);

		}

		/*while (lStackServer3DViewer.isShowing())
		{
			Thread.sleep(100);
		}/**/

		Thread.sleep(2000);
		lStackServer3DViewer.close();

	}
}
