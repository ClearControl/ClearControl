package clearcontrol.gui.jfx.var.customvarpanel;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.var.bounds.BoundedVariablePane;
import clearcontrol.gui.jfx.var.checkbox.VariableCheckBox;
import clearcontrol.gui.jfx.var.combo.IntComboBoxVariable;
import clearcontrol.gui.jfx.var.function.UnivariateAffineFunctionPane;
import clearcontrol.gui.jfx.var.onoffarray.OnOffArrayPane;
import clearcontrol.gui.jfx.var.slider.VariableSlider;
import clearcontrol.gui.jfx.var.textfield.NumberVariableTextField;
import clearcontrol.gui.jfx.var.textfield.StringVariableTextField;
import clearcontrol.gui.jfx.var.togglebutton.VariableToggleButton;

/**
 * Custom variable pane
 *
 * @author royer
 */
public class CustomVariablePane extends TabPane
{

  private static final double cDefaultWidth = 300;

  private int mCursor = 0;
  private double mSliderWidth;

  private CustomGridPane mCurrentTabGridPane;

  /**
   * Instantiates a custom variable pane
   */
  public CustomVariablePane()
  {
    this(cDefaultWidth);

    getTabs().addListener((ListChangeListener<Tab>) change -> {
      final StackPane header =
                             (StackPane) this.lookup(".tab-header-area");

      if (header != null)
      {
        if (this.getTabs().size() == 1)
          header.setPrefHeight(0);
        else
          header.setPrefHeight(-1);
      }
    });

    setStyle("-fx-tab-max-height: 0 ;");
    // final StackPane header = (StackPane) lookup(".tab-header-area");
    // header.setStyle("visibility: hidden ;");
    setMaxWidth(Double.MAX_VALUE);

  }

  /**
   * Instantiates a custom variable pane with a given width for sliders
   * 
   * @param pSliderWidth
   *          slider width
   */
  public CustomVariablePane(double pSliderWidth)
  {
    super();
    mSliderWidth = pSliderWidth;
  }

  /**
   * Adds a tab
   * 
   * @param pName
   *          tab name
   */
  public void addTab(String pName)
  {
    Tab lTab = new Tab(pName);
    lTab.setClosable(false);

    getTabs().add(lTab);

    CustomGridPane lGridPane = new CustomGridPane();
    lTab.setContent(lGridPane);

    mCurrentTabGridPane = lGridPane;

    if (getTabs().size() > 1)
    {
      setStyle("-fx-tab-max-height: 20 ;");
      // final StackPane header = (StackPane) lookup(".tab-header-area");
      // header.setStyle("visibility: showing ;");
    }

    mCursor = 0;
  }

  /**
   * Adds a check box for a given variable
   * 
   * @param pLabelText
   *          label text
   * @param pBooleanVariable
   *          boolean variable
   * @return check box
   */
  public VariableCheckBox addCheckBoxForVariable(String pLabelText,
                                                 Variable<Boolean> pBooleanVariable)
  {
    VariableCheckBox lVariableCheckBox =
                                       new VariableCheckBox(pLabelText,
                                                            pBooleanVariable);

    int lCursor = mCursor++;
    GridPane.setHgrow(lVariableCheckBox, Priority.ALWAYS);
    mCurrentTabGridPane.add(lVariableCheckBox.getLabel(), 0, lCursor);
    mCurrentTabGridPane.add(lVariableCheckBox.getCheckBox(),
                            1,
                            lCursor);

    return lVariableCheckBox;
  }

  /**
   * Adds a custom toggle button given the selected and unselected text and
   * corresponding variable.
   * 
   * @param pSelectedText
   *          selected text
   * @param pUnselectedText
   *          unselected text
   * @param pBooleanVariable
   *          boolean variable
   * @return custom toggle button
   */
  public VariableToggleButton addToggleButton(String pSelectedText,
                                              String pUnselectedText,
                                              Variable<Boolean> pBooleanVariable)
  {
    final VariableToggleButton lToggleButton =
                                             new VariableToggleButton(pSelectedText,
                                                                      pUnselectedText,
                                                                      pBooleanVariable);

    int lCursor = mCursor++;
    mCurrentTabGridPane.add(lToggleButton, 0, lCursor);
    GridPane.setHgrow(lToggleButton, Priority.ALWAYS);
    GridPane.setColumnSpan(lToggleButton, 3);

    return lToggleButton;
  }

  /**
   * Adds a number text field for the given variable
   * 
   * @param pTextFieldLabel
   *          text field label
   * @param pVariable
   *          variable
   * @return number text field
   */
  public <T extends Number> NumberVariableTextField<T> addNumberTextFieldForVariable(String pTextFieldLabel,
                                                                                     BoundedVariable<T> pVariable)
  {
    final NumberVariableTextField<T> lNumberTextField =
                                                      new NumberVariableTextField<T>(pTextFieldLabel,
                                                                                     pVariable);

    int lCursor = mCursor++;
    mCurrentTabGridPane.add(lNumberTextField.getLabel(), 0, lCursor);
    mCurrentTabGridPane.add(lNumberTextField.getTextField(),
                            1,
                            lCursor);

    return lNumberTextField;
  }

  /**
   * Adds a number text field for the given variable
   * 
   * @param pTextFieldLabel
   *          text field label
   * @param pVariable
   *          variable
   * @param pMin
   *          min
   * @param pMax
   *          max
   * @param pGranularity
   *          granularity
   * @return number text field
   */
  public <T extends Number> NumberVariableTextField<T> addNumberTextFieldForVariable(String pTextFieldLabel,
                                                                                     Variable<T> pVariable,
                                                                                     T pMin,
                                                                                     T pMax,
                                                                                     T pGranularity)
  {
    final NumberVariableTextField<T> lNumberTextField =
                                                      new NumberVariableTextField<T>(pTextFieldLabel,
                                                                                     pVariable,
                                                                                     pMin,
                                                                                     pMax,
                                                                                     pGranularity);

    int lCursor = mCursor++;
    mCurrentTabGridPane.add(lNumberTextField.getLabel(), 0, lCursor);
    mCurrentTabGridPane.add(lNumberTextField.getTextField(),
                            1,
                            lCursor);

    return lNumberTextField;
  }

  /**
   * Adds a slider for a given variable, min, max, granularity, and tick spacing
   * 
   * @param pVariable
   *          variable
   * @param pMin
   *          min
   * @param pMax
   *          max
   * @param pGranularity
   *          granularity
   * @param pTicks
   *          tick spacing
   * @return slider
   */
  public <T extends Number> VariableSlider<T> addSliderForVariable(Variable<T> pVariable,
                                                                   T pMin,
                                                                   T pMax,
                                                                   T pGranularity,
                                                                   T pTicks)
  {
    return addSliderForVariable(pVariable.getName(),
                                pVariable,
                                pMin,
                                pMax,
                                pGranularity,
                                pTicks);
  }

  /**
   * Adds a slider with a given slider name, for a given variable, min, max,
   * granularity and tick spacing.
   * 
   * @param pSliderName
   *          slider name
   * @param pVariable
   *          variable
   * @param pMin
   *          min
   * @param pMax
   *          max
   * @param pGranularity
   *          granularity
   * @param pTicks
   *          tick spacing
   * @return slider
   */
  public <T extends Number> VariableSlider<T> addSliderForVariable(String pSliderName,
                                                                   Variable<T> pVariable,
                                                                   T pMin,
                                                                   T pMax,
                                                                   T pGranularity,
                                                                   T pTicks)
  {
    return addSliderForVariable(pSliderName,
                                pVariable,
                                new Variable<T>("Min", pMin),
                                new Variable<T>("Max", pMax),
                                new Variable<T>("Granularity",
                                                pGranularity),
                                pTicks);
  }

  /**
   * Adds a slider for a given slider name, variable, min, max, granularity and
   * tick spacing
   * 
   * @param pSliderName
   *          slider name
   * @param pVariable
   *          variable
   * @param pMin
   *          min
   * @param pMax
   *          max
   * @param pGranularity
   *          granularity
   * @param pTicks
   *          tick spacing
   * @return slider
   */
  public <T extends Number> VariableSlider<T> addSliderForVariable(String pSliderName,
                                                                   Variable<T> pVariable,
                                                                   Variable<T> pMin,
                                                                   Variable<T> pMax,
                                                                   Variable<T> pGranularity,
                                                                   T pTicks)
  {
    final VariableSlider<T> lSlider =
                                    new VariableSlider<T>(pSliderName,
                                                          pVariable,
                                                          pMin,
                                                          pMax,
                                                          pGranularity,
                                                          pTicks);
    lSlider.getSlider().setPrefWidth(mSliderWidth);
    lSlider.getSlider().setMinWidth(mSliderWidth / 4);
    lSlider.getSlider().setMaxWidth(Double.MAX_VALUE);

    GridPane.setHgrow(lSlider.getSlider(), Priority.ALWAYS);

    int lCursor = mCursor++;
    mCurrentTabGridPane.add(lSlider.getLabel(), 0, lCursor);
    mCurrentTabGridPane.add(lSlider.getSlider(), 1, lCursor);
    mCurrentTabGridPane.add(lSlider.getTextField(), 2, lCursor);

    return lSlider;
  }

  /**
   * Adds a slider for agiven variable and tick spacing
   * 
   * @param pVariable
   *          variable
   * @param pTicks
   *          tick spacing
   * @return slider
   */
  public <T extends Number> VariableSlider<T> addSliderForVariable(BoundedVariable<T> pVariable,
                                                                   T pTicks)
  {
    return addSliderForVariable(pVariable.getName(),
                                pVariable,
                                pTicks);
  }

  /**
   * Adds a slider for a given slider name, variable, and tick spacing.
   * 
   * @param pSliderName
   *          slider name
   * @param pVariable
   *          variable
   * @param pTicks
   *          tick spacing
   * @return slider
   */
  public <T extends Number> VariableSlider<T> addSliderForVariable(String pSliderName,
                                                                   BoundedVariable<T> pVariable,
                                                                   T pTicks)
  {
    final VariableSlider<T> lSlider =
                                    new VariableSlider<T>(pSliderName,
                                                          pVariable,
                                                          pTicks);
    lSlider.setPrefWidth(mSliderWidth);
    lSlider.setMinWidth(mSliderWidth / 4);

    int lCursor = mCursor++;
    GridPane.setHgrow(lSlider.getSlider(), Priority.ALWAYS);
    mCurrentTabGridPane.add(lSlider.getLabel(), 0, lCursor);
    mCurrentTabGridPane.add(lSlider.getSlider(), 1, lCursor);
    mCurrentTabGridPane.add(lSlider.getTextField(), 2, lCursor);

    return lSlider;
  }

  /**
   * Adds a on/off array pane
   * 
   * @param pOnOffArrayPaneName
   *          on/off array pane name
   * @return on/off array pane
   */
  public <T extends Number> OnOffArrayPane addOnOffArray(String pOnOffArrayPaneName)
  {

    final OnOffArrayPane lOnOffArrayPane = new OnOffArrayPane();
    lOnOffArrayPane.setVertical(false);

    lOnOffArrayPane.setPrefWidth(mSliderWidth);
    lOnOffArrayPane.setMinWidth(mSliderWidth / 4);
    lOnOffArrayPane.setMaxWidth(Double.MAX_VALUE);

    GridPane.setHgrow(lOnOffArrayPane, Priority.ALWAYS);

    Label lLabel = new Label(pOnOffArrayPaneName);

    int lCursor = mCursor++;
    mCurrentTabGridPane.add(lLabel, 0, lCursor);
    mCurrentTabGridPane.add(lOnOffArrayPane, 1, lCursor);
    GridPane.setColumnSpan(lOnOffArrayPane, 2);

    return lOnOffArrayPane;
  }

  /**
   * Adds a function pane
   * 
   * @param pName
   *          name
   * @param pFunction
   *          function
   */
  public void addFunctionPane(String pName,
                              Variable<UnivariateAffineFunction> pFunction)
  {
    Label lLabel = new Label(pName + " ");
    UnivariateAffineFunctionPane lFunctionPane =
                                               new UnivariateAffineFunctionPane(null,
                                                                                pFunction);
    int lCursor = mCursor++;
    mCurrentTabGridPane.add(lLabel, 0, lCursor);
    mCurrentTabGridPane.add(lFunctionPane, 1, lCursor);
    GridPane.setHgrow(lFunctionPane, Priority.ALWAYS);
    GridPane.setColumnSpan(lFunctionPane, 2);
  }

  /**
   * Adds a bounded variable pane
   * 
   * @param pName
   *          name
   * @param pBoundedVariable
   *          bounded variable
   */
  public void addBoundedVariable(String pName,
                                 BoundedVariable<Number> pBoundedVariable)
  {

    BoundedVariablePane lBoundedVariablePane =
                                             new BoundedVariablePane(null,
                                                                     pBoundedVariable);

    Label lLabel = new Label(pName);

    int lCursor = mCursor++;
    mCurrentTabGridPane.add(lLabel, 0, lCursor);
    mCurrentTabGridPane.add(lBoundedVariablePane, 1, lCursor);
    GridPane.setHgrow(lBoundedVariablePane, Priority.ALWAYS);
    GridPane.setColumnSpan(lBoundedVariablePane, 2);
  }

  /**
   * Adds a int combo box pane
   * 
   * @param pName
   *          name
   * @param pBoundedVariable
   *          bounded variable
   * @param pMin
   *          min
   * @param pMax
   *          max
   */
  public void addIntComboBox(String pName,
                             Variable<Integer> pBoundedVariable,
                             int pMin,
                             int pMax)
  {

    IntComboBoxVariable lIntComboBoxVariable =
                                             new IntComboBoxVariable(pBoundedVariable,
                                                                     pMin,
                                                                     pMax);

    Label lLabel = new Label(pName);

    int lCursor = mCursor++;
    mCurrentTabGridPane.add(lLabel, 0, lCursor);
    mCurrentTabGridPane.add(lIntComboBoxVariable, 1, lCursor);
    GridPane.setHgrow(lIntComboBoxVariable, Priority.ALWAYS);
    GridPane.setColumnSpan(lIntComboBoxVariable, 2);
  }

  public void addStringField(String pName, Variable<String> pVariable)
  {
    StringVariableTextField lPostFixTextField =
                                              new StringVariableTextField(pName,
                                                                          pVariable);

    Label lLabel = new Label(pName);
    int lCursor = mCursor++;

    mCurrentTabGridPane.add(lLabel, 0, lCursor);
    mCurrentTabGridPane.add(lPostFixTextField, 1, lCursor);
    GridPane.setHgrow(lPostFixTextField, Priority.ALWAYS);
    GridPane.setColumnSpan(lPostFixTextField, 2);

  }

}
