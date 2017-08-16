package clearcontrol.stack.sourcesink.source.viewer.gui;

import static java.lang.Math.round;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.var.textfield.NumberVariableTextField;
import clearcontrol.stack.sourcesink.source.StackSourceInterface;
import clearcontrol.stack.sourcesink.source.viewer.StackSourceViewer;

/**
 * Laser device GUI panel
 *
 * @author royer
 */
public class StackSourceViewerPanel extends CustomGridPane
{

  private StackSourceViewer mStackSourceViewer;

  protected NumberVariableTextField<Long> mIndexTextField;
  protected Slider mIndexSlider;
  protected ComboBox<String> mChannelComboBox;

  /**
   * Instantiates a stack source viewer panel
   * 
   * @param pStackSourceViewer
   *          stack source viewer
   */
  public StackSourceViewerPanel(StackSourceViewer pStackSourceViewer)
  {
    init(pStackSourceViewer);

    GridPane.setHgrow(mChannelComboBox, Priority.ALWAYS);
    add(new Label("Channel: "), 0, 0);
    add(mChannelComboBox, 1, 0);

    mIndexSlider.setMaxWidth(Double.POSITIVE_INFINITY);
    GridPane.setHgrow(mIndexSlider, Priority.ALWAYS);
    GridPane.setColumnSpan(mIndexSlider, 3);
    add(mIndexTextField.getTextField(), 0, 1);
    add(mIndexSlider, 1, 1);

  }

  protected StackSourceViewerPanel()
  {
  }

  protected void init(StackSourceViewer pStackSourceViewer)
  {
    mStackSourceViewer = pStackSourceViewer;

    mIndexTextField = new NumberVariableTextField<>("StackIndex",
                                                    pStackSourceViewer.getStackIndexVariable(),
                                                    0L,
                                                    1L,
                                                    1L);

    mIndexTextField.setNumberFormatPrecision(0);

    mIndexSlider = new Slider(0, 1, 1);
    mChannelComboBox = new ComboBox<>();

    updateSlider(pStackSourceViewer, false);
    updateTextField(pStackSourceViewer, false);
    updateComboBoxValues(pStackSourceViewer, false);

    setupListeners(pStackSourceViewer);
  }

  protected void setupListeners(StackSourceViewer pStackSourceViewer)
  {
    mIndexSlider.valueChangingProperty().addListener((e, o, now) -> {
      if (!now)
      {
        long lStackIndex = (long) round(mIndexSlider.valueProperty()
                                                    .get());
        mStackSourceViewer.getStackIndexVariable().set(lStackIndex);
      }
    });

    mIndexSlider.setOnMouseClicked((e) -> {
      long lStackIndex = (long) round(mIndexSlider.valueProperty()
                                                  .get());
      mStackSourceViewer.getStackIndexVariable().set(lStackIndex);

    });

    mIndexSlider.setOnKeyPressed((e) -> {
      long lStackIndex = (long) round(mIndexSlider.valueProperty()
                                                  .get());
      mStackSourceViewer.getStackIndexVariable().set(lStackIndex);

    });

    mChannelComboBox.showingProperty().addListener((e) -> {
      if (mChannelComboBox.isShowing())
        updateComboBoxValues(pStackSourceViewer, true);
      else
        pStackSourceViewer.getStackChannelVariable()
                          .set(mChannelComboBox.valueProperty()
                                               .get());
    });

    StackSourceInterface lStackSourceLocal =
                                           pStackSourceViewer.getStackSourceVariable()
                                                             .get();

    if (!lStackSourceLocal.getChannelList().isEmpty())
    {
      Platform.runLater(() -> {
        mChannelComboBox.valueProperty()
                        .set(lStackSourceLocal.getChannelList()
                                              .get(0));
      });
    }

    pStackSourceViewer.getStackSourceVariable()
                      .addSetListener((o, n) -> {
                        updateSlider(pStackSourceViewer, true);
                        updateTextField(pStackSourceViewer, true);
                        updateComboBoxValues(pStackSourceViewer,
                                             true);
                      });

    pStackSourceViewer.getStackChannelVariable()
                      .addSetListener((o, n) -> {
                        updateSlider(pStackSourceViewer, true);
                        updateTextField(pStackSourceViewer, true);
                        mChannelComboBox.valueProperty().set(n);
                      });

    pStackSourceViewer.getStackIndexVariable()
                      .addSetListener((o, n) -> {
                        updateSlider(pStackSourceViewer, true);
                        updateTextField(pStackSourceViewer, true);
                        mIndexSlider.setValue(n);
                      });
  }

  protected void updateComboBoxValues(StackSourceViewer pStackSourceViewer,
                                      boolean pRunLater)
  {
    Runnable lRunnable = () -> {
      StackSourceInterface lStackSourceLocal =
                                             pStackSourceViewer.getStackSourceVariable()
                                                               .get();
      if (lStackSourceLocal != null)
      {
        mChannelComboBox.getItems()
                        .retainAll(lStackSourceLocal.getChannelList());
        for (String lChannel : lStackSourceLocal.getChannelList())
        {
          if (!mChannelComboBox.getItems().contains(lChannel))
          {
            mChannelComboBox.getItems().add(lChannel);
          }
        }
      }
    };

    if (pRunLater)
      Platform.runLater(lRunnable);
    else
      lRunnable.run();
  }

  protected void updateSlider(StackSourceViewer pStackSourceViewer,
                              boolean pRunLater)
  {
    Runnable lRunnable = () -> {

      StackSourceInterface lStackSource =
                                        pStackSourceViewer.getStackSourceVariable()
                                                          .get();

      if (lStackSource != null)
      {
        long lNumberOfStacks =
                             lStackSource.getNumberOfStacks(pStackSourceViewer.getStackChannelVariable()
                                                                              .get());

        mIndexSlider.setMax(lNumberOfStacks);

        if (lNumberOfStacks <= 100)
        {
          mIndexSlider.setShowTickMarks(true);
          mIndexSlider.setMajorTickUnit(1);
          mIndexSlider.setMinorTickCount(0);
          mIndexSlider.snapToTicksProperty().set(true);
        }
        else if (lNumberOfStacks > 100)
        {
          mIndexSlider.setShowTickMarks(false);
          mIndexSlider.setMajorTickUnit(10);
          mIndexSlider.setMinorTickCount(1);
          mIndexSlider.snapToTicksProperty().set(true);
        }
      }
    };

    if (pRunLater)
      Platform.runLater(lRunnable);
    else
      lRunnable.run();

  }

  protected void updateTextField(StackSourceViewer pStackSourceViewer,
                                 boolean pRunLater)
  {
    Runnable lRunnable = () -> {

      StackSourceInterface lStackSource =
                                        pStackSourceViewer.getStackSourceVariable()
                                                          .get();

      if (lStackSource != null)
      {
        long lNumberOfStacks =
                             lStackSource.getNumberOfStacks(pStackSourceViewer.getStackChannelVariable()
                                                                              .get());

        mIndexTextField.getMaxVariable().set(lNumberOfStacks - 1);
      }
    };

    if (pRunLater)
      Platform.runLater(lRunnable);
    else
      lRunnable.run();

  }

}
