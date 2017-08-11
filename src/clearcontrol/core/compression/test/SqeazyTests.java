package clearcontrol.core.compression.test;

import org.bridj.Pointer;
import org.junit.Test;
import sqeazy.bindings.SqeazyLibrary;

/**
 * 
 *
 * @author royer
 */
public class SqeazyTests
{

  /**
   * Squeazy tests go here...
   */
  @Test
  public void test()
  {

    Pointer<Integer> lVersion = Pointer.allocateInts(3);
    SqeazyLibrary.SQY_Version_Triple(lVersion);

    System.out.println("Version: " + lVersion.getIntAtIndex(0)
                       + "."
                       + lVersion.getIntAtIndex(1)
                       + "."
                       + lVersion.getIntAtIndex(2));

  }

}
