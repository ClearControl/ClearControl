package variable.persistence.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import variable.persistence.DoubleVariableAsFile;
import variable.persistence.ObjectVariableAsFile;

public class VariableAsFileTests
{

	@Test
	public void testDoubleVariableAsFile() throws IOException
	{
		File lTempFile = File.createTempFile(	"VariableAsFileTests",
																					"testDoubleVariableAsFile");
		DoubleVariableAsFile lDoubleVariable = new DoubleVariableAsFile(lTempFile,
																																		1);

		lDoubleVariable.setValue(2);
		final double lValue = lDoubleVariable.getValue();

		assertEquals(2, lValue, 0.1);

		DoubleVariableAsFile lDoubleVariable2 = new DoubleVariableAsFile(	lTempFile,
																																			1);

		final double lValue2 = lDoubleVariable2.getValue();
		assertEquals(lValue, lValue2, 0.1);
	}

	@Test
	public void testObjectVariableAsFile() throws IOException, InterruptedException
	{
		File lTempFile = File.createTempFile(	"VariableAsFileTests",
																					"testObjectVariableAsFile");
		ObjectVariableAsFile<String> lObjectVariable = new ObjectVariableAsFile<String>(lTempFile,
																																										"1");

		lObjectVariable.setReference("2");
		Thread.sleep(100);
		
		final String lValue = lObjectVariable.getReference();

		assertEquals("2", lValue);

		ObjectVariableAsFile<String> lObjectVariable2 = new ObjectVariableAsFile<String>(	lTempFile,
																																											"1");

		final String lValue2 = lObjectVariable2.getReference();
		assertEquals(lValue, lValue2);
	}

}
