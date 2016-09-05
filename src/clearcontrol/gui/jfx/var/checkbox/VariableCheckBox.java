package clearcontrol.gui.jfx.var.checkbox;

import clearcontrol.core.variable.Variable;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class VariableCheckBox extends HBox
{

	private final Label mLabel;
	private final CheckBox mCheckBox;
	private Variable<Boolean> mVariable;

	public VariableCheckBox(String pCheckBoxLabel,
													Variable<Boolean> pVariable)
	{
		super();
		mVariable = pVariable;

		setAlignment(Pos.CENTER);
		setPadding(new Insets(25, 25, 25, 25));

		mLabel = new Label(pCheckBoxLabel);
		mLabel.setAlignment(Pos.CENTER_LEFT);

		mCheckBox = new CheckBox();
		mCheckBox.setAlignment(Pos.CENTER);

		// getCheckBox().setPrefWidth(7 * 15);

		getChildren().add(getCheckBox());
		getChildren().add(getLabel());

		mVariable.addSetListener((o, n) -> {
			if (n != mCheckBox.isSelected() && n != null)
				Platform.runLater(() -> {
					mCheckBox.setSelected(n);
				});
		});

		mCheckBox.setOnAction((e) -> {
			if (mCheckBox.isSelected() != mVariable.get())
				mVariable.setAsync(mCheckBox.isSelected());
		});

		Platform.runLater(() -> {
			mCheckBox.setSelected(mVariable.get());
		});

	}

	public Label getLabel()
	{
		return mLabel;
	}

	public CheckBox getCheckBox()
	{
		return mCheckBox;
	}

}
