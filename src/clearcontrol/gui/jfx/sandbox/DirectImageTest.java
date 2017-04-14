package clearcontrol.gui.jfx.sandbox;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DirectImageTest extends Application
{

  @Override
  public void start(Stage primaryStage) throws NoSuchFieldException,
                                        SecurityException,
                                        IllegalArgumentException,
                                        IllegalAccessException,
                                        NoSuchMethodException,
                                        InvocationTargetException
  {

    int width = 1000;
    int height = 1000;
    int bpp = 4;

    ByteBuffer direct =
                      ByteBuffer.allocateDirect(width * height * bpp);

    for (int i = 0; i < width * height; i++)
    {
      direct.put((byte) 255);
      direct.put((byte) i);
      direct.put((byte) 0);
      direct.put((byte) i);
    }
    direct.rewind();

    WritableImage writableImg = new WritableImage(width, height);

    writeToImageDirectly(direct, writableImg);

    // Display image on screen
    StackPane root = new StackPane();
    ImageView imageView = new ImageView();
    imageView.setImage(writableImg);
    root.getChildren().add(imageView);
    Scene scene = new Scene(root, 300, 250);
    primaryStage.setTitle("Image Read Test");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  protected void writeToImageDirectly(ByteBuffer direct,
                                      WritableImage writableImg) throws NoSuchMethodException,
                                                                 IllegalAccessException,
                                                                 InvocationTargetException,
                                                                 NoSuchFieldException
  {
    // Get the platform image
    Method getWritablePlatformImage =
                                    javafx.scene.image.Image.class.getDeclaredMethod("getWritablePlatformImage");
    getWritablePlatformImage.setAccessible(true);
    com.sun.prism.Image prismImg =
                                 (com.sun.prism.Image) getWritablePlatformImage.invoke(writableImg);

    // Replace the buffer
    Field pixelBuffer =
                      com.sun.prism.Image.class.getDeclaredField("pixelBuffer");
    pixelBuffer.setAccessible(true);
    ByteBuffer lByteBuffer = (ByteBuffer) pixelBuffer.get(prismImg);

    lByteBuffer.rewind();
    direct.rewind();
    lByteBuffer.put(direct);
    lByteBuffer.rewind();

    // Invalidate the platform image
    Field serial =
                 com.sun.prism.Image.class.getDeclaredField("serial");
    serial.setAccessible(true);
    Array.setInt(serial.get(prismImg),
                 0,
                 Array.getInt(serial.get(prismImg), 0) + 1);

    // Invalidate the WritableImage
    Method pixelsDirty =
                       javafx.scene.image.Image.class.getDeclaredMethod("pixelsDirty");
    pixelsDirty.setAccessible(true);
    pixelsDirty.invoke(writableImg);
  }

  protected void replaceImageBuffer(ByteBuffer direct,
                                    WritableImage writableImg) throws NoSuchMethodException,
                                                               IllegalAccessException,
                                                               InvocationTargetException,
                                                               NoSuchFieldException
  {
    // Get the platform image
    Method getWritablePlatformImage =
                                    javafx.scene.image.Image.class.getDeclaredMethod("getWritablePlatformImage");
    getWritablePlatformImage.setAccessible(true);
    com.sun.prism.Image prismImg =
                                 (com.sun.prism.Image) getWritablePlatformImage.invoke(writableImg);

    // Replace the buffer
    Field pixelBuffer =
                      com.sun.prism.Image.class.getDeclaredField("pixelBuffer");
    pixelBuffer.setAccessible(true);
    pixelBuffer.set(prismImg, direct);

    // Invalidate the platform image
    Field serial =
                 com.sun.prism.Image.class.getDeclaredField("serial");
    serial.setAccessible(true);
    Array.setInt(serial.get(prismImg),
                 0,
                 Array.getInt(serial.get(prismImg), 0) + 1);

    // Invalidate the WritableImage
    Method pixelsDirty =
                       javafx.scene.image.Image.class.getDeclaredMethod("pixelsDirty");
    pixelsDirty.setAccessible(true);
    pixelsDirty.invoke(writableImg);
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}
