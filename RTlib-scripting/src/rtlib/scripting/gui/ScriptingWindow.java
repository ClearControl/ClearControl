package rtlib.scripting.gui;

import java.awt.HeadlessException;

import javax.swing.JFrame;

import rtlib.scripting.engine.ScriptingEngine;

public class ScriptingWindow extends JFrame
{

	private static final long serialVersionUID = 1L;

	public ScriptingWindow() throws HeadlessException
	{
		this("Scripting Window", null, 60, 80);
	}

	public ScriptingWindow(	String pTitle,
													ScriptingEngine pScriptingEngine,
													int pNumberOfRows,
													int pNumberOfCols) throws HeadlessException
	{
		super(pTitle);

		final ScriptingPanel lScriptingPanel = new ScriptingPanel(pScriptingEngine,
																															pNumberOfRows,
																															pNumberOfCols);
		add(lScriptingPanel);

		pack();

	}



}
