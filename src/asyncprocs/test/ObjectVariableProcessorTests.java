package asyncprocs.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import utils.concurency.thread.EnhancedThread;
import variable.objectv.ObjectInputVariableInterface;

import asyncprocs.AsynchronousProcessorBase;
import asyncprocs.AsynchronousProcessorInterface;
import asyncprocs.AsynchronousProcessorNull;
import asyncprocs.AsynchronousProcessorPool;
import asyncprocs.ObjectVariableProcessor;
import asyncprocs.ProcessorInterface;

public class ObjectVariableProcessorTests
{

	@Test
	public void testObjectVariableProcessorTests()
	{

		ProcessorInterface<String, String> lProcessor = new ProcessorInterface<String, String>()
		{

			@Override
			public void close() throws IOException
			{
				System.out.println("close");
			}

			@Override
			public String process(String pInput)
			{
				System.out.println("Input: " + pInput);
				return pInput;
			}
		};

		ObjectVariableProcessor<String, String> lObjectVariableProcessor = new ObjectVariableProcessor<String, String>(	"test",
																																																										10,
																																																										2,
																																																										lProcessor,
																																																										false);
		
		lObjectVariableProcessor.open();
		lObjectVariableProcessor.start();
		
		EnhancedThread.sleep(1000);
		
		
		
		lObjectVariableProcessor.getOutputObjectVariable().sendUpdatesTo(new ObjectInputVariableInterface<String>()
		{
			
			@Override
			public void setReference(	Object pObjectEventSource,
																String pNewReference)
			{
				System.out.println("Received on the output variable: "+pNewReference);
			}
		});
		
		lObjectVariableProcessor.getInputObjectVariable().setReference("1");
		
		

	}

}
