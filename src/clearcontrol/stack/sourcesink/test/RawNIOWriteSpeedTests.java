package clearcontrol.stack.sourcesink.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.junit.Test;

/**
 * Raw NIO write speed tests
 *
 * @author royer
 */
public class RawNIOWriteSpeedTests
{

  /**
   * Test NIO write speed
   * 
   * @throws IOException
   *           NA
   */
  @Test
  public void test() throws IOException
  {
    File file = File.createTempFile("RawNIOWriteSpeedTests", "file");
    file.deleteOnExit();

    System.out.println(file);

    ByteBuffer buf = ByteBuffer.allocateDirect(64_000_000);
    for (int i = 0; i < buf.capacity(); i++)
      buf.put((byte) i);

    long lSize = 0;

    long lStart = System.nanoTime();
    FileOutputStream lFileOutputStream = new FileOutputStream(file);
    FileChannel lFileChannel = lFileOutputStream.getChannel();
    for (int i = 0; i < 50; i++)
    {
      buf.clear();
      lFileChannel.write(buf);
      lSize += buf.capacity();
    }
    lFileOutputStream.close();
    long lStop = System.nanoTime();

    double lElapsedTimeInSeconds = (lStop - lStart) * 1e-9;

    System.out.println("Time taken: " + lElapsedTimeInSeconds + " s");

    double lSpeed = (lSize * 1e-6) / lElapsedTimeInSeconds;

    System.out.format("speed: %g \n", lSpeed);

  }

}
