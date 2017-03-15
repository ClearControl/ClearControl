package clearcontrol.simulation.loaders;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by dibrov on 14/02/17.
 */
public class SampleSpaceSaveAndLoad
{
  public static void saveBlankSampleSpaceOnDisk(int pDimX,
                                                int pDimY,
                                                int pDimZ,
                                                String pPath)
  {
    System.out.println("Saving a sample space with dimensions: "
                       + pDimX + "x" + pDimY + "x" + pDimZ + "...");
    byte[] arr = new byte[pDimX * pDimY * pDimZ];

    try (FileOutputStream f = new FileOutputStream(pPath))
    {

      long t1 = System.nanoTime();
      f.write(arr);
      long t2 = System.nanoTime();

      System.out.println("--- Buffering: no. SampleSpace saved in: "
                         + ((t2 - t1) / 1000000.) + " ms");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      System.out.println("Success: saved to " + pPath);
      System.out.println();
    }
  }

  public static void saveBufferedBlankSampleSpaceOnDisk(int pDimX,
                                                        int pDimY,
                                                        int pDimZ,
                                                        String pPath)
  {
    System.out.println("Saving a sample space with dimensions: "
                       + pDimX + "x" + pDimY + "x" + pDimZ + "...");
    try (FileOutputStream f = new FileOutputStream(pPath);
        BufferedOutputStream bos = new BufferedOutputStream(f))
    {
      byte[] arr = new byte[pDimX * pDimY * pDimZ];
      long t1 = System.nanoTime();
      bos.write(arr, 0, pDimX * pDimY * pDimZ);
      long t2 = System.nanoTime();

      System.out.println("Buffering: yes. SampleSpace saved in: "
                         + ((t2 - t1) / 1000000.) + " ms");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      System.out.println("Success: saved to " + pPath);
      System.out.println();
    }
  }

  public static byte[] loadSampleSpaceFromDisk(String pPath,
                                               int pDimX,
                                               int pDimY,
                                               int pDimZ)
  {
    System.out.println("Loading a SampleSpace from file " + pPath);
    byte[] arr = new byte[pDimX * pDimY * pDimZ];
    try (FileInputStream fis = new FileInputStream(pPath);
        BufferedInputStream bis = new BufferedInputStream(fis))
    {
      long t1 = System.nanoTime();
      bis.read(arr);
      long t2 = System.nanoTime();
      System.out.println("---Buffering: yes. Loaded file " + pPath
                         + " in: "
                         + ((t2 - t1) / 1000000.)
                         + " ms");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
    finally
    {
      System.out.println("Success.");
      System.out.println();
    }
    return arr;
  }

  public static short[] loadUnsignedShortSampleSpaceFromDisk(String pPath,
                                                             int pDimX,
                                                             int pDimY,
                                                             int pDimZ)
  {
    System.out.println("Loading a SampleSpace from file " + pPath);
    byte[] arr = new byte[2 * pDimX * pDimY * pDimZ];
    short[] arrInt = new short[pDimX * pDimY * pDimZ];
    int mask1 = 0B1111111100000000;
    int mask2 = 0B0000000011111111;

    long h = 0;
    try (FileInputStream fis = new FileInputStream(pPath);
        BufferedInputStream bis = new BufferedInputStream(fis))
    {
      long t1 = System.nanoTime();
      bis.read(arr);

      for (int i = 0; i < pDimX * pDimY * pDimZ; i++)
      {
        int b1 = arr[2 * i];
        int b2 = arr[2 * i + 1];
        // System.out.println("bytes b1 and b2: " + b1 + " " + b2);
        // System.out.println("bytes b1 and b2: " + ((b1<<8)&mask1) + " " +
        // (b2&mask2));
        // System.out.println("bytes b1 and b2 sum: " + (((b1<<8)&mask1) +
        // (b2&mask2)));
        arrInt[i] = (short) (((b1 << 8) & mask1) + (b2 & mask2));
        // System.out.println("just loaded: " + h);
      }
      long t2 = System.nanoTime();
      System.out.println("---Buffering: yes. Loaded file " + pPath
                         + " in: "
                         + ((t2 - t1) / 1000000.)
                         + " ms");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
    finally
    {
      System.out.println("Success.");
      System.out.println();
    }
    return arrInt;
  }

  public static void main(String[] args)
  {

    int lDimX = 1000;
    int lDimY = 1000;
    int lDimZ = 100;

    String lPath = "./config_files/SampleSpace1000x1000x100.raw";
    saveBlankSampleSpaceOnDisk(lDimX, lDimY, lDimZ, lPath);

    lDimX = 1000;
    lDimY = 1000;
    lDimZ = 1000;

    lPath = "./config_files/SampleSpace1000x1000x1000.raw";
    saveBlankSampleSpaceOnDisk(lDimX, lDimY, lDimZ, lPath);

    lDimX = 4000;
    lDimY = 2000;
    lDimZ = 100;

    lPath = "./config_files/SampleSpace4000x2000x100.raw";
    saveBlankSampleSpaceOnDisk(lDimX, lDimY, lDimZ, lPath);

  }
}
