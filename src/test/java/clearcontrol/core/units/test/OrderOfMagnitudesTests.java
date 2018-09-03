package clearcontrol.core.units.test;

import static clearcontrol.core.units.OrderOfMagnitude.Micro;
import static clearcontrol.core.units.OrderOfMagnitude.Milli;
import static clearcontrol.core.units.OrderOfMagnitude.Unit;
import static org.junit.Assert.assertEquals;

import clearcontrol.core.units.OrderOfMagnitude;

import org.junit.Test;

/**
 * Order of agnitude tests
 *
 * @author royer
 */
public class OrderOfMagnitudesTests
{

  /**
   * Tests shortcut methods
   */
  @Test
  public void testShortcutMethods()
  {
    assertEquals(1000, OrderOfMagnitude.milli2micro(1), 0.1);
    assertEquals(0.001, OrderOfMagnitude.micro2milli(1), 0.1);
    assertEquals(1e6, OrderOfMagnitude.unit2micro(1), 0.1);
    assertEquals(1e-6, OrderOfMagnitude.micro2unit(1), 1e-7);
  }

  /**
   * Tests convert from
   */
  @Test
  public void testConvertFrom()
  {
    assertEquals(1000, Micro.convertFrom(1, Milli), 0.1);
    assertEquals(0.001, Milli.convertFrom(1, Micro), 0.1);
    assertEquals(1e6, Micro.convertFrom(1, Unit), 0.1);
    assertEquals(1e-6, Unit.convertFrom(1, Micro), 1e-7);
  }

}
