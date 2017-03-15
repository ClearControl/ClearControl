package clearcontrol.microscope.gui.halcyon.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

public class SandBox
{

  @Test
  public void test() throws ClassNotFoundException,
                     NoSuchMethodException,
                     SecurityException,
                     InstantiationException,
                     IllegalAccessException,
                     IllegalArgumentException,
                     InvocationTargetException
  {
    Class<?> cl = Class.forName("javax.swing.JLabel");
    Constructor<?> cons = cl.getConstructor(String.class);
    Object o = cons.newInstance("JLabel");
  }

}
