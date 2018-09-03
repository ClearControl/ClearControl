package clearcontrol.scripting.gui.demo.other;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DragDemo
{

  public static void main(String[] args)
  {

    // Create a frame
    final Frame frame = new Frame("Example Frame");

    /*
    
    * Create a container with a flow layout, which arranges its children
    
    * horizontally and center aligned. A container can also be created with
    
    * a specific layout using Panel(LayoutManager) constructor, e.g.
    
    * Panel(new FlowLayout(FlowLayout.RIGHT)) for right alignment
    
    */
    final Panel panel = new Panel();

    // Add a drop target text area in the center of the frame
    final Component textArea = new DropTargetTextArea();
    frame.add(textArea, BorderLayout.CENTER);

    // Add several draggable labels to the container
    final Label helloLabel = new DraggableLabel("Hello");
    final Label worldLabel = new DraggableLabel("World");
    panel.add(helloLabel);
    panel.add(worldLabel);

    // Add the container to the bottom of the frame
    frame.add(panel, BorderLayout.SOUTH);

    // Display the frame
    final int frameWidth = 300;
    final int frameHeight = 300;
    frame.setSize(frameWidth, frameHeight);

    frame.setVisible(true);

  }

  // Make a Label draggable; You can use the example to make any component
  // draggable
  public static class DraggableLabel extends Label implements
                                     DragGestureListener,
                                     DragSourceListener
  {
    DragSource dragSource;

    public DraggableLabel(String text)
    {

      setText(text);

      dragSource = new DragSource();

      dragSource.createDefaultDragGestureRecognizer(this,
                                                    DnDConstants.ACTION_COPY_OR_MOVE,
                                                    this);
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent evt)
    {

      final Transferable transferable =
                                      new StringSelection(getText());

      dragSource.startDrag(evt,
                           DragSource.DefaultCopyDrop,
                           transferable,
                           this);
    }

    @Override
    public void dragEnter(DragSourceDragEvent evt)
    {

      // Called when the user is dragging this drag source and enters the
      // drop
      // target

      System.out.println("Drag enter");
    }

    @Override
    public void dragOver(DragSourceDragEvent evt)
    {

      // Called when the user is dragging this drag source and moves over
      // the
      // drop target

      System.out.println("Drag over");
    }

    @Override
    public void dragExit(DragSourceEvent evt)
    {

      // Called when the user is dragging this drag source and leaves the
      // drop
      // target

      System.out.println("Drag exit");
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent evt)
    {

      // Called when the user changes the drag action between copy or move

      System.out.println("Drag action changed");
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent evt)
    {

      // Called when the user finishes or cancels the drag operation

      System.out.println("Drag action End");
    }

  }

  // Make a TextArea a drop target; You can use the example to make any
  // component a drop target
  public static class DropTargetTextArea extends TextArea
                                         implements DropTargetListener
  {

    public DropTargetTextArea()
    {

      new DropTarget(this, this);
    }

    @Override
    public void dragEnter(DropTargetDragEvent evt)
    {

      // Called when the user is dragging and enters this drop target

      System.out.println("Drop enter");
    }

    @Override
    public void dragOver(DropTargetDragEvent evt)
    {

      // Called when the user is dragging and moves over this drop target

      System.out.println("Drop over");
    }

    @Override
    public void dragExit(DropTargetEvent evt)
    {

      // Called when the user is dragging and leaves this drop target

      System.out.println("Drop exit");
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent evt)
    {

      // Called when the user changes the drag action between copy or move

      System.out.println("Drop action changed");
    }

    @Override
    public void drop(DropTargetDropEvent evt)
    {

      // Called when the user finishes or cancels the drag operation

      try
      {

        final Transferable transferable = evt.getTransferable();

        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
        {

          evt.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

          final List<File> lFileList =
                                     (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);

          evt.getDropTargetContext().dropComplete(true);

          System.out.println(lFileList);

        }
        else
        {

          evt.rejectDrop();

        }

      }
      catch (final IOException e)
      {

        evt.rejectDrop();

      }
      catch (final UnsupportedFlavorException e)
      {

        evt.rejectDrop();

      }
    }

  }

}
