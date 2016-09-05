package clearcontrol.gui.jfx.var.togglebutton;

import clearcontrol.core.variable.Variable;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;

public class CustomToggleButton extends ToggleButton
{

	public CustomToggleButton()
	{
		super();
		setStyle();
	}

	public CustomToggleButton(String pText, Node pGraphic)
	{
		super(pText, pGraphic);
		setStyle();
	}

	public CustomToggleButton(String pText)
	{
		super(pText);
		setStyle();
	}

	public CustomToggleButton(String pSelectedText,
														String pDeselectedText)
	{
		super(pDeselectedText);
		setStyle();

		selectedProperty().addListener((e) -> {
			if (selectedProperty().get())
			{
				setText(pSelectedText);
			}
			else
			{
				setText(pDeselectedText);
			}
		});
	}

	public CustomToggleButton(String pSelectedText,
														String pDeselectedText,
														Variable<Boolean> pBooleanVariable)
	{
		this(pSelectedText, pDeselectedText);

		pBooleanVariable.addSetListener((o, n) -> {
			if (selectedProperty().get() != n && n != null)
			{
				Platform.runLater(() -> {
					selectedProperty().set(n);
				});
			}
		});

		selectedProperty().addListener((e) -> {
			pBooleanVariable.setAsync(selectedProperty().get());
		});

		Platform.runLater(() -> {
			if (pBooleanVariable.get() != null)
				selectedProperty().set(pBooleanVariable.get());
		});

	}

	private void setStyle()
	{
		getStylesheets().add(getClass().getResource("css/coloredbutton.css")
																		.toExternalForm());

	}

}
