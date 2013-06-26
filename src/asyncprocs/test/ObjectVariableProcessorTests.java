package asyncprocs.test;

import java.io.IOException;

import org.junit.Test;

import thread.EnhancedThread;
import variable.objectv.ObjectVariable;
import asyncprocs.ObjectVariableAsynchronousPooledProcessor;
import asyncprocs.ProcessorInterface;

public class ObjectVariableProcessorTests
{

	@Test
	public void testObjectVariableProcessorTests()
	{

		final ProcessorInterface<String, String> lProcessor = new ProcessorInterface<String, String>()
		{

			@Override
			public void close() throws IOException
			{
				System.out.println("close");
			}

			@Override
			public String process(final String pInput)
			{
				System.out.println("Input: " + pInput);
				return pInput;
			}
		};

		final ObjectVariableAsynchronousPooledProcessor<String, String> lObjectVariableProcessor = new ObjectVariableAsynchronousPooledProcessor<String, String>(	"test",
																																																																															10,
																																																																															2,
																																																																															lProcessor,
																																																																															false);

		lObjectVariableProcessor.open();
		lObjectVariableProcessor.start();

		EnhancedThread.sleep(1000);

		lObjectVariableProcessor.getOutputObjectVariable()
														.syncWith(new ObjectVariable<String>("Notifier")
														{

															@Override
															public void setReference(final String pNewReference)
															{
																System.out.println("Received on the output variable: " + pNewReference);
															}
														});

		lObjectVariableProcessor.getInputObjectVariable()
														.setReference("1");

	}

}
