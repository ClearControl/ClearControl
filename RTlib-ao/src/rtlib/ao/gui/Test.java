package rtlib.ao.gui;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class Test extends JPanel
{

	/**
	 * Create the panel.
	 */
	public Test()
	{
		setLayout(new MigLayout("",
								"[32px, grow][32px,grow]",
								"[32px,grow][32px,grow]"));

		final JPanel panel = new JPanel();
		add(panel, "cell 0 0,grow");

	}

}
