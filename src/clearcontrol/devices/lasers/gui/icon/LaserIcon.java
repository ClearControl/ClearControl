package clearcontrol.devices.lasers.gui.icon;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG
 * (http://mpi-cbg.de) February 2018
 */
public class LaserIcon extends CustomGridPane
{
  public LaserIcon(double pWidth, double pHeight)
  {
    ImageView lImageView =
                         new ImageView(new Image(LaserIcon.class.getResourceAsStream("/clearcontrol/devices/lasers/gui/icon/lasersymbol.png")));

    lImageView.setFitWidth(pWidth);
    lImageView.setFitHeight(pHeight);

    add(lImageView, 0, 0);
    setMinHeight(pHeight);
    setMaxHeight(pHeight);
    setMinWidth(pWidth);
    setMaxWidth(pWidth);
  }
}
