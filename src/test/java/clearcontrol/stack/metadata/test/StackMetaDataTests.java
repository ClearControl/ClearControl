package clearcontrol.stack.metadata.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import clearcontrol.stack.metadata.MetaDataOrdinals;
import clearcontrol.stack.metadata.StackMetaData;

import org.junit.Test;

/**
 * Stack meta data testss
 *
 * @author royer
 */
public class StackMetaDataTests
{

  /**
   * Basic tests
   */
  @Test
  public void test()
  {
    StackMetaData lStackMetaData = new StackMetaData();

    lStackMetaData.setIndex(14);
    lStackMetaData.setTimeStampInNanoseconds(12345);
    lStackMetaData.setVoxelDimX(0.1);
    lStackMetaData.setVoxelDimY(0.2);
    lStackMetaData.setVoxelDimZ(0.3);

    String lString = lStackMetaData.toString();
    System.out.println(lString);

    StackMetaData lStackMetaData2 = new StackMetaData();

    lStackMetaData2.fromString(lString);

    assertTrue(14L == lStackMetaData2.getIndex());
    assertTrue(12345 == lStackMetaData2.getTimeStampInNanoseconds());
    assertTrue(0.1 == lStackMetaData2.getVoxelDimX());
    assertTrue(0.2 == lStackMetaData2.getVoxelDimY());
    assertTrue(0.3 == lStackMetaData2.getVoxelDimZ());

    assertEquals("Index", MetaDataOrdinals.Index.toString());
  }

}
