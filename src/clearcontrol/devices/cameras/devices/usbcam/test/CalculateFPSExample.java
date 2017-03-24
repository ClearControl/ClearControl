package clearcontrol.devices.cameras.devices.usbcam.test;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

public class CalculateFPSExample
{

  public static void main(String[] args)
  {

    long t1 = 0;
    long t2 = 0;

    int p = 10;
    int r = 50;

    List<Webcam> lWebcams = Webcam.getWebcams();
    for (Webcam lWebcam : lWebcams)
    {
      System.out.println(lWebcam);

      lWebcam.setViewSize(new Dimension(1024, 768));

      WebcamPanel panel = new WebcamPanel(lWebcam);
      panel.setFPS(25);

      JFrame window = new JFrame("Test webcam panel");
      window.add(panel);
      window.setResizable(true);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.pack();
      window.setVisible(true);

      lWebcam.open();
      for (int k = 0; k < p; k++)
      {

        System.out.println(lWebcam.getViewSize());
        lWebcam.getImage();

        t1 = System.currentTimeMillis();
        for (int i = 0; ++i <= r; lWebcam.getImage())
        {
          System.out.print(".");
        }
        System.out.println("");
        t2 = System.currentTimeMillis();

        System.out.println("FPS " + k
                           + ": "
                           + (1000 * r / (t2 - t1 + 1)));

      }
      // lWebcam.close();
    }

  }
}