package clearcontrol.gui.jfx.rbg;

import clearcontrol.gui.jfx.rbg.skin.RadialBargraphSkin;
import javafx.scene.control.Skin;

/**
 * Created by moon on 1/29/16.
 */
public class RadialBargraph	extends
														eu.hansolo.enzo.gauge.RadialBargraph
{
	// ******************** Style related *************************************
	@Override
	protected Skin createDefaultSkin()
	{
		return new RadialBargraphSkin(this);
	}

	@Override
	public String getUserAgentStylesheet()
	{
		return getClass().getResource("css/radial-bargraph.css")
											.toExternalForm();
	}
}
