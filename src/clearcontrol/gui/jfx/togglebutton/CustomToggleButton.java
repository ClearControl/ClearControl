package clearcontrol.gui.jfx.togglebutton;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.variable.JFXPropertyVariable;
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
	
	public CustomToggleButton(String pSelectedText, String pDeselectedText)
	{
		super(pDeselectedText);
		setStyle();
		
		selectedProperty().addListener((e)->{
			if(selectedProperty().get())
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
		this(pSelectedText,pDeselectedText);
		
		pBooleanVariable.addSetListener((o,n)->{
			if (o!=n)
			{
				selectedProperty().set(n);
			}
		});
		
		selectedProperty().addListener((e)->{
			pBooleanVariable.set(selectedProperty().get());
		});
	}

	private void setStyle()
	{
		getStylesheets().add(getClass().getResource("coloredbutton.css")
																		.toExternalForm());
	
	}

}
