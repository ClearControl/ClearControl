package clearcontrol.core.device.queue.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import clearcontrol.core.device.queue.VariableQueueBase;
import clearcontrol.core.variable.Variable;

import org.junit.Test;

/**
 * Variable state queue tests
 *
 * @author royer
 */
public class VariableStateQueuesTests
{

  /**
   * Normal tests
   */
  @Test
  public void testNormal()
  {
    VariableQueueBase lVariableStateQueues = new VariableQueueBase();

    Variable<Double> lOneVariable =
                                  new Variable<Double>("OneVariable");

    lVariableStateQueues.registerVariable(lOneVariable);

    lVariableStateQueues.clearQueue();
    for (int i = 0; i < 100; i++)
    {
      lOneVariable.set(i * 1.23);
      lVariableStateQueues.addCurrentStateToQueue();
    }
    lVariableStateQueues.finalizeQueue();

    ArrayList<Double> lVariableQueue =
                                     lVariableStateQueues.getVariableQueue(lOneVariable);
    for (int i = 0; i < 100; i++)
    {
      assertEquals(i * 1.23, lVariableQueue.get(i), 0.001);
    }

    lVariableStateQueues.clearQueue();

    assertEquals(0, lVariableStateQueues.getQueueLength());

  }

}
