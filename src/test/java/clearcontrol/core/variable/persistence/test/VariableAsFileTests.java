package clearcontrol.core.variable.persistence.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.persistence.DoubleVariableAsFile;
import clearcontrol.core.variable.persistence.VariableAsFile;
import clearcontrol.core.variable.persistence.VariableBundleAsFile;

import org.junit.Test;

/**
 *
 *
 * @author royer
 */
public class VariableAsFileTests
{

  /**
   * 
   * @throws IOException
   *           N/A
   * @throws InterruptedException
   *           N/A
   */
  @Test
  public void testDoubleVariableAsFile() throws IOException,
                                         InterruptedException
  {
    final File lTempFile =
                         File.createTempFile("VariableAsFileTests",
                                             "testDoubleVariableAsFile");
    final DoubleVariableAsFile lDoubleVariable1 =
                                                new DoubleVariableAsFile(lTempFile,
                                                                         "x",
                                                                         1);

    lDoubleVariable1.set(2.0);
    Thread.sleep(100);
    final double lValue = lDoubleVariable1.get();

    assertEquals(2, lValue, 0.1);

    final DoubleVariableAsFile lDoubleVariable2 =
                                                new DoubleVariableAsFile(lTempFile,
                                                                         "x",
                                                                         1);

    final double lValue2 = lDoubleVariable2.get();
    assertEquals(lValue, lValue2, 0.1);

    lDoubleVariable1.close();
    lDoubleVariable2.close();

  }

  /**
   * @throws IOException
   *           N/A
   * @throws InterruptedException
   *           N/A
   */
  @Test
  public void testObjectVariableAsFile() throws IOException,
                                         InterruptedException
  {
    final File lTempFile =
                         File.createTempFile("VariableAsFileTests",
                                             "testObjectVariableAsFile");
    final VariableAsFile<String> lObjectVariable1 =
                                                  new VariableAsFile<String>("x",
                                                                             lTempFile,
                                                                             "1");

    lObjectVariable1.set("2");
    Thread.sleep(100);

    final String lValue = lObjectVariable1.get();

    assertEquals("2", lValue);

    final VariableAsFile<String> lObjectVariable2 =
                                                  new VariableAsFile<String>("y",
                                                                             lTempFile,
                                                                             "1");

    final String lValue2 = lObjectVariable2.get();
    assertEquals(lValue, lValue2);

    lObjectVariable1.set("3");

    final String lValue3 = lObjectVariable2.get();
    assertEquals("3", lValue3);

    lObjectVariable1.close();
    lObjectVariable2.close();
  }

  /**
   * @throws IOException
   *           N/A
   * @throws InterruptedException
   *           N/A
   */
  @Test
  public void testVariableBundleAsFile() throws IOException,
                                         InterruptedException
  {
    final File lTempFile =
                         File.createTempFile("VariableAsFileTests",
                                             "testVariableBundleAsFile");
    System.out.println(lTempFile);

    final Variable<Double> x1 = new Variable<Double>("x", 1.0);
    final Variable<String> y1 = new Variable<String>("y", "1");

    final VariableBundleAsFile lVariableBundleAsFile1 =
                                                      new VariableBundleAsFile("bundle",
                                                                               lTempFile);

    lVariableBundleAsFile1.addVariable("path1.bla", x1);
    lVariableBundleAsFile1.addVariable("path2.blu", y1);

    x1.set(2.0);
    y1.set("3");
    // Thread.sleep(1000);

    lVariableBundleAsFile1.close();

    // Thread.sleep(10000000);

    final Variable<Double> x2 = new Variable<Double>("x", 1.0);
    final Variable<String> y2 = new Variable<String>("y", "1");

    final VariableBundleAsFile lVariableBundleAsFile2 =
                                                      new VariableBundleAsFile("bundle",
                                                                               lTempFile);

    lVariableBundleAsFile2.addVariable("path1.bla", x2);
    lVariableBundleAsFile2.addVariable("path2.blu", y2);

    lVariableBundleAsFile2.read();

    assertEquals(x1.get(), x2.get(), 0.01);
    System.out.println("done");
    assertEquals(y1.get(), y2.get());

    lVariableBundleAsFile2.close();

  }
}
