package clearcontrol.core.cpu.test;

import clearcontrol.core.cpu.LoadAverages;
import clearcontrol.core.cpu.LoadAverages.LoadAveragesResult;

import org.junit.Test;

/**
 *
 *
 * @author royer
 */
public class LoadAverageTests
{

  /**
   * Tests load averages
   */
  @Test
  public void test()
  {
    for (int i = 0; i < 10; i++)
    {
      LoadAveragesResult lLoadAverages =
                                       LoadAverages.getLoadAverages();
      System.out.println(lLoadAverages);
    }

  }

}
