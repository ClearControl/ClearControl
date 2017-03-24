package clearcontrol.core.variable.queue.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.queue.InvalidQueueException;
import clearcontrol.core.variable.queue.QueueingMode;
import clearcontrol.core.variable.queue.VariableStateQueues;

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
    VariableStateQueues lVariableStateQueues =
                                             new VariableStateQueues();

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

  /**
   * Normal tests
   */
  @Test
  public void testConstant()
  {
    VariableStateQueues lVariableStateQueues =
                                             new VariableStateQueues();

    Variable<Double> lOneVariable =
                                  new Variable<Double>("OneVariable");

    lVariableStateQueues.registerVariable(QueueingMode.Constant,
                                          lOneVariable);

    lVariableStateQueues.clearQueue();

    lOneVariable.set((double) 0);
    lVariableStateQueues.addCurrentStateToQueue();

    try
    {
      lOneVariable.set((double) 1);
      lVariableStateQueues.addCurrentStateToQueue();
      fail();
    }
    catch (InvalidQueueException e)
    {
      assertTrue(true);
    }

    for (int i = 0; i < 100 - 1; i++)
    {
      lOneVariable.set((double) 0);
      lVariableStateQueues.addCurrentStateToQueue();
    }

    lVariableStateQueues.finalizeQueue();

    ArrayList<Double> lVariableQueue =
                                     lVariableStateQueues.getVariableQueue(lOneVariable);
    for (int i = 0; i < 100; i++)
    {
      assertEquals(0, lVariableQueue.get(i), 0.001);
    }

    lVariableStateQueues.clearQueue();

    assertEquals(0, lVariableStateQueues.getQueueLength());

  }

}
