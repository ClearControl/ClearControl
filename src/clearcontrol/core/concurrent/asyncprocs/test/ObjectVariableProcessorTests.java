package clearcontrol.core.concurrent.asyncprocs.test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.asyncprocs.ObjectVariableAsynchronousPooledProcessor;
import clearcontrol.core.concurrent.asyncprocs.ProcessorInterface;
import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.variable.Variable;

import org.junit.Test;

public class ObjectVariableProcessorTests
{

  @Test
  public void testObjectVariableProcessorTests()
  {

    final ProcessorInterface<String, String> lProcessor =
                                                        new ProcessorInterface<String, String>()
                                                        {

                                                          @Override
                                                          public void close() throws IOException
                                                          {
                                                            System.out.println("close");
                                                          }

                                                          @Override
                                                          public String process(final String pInput)
                                                          {
                                                            System.out.println("Input: "
                                                                               + pInput);
                                                            return pInput;
                                                          }
                                                        };

    final ObjectVariableAsynchronousPooledProcessor<String, String> lObjectVariableProcessor =
                                                                                             new ObjectVariableAsynchronousPooledProcessor<String, String>("test",
                                                                                                                                                           10,
                                                                                                                                                           2,
                                                                                                                                                           lProcessor,
                                                                                                                                                           false);

    lObjectVariableProcessor.open();
    lObjectVariableProcessor.start();

    ThreadUtils.sleep(1000, TimeUnit.MILLISECONDS);

    lObjectVariableProcessor.getOutputObjectVariable()
                            .syncWith(new Variable<String>("Notifier")
                            {

                              @Override
                              public void set(final String pNewReference)
                              {
                                System.out.println("Received on the output variable: "
                                                   + pNewReference);
                              }
                            });

    lObjectVariableProcessor.getInputObjectVariable().set("1");

  }

}
