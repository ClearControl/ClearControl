package rtlib.scripting.gui;

import java.awt.HeadlessException;

import javax.swing.JFrame;

import rtlib.scripting.engine.ScriptingEngine;

public class ScriptingWindow extends JFrame
{

	private static final long serialVersionUID = 1L;
	private ScriptingPanel mScriptingPanel;

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

		mScriptingPanel = new ScriptingPanel(	pTitle,
												pScriptingEngine,
												pNumberOfRows,
												pNumberOfCols);
		add(mScriptingPanel);
		pack();
		setSize(512, 512);

	}

	public void loadLastLoadedScriptFile()
	{
		mScriptingPanel.loadLastLoadedScriptFile();
	}

}
