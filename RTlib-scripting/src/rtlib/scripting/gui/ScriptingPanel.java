package rtlib.scripting.gui;

import java.awt.Color;
import java.io.PrintStream;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.text.AbstractDocument;

import net.miginfocom.swing.MigLayout;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import rtlib.core.gui.LineLimitedDocumentFilter;
import rtlib.scripting.engine.ScriptingEngine;
import rtlib.scripting.engine.ScriptingEngineListener;
import rtlib.scripting.lang.groovy.GroovyScripting;
import rtlib.scripting.lang.jython.JythonScripting;

public class ScriptingPanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	private static final int cMaxNumberOfLines = 500_000;
	private final ScriptingEngine mScriptingEngine;

	public ScriptingPanel()
	{
		this(null);
	}

	public ScriptingPanel(ScriptingEngine pScriptingEngine)
	{
		this(pScriptingEngine, 60, 80);
	}

	public ScriptingPanel(ScriptingEngine pScriptingEngine,
												int pNumberOfRows,
												int pNumberOfCols)
	{
		super();
		mScriptingEngine = pScriptingEngine;

		final RSyntaxTextArea lRSyntaxTextArea = new RSyntaxTextArea(	pNumberOfRows,
																																	pNumberOfCols);

		if (pScriptingEngine != null)
		{
			if (pScriptingEngine.getScriptingLanguageInterface() instanceof GroovyScripting)
				lRSyntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
			else if (pScriptingEngine.getScriptingLanguageInterface() instanceof JythonScripting)
				lRSyntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);

			pScriptingEngine.addListener(new ScriptingEngineListener()
			{

				@Override
				public void updatedScript(ScriptingEngine pScriptingEngine,
																	String pScript)
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void beforeScriptExecution(ScriptingEngine pScriptingEngine,
																					String pScriptString)
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void asynchronousResult(	ScriptingEngine pScriptingEngine,
																				String pScriptString,
																				Map<String, Object> pBinding,
																				Throwable pThrowable,
																				String pErrorMessage)
				{
					if (pErrorMessage != null)
					{
						final PrintStream lPrintStream = new PrintStream(pScriptingEngine.getOutputStream());
						lPrintStream.println(pErrorMessage);
					}
				}

				@Override
				public void afterScriptExecution(	ScriptingEngine pScriptingEngine,
																					String pScriptString)
				{
					// TODO Auto-generated method stub

				}
			});
		}
		else
			lRSyntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);

		lRSyntaxTextArea.setCodeFoldingEnabled(true);
		lRSyntaxTextArea.setTabSize(2);
		lRSyntaxTextArea.setPaintTabLines(true);
		lRSyntaxTextArea.setTabLineColor(Color.LIGHT_GRAY);

		final RTextScrollPane lRTextScrollPane = new RTextScrollPane(lRSyntaxTextArea);

		setLayout(new MigLayout("insets 0",
														"[49px,grow][49px,grow]",
														"[grow][19px,grow][]"));

		final JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.75);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane, "cell 0 0 2 1,grow");

		final JPanel lExecuteButtonAndConsolePanel = new JPanel();
		splitPane.setLeftComponent(lRTextScrollPane);
		splitPane.setRightComponent(lExecuteButtonAndConsolePanel);
		lExecuteButtonAndConsolePanel.setLayout(new MigLayout("insets 0",
																													"[606px,grow,fill][606px,grow,fill][606px,grow,fill]",
																													"[29px][201px,grow,fill]"));

		final JButton lExecuteButton = new JButton("execute");
		lExecuteButtonAndConsolePanel.add(lExecuteButton,
																			"cell 0 0,alignx left,aligny center");

		final JButton lCancelButton = new JButton("cancel");
		lExecuteButtonAndConsolePanel.add(lCancelButton, "cell 1 0");

		final JButton lConsoleClearButton = new JButton("clear");
		lExecuteButtonAndConsolePanel.add(lConsoleClearButton, "cell 2 0");


		final JTextArea lConsoleTextArea = new JTextArea();
		((AbstractDocument) lConsoleTextArea.getDocument()).setDocumentFilter(new LineLimitedDocumentFilter(lConsoleTextArea,
																																																				cMaxNumberOfLines));

		final JScrollPane lConsoleTextAreaScrollPane = new JScrollPane(lConsoleTextArea);
		lExecuteButtonAndConsolePanel.add(lConsoleTextAreaScrollPane,
																			"cell 0 1 3 1,alignx center,aligny center");

		final OutputStreamToJTextArea lOutputStreamToJTextArea = new OutputStreamToJTextArea(lConsoleTextArea);
		mScriptingEngine.setOutputStream(lOutputStreamToJTextArea);

		lExecuteButton.addActionListener((e) -> {
			mScriptingEngine.setScript(lRSyntaxTextArea.getText());
			mScriptingEngine.executeScriptAsynchronously();
		});

		lConsoleClearButton.addActionListener((e) -> {
			try
			{
				lConsoleTextArea.getDocument()
												.remove(0,
																lConsoleTextArea.getDocument()
																								.getLength());
			}
			catch (final Exception e1)
			{
				e1.printStackTrace();
			}
		});

		lCancelButton.addActionListener((e) -> {
			mScriptingEngine.stopAsynchronousExecution();
		});

	}

}
