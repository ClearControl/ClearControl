package compression.test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;

import test.testimage.TestImage;

import compression.AdaptiveQuantizer;

import frames.Frame;

public class AdaptiveQuantizerTests
{

	@Test
	public void testBasics() throws IOException
	{
		System.out.println("should be -12:"+((byte)(255)));
	}
	
	@Test
	public void test() throws IOException
	{
		AdaptiveQuantizer lAdaptiveQuantizer = new AdaptiveQuantizer();
		
		ByteBuffer lLoadRawImage = TestImage.loadRawImage();
		Frame lFrame16Bit = new Frame(lLoadRawImage,0,549,1080,2);
		
		
		final Frame l8BitImage = lAdaptiveQuantizer.apply(lFrame16Bit);
		
		l8BitImage.writeRaw(new File("test.raw"));
		
		final int[] lHistogram = lAdaptiveQuantizer.getHistogram();
		System.out.println(Arrays.toString(lHistogram));
		for(int i=0; i<lHistogram.length; i++)
		{
			if(lHistogram[i]>0) System.out.format("%d -> %d \n",i,lHistogram[i]);
		}
		
		final float[] lImportance = lAdaptiveQuantizer.getImportance();
		for(int i=0; i<lImportance.length; i++)
		{
			if(lImportance[i]>0) System.out.format("%d\t%g \n",i,lImportance[i]);
		}
		
		final int[] l8BitTo16BitLookupTable = lAdaptiveQuantizer.get8BitTo16BitLookupTable();
		for(int i=0; i<l8BitTo16BitLookupTable.length; i++)
		{
			System.out.format("%d\t%d \n",i,l8BitTo16BitLookupTable[i]);
		}
		
		
		
		
		
		
	}


}
