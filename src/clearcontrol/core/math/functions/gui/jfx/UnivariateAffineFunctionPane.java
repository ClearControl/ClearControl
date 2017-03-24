package clearcontrol.core.math.functions.gui.jfx;

import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class UnivariateAffineFunctionPane extends CustomGridPane
{
  // number of decimals after comma:
  int mPrecision = 3;
  private Variable<UnivariateAffineFunction> mFunctionVariable;

  private TextField mSlopeTextField, mConstantTextField;
  private int mCursor = 0;

  public UnivariateAffineFunctionPane(String pName,
                                      Variable<UnivariateAffineFunction> pFunctionVariable)
  {
    super();
    mFunctionVariable = pFunctionVariable;

    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(0);
    setPadding(new Insets(0, 25, 0, 25));

    Label lNameLabel = null;
    if (pName != null)
      lNameLabel = new Label(pName + ":  y = ");
    else
      lNameLabel = new Label(" y = ");
    mSlopeTextField =
                    new TextField("" + mFunctionVariable.get()
                                                        .getSlope());
    mSlopeTextField.setPrefColumnCount(2 + mPrecision);
    mSlopeTextField.setStyle("-fx-text-box-border: transparent;");
    mSlopeTextField.setStyle("-fx-background-insets: 0;");
    mSlopeTextField.setStyle("-fx-background-color: -fx-control-inner-background;");

    Label lXLabel = new Label("* x +");
    mConstantTextField =
                       new TextField(""
                                     + mFunctionVariable.get()
                                                        .getConstant());
    mConstantTextField.setPrefColumnCount(2 + mPrecision);
    mConstantTextField.setStyle("-fx-text-box-border: transparent;");
    mConstantTextField.setStyle("-fx-background-insets: 0;");
    mConstantTextField.setStyle("-fx-background-color: -fx-control-inner-background;");

    add(lNameLabel, mCursor++, 0);
    add(mSlopeTextField, mCursor++, 0);
    add(lXLabel, mCursor++, 0);
    add(mConstantTextField, mCursor++, 0);

    pFunctionVariable.addSetListener((o, n) -> {
      if (o.getSlope() != n.getSlope()
          || o.getConstant() != n.getConstant())
        setFunctionTextField(n);
    });

    mSlopeTextField.focusedProperty().addListener((obs, o, n) -> {
      if (!n)
        parseSlopeAndSetVariable();
    });
    mSlopeTextField.setOnKeyPressed((e) -> {
      if (e.getCode().equals(KeyCode.ENTER))
        parseSlopeAndSetVariable();
      ;
    });

    mConstantTextField.focusedProperty().addListener((obs, o, n) -> {
      if (!n)
        parseOffsetAndSetVariable();
    });
    mConstantTextField.setOnKeyPressed((e) -> {
      if (e.getCode().equals(KeyCode.ENTER))
        parseOffsetAndSetVariable();
      ;
    });

  }

  private void parseSlopeAndSetVariable()
  {
    try
    {
      String lString = mSlopeTextField.textProperty().get();
      double lSlope = Double.parseDouble(lString);

      if (lSlope != mFunctionVariable.get().getSlope())
      {
        mFunctionVariable.get().setSlope(lSlope);
        mFunctionVariable.setCurrent();
        mSlopeTextField.setStyle("-fx-text-fill: black");
      }
    }
    catch (NumberFormatException e)
    {
      mSlopeTextField.setStyle("-fx-text-fill: red");
    }
  }

  private void parseOffsetAndSetVariable()
  {
    try
    {
      String lString = mConstantTextField.textProperty().get();
      double lConstant = Double.parseDouble(lString);
      if (lConstant != mFunctionVariable.get().getConstant())
      {
        mFunctionVariable.get().setConstant(lConstant);
        mFunctionVariable.setCurrent();
        mConstantTextField.setStyle("-fx-text-fill: black");
      }
    }
    catch (NumberFormatException e)
    {
      mConstantTextField.setStyle("-fx-text-fill: red");
    }
  }

  private void setFunctionTextField(UnivariateAffineFunction pFunction)
  {
    Platform.runLater(() -> {
      mSlopeTextField.textProperty()
                     .set(String.format("%." + mPrecision
                                        + "f",
                                        pFunction.getSlope()));
      mConstantTextField.textProperty()
                        .set(String.format("%." + mPrecision
                                           + "f",
                                           pFunction.getConstant()));
    });
  }

}
