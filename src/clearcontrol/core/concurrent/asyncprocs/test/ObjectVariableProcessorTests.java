package clearcontrol.core.concurrent.asyncprocs.test;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.asyncprocs.ObjectVariableAsynchronousProcessorPool;
import clearcontrol.core.concurrent.asyncprocs.ProcessorInterface;
import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.core.variable.Variable;

import org.junit.Test;

/**
 * Object Variable processor tests
 *
 * @author royer
 */
public class ObjectVariableProcessorTests
{

  /**
   * Object variable processor tests
   */
  @Test
  public void testObjectVariableProcessorTests()
  {

    final ProcessorInterface<String, String> lProcessor = (input) -> {
      System.out.println("Input: " + input);
      return input;
    };

    final ObjectVariableAsynchronousProcessorPool<String, String> lObjectVariableProcessor =
                                                                                           new ObjectVariableAsynchronousProcessorPool<String, String>("test",
                                                                                                                                                       10,
                                                                                                                                                       2,
                                                                                                                                                       lProcessor,
                                                                                                                                                       false);

    lObjectVariableProcessor.start();

    ThreadSleep.sleep(1000, TimeUnit.MILLISECONDS);

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
