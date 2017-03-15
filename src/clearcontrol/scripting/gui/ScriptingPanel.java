package clearcontrol.scripting.gui;

import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultCaret;

import net.miginfocom.swing.MigLayout;
import clearcontrol.core.file.FileEventNotifier;
import clearcontrol.core.file.FileEventNotifier.FileEventKind;
import clearcontrol.core.file.FileEventNotifierListener;
import clearcontrol.core.gui.LineLimitedDocumentFilter;
import clearcontrol.scripting.engine.ScriptingEngine;
import clearcontrol.scripting.engine.ScriptingEngineListener;
import clearcontrol.scripting.lang.groovy.GroovyScripting;
import clearcontrol.scripting.lang.jython.JythonScripting;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

public class ScriptingPanel extends JPanel implements
                            DropTargetListener,
                            FileEventNotifierListener,
                            DocumentListener,
                            KeyListener
{

  private static final long serialVersionUID = 1L;

  private File mLastLoadedScriptFile;

  private static final int cMaxNumberOfLines = 500_000;
  private final ScriptingEngine mScriptingEngine;
  private final JTextField mCurrentFileTextField;
  private final RSyntaxTextArea mRSyntaxTextArea;
  private FileEventNotifier mFileEventNotifier;
  private JTextArea mConsoleTextArea;

  public ScriptingPanel(String pName)
  {
    this(pName, null);
  }

  public ScriptingPanel(String pName,
                        ScriptingEngine pScriptingEngine)
  {
    this(pName, pScriptingEngine, null, 60, 80);
  }

  public ScriptingPanel(String pName,
                        ScriptingEngine pScriptingEngine,
                        int pNumberOfRows,
                        int pNumberOfCols)
  {
    this(pName, pScriptingEngine, null, pNumberOfRows, pNumberOfCols);
  }

  public ScriptingPanel(String pName,
                        ScriptingEngine pScriptingEngine,
                        File pDefaultFile,
                        int pNumberOfRows,
                        int pNumberOfCols)
  {
    super();
    mScriptingEngine = pScriptingEngine;

    String lFileName =
                     String.format("/.rtlib/%s.scriptname.txt", pName)
                           .replaceAll("[^\\.\\/\\w]+", "_");
    mLastLoadedScriptFile = new File(System.getProperty("user.home"),
                                     lFileName);
    mLastLoadedScriptFile.getParentFile().mkdirs();

    new DropTarget(this, this);

    mRSyntaxTextArea = new RSyntaxTextArea(pNumberOfRows,
                                           pNumberOfCols);

    new DropTarget(mRSyntaxTextArea, this);

    if (pScriptingEngine != null)
    {
      if (pScriptingEngine.getScriptingLanguageInterface() instanceof GroovyScripting)
        mRSyntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
      else if (pScriptingEngine.getScriptingLanguageInterface() instanceof JythonScripting)
        mRSyntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);

      pScriptingEngine.addListener(new ScriptingEngineListener()
      {

        @Override
        public void updatedScript(ScriptingEngine pScriptingEngine,
                                  String pScript)
        {
          try
          {
            loadText(pScript);
          }
          catch (InvocationTargetException e)
          {
            e.printStackTrace();
          }
        }

        @Override
        public void beforeScriptExecution(ScriptingEngine pScriptingEngine,
                                          String pScriptString)
        {
          // TODO Auto-generated method stub

        }

        @Override
        public void asynchronousResult(ScriptingEngine pScriptingEngine,
                                       String pScriptString,
                                       Map<String, Object> pBinding,
                                       Throwable pThrowable,
                                       String pErrorMessage)
        {
          if (pErrorMessage != null)
          {
            final PrintStream lPrintStream =
                                           new PrintStream(pScriptingEngine.getOutputStream());
            lPrintStream.println(pErrorMessage);
          }
        }

        @Override
        public void afterScriptExecution(ScriptingEngine pScriptingEngine,
                                         String pScriptString)
        {
          // TODO Auto-generated method stub

        }

        @Override
        public void scriptAlreadyExecuting(ScriptingEngine pScriptingEngine)
        {
          mConsoleTextArea.append("!! Script is already running, try to cancel first !!\n");
        }
      });
    }
    else
      mRSyntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);

    mRSyntaxTextArea.setCodeFoldingEnabled(true);
    mRSyntaxTextArea.setTabSize(2);
    mRSyntaxTextArea.setPaintTabLines(true);
    mRSyntaxTextArea.setTabLineColor(Color.LIGHT_GRAY);
    mRSyntaxTextArea.getDocument().addDocumentListener(this);
    mRSyntaxTextArea.addKeyListener(this);

    final RTextScrollPane lRTextScrollPane =
                                           new RTextScrollPane(mRSyntaxTextArea);

    setLayout(new MigLayout("insets 0",
                            "[84.00px,grow][49px,grow]",
                            "[][][grow]"));

    if (pDefaultFile != null)
      mCurrentFileTextField =
                            new JTextField(pDefaultFile.getAbsolutePath());
    else
      mCurrentFileTextField = new JTextField();

    new DropTarget(mCurrentFileTextField, this);
    add(mCurrentFileTextField, "cell 0 1 2 1,growx");
    mCurrentFileTextField.setColumns(10);
    mCurrentFileTextField.addActionListener((event) -> {
      try
      {
        File lFile = new File(mCurrentFileTextField.getText());
        mScriptingEngine.setScriptName(lFile.getName());
        loadFromFile(mCurrentFileTextField.getText());
      }
      catch (final Throwable e)
      {
        e.printStackTrace();
      }
    });

    final JSplitPane lSplitPane = new JSplitPane();
    lSplitPane.setResizeWeight(0.75);
    lSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    add(lSplitPane, "cell 0 2 2 1,grow");

    final JPanel lExecuteButtonAndConsolePanel = new JPanel();
    lSplitPane.setLeftComponent(lRTextScrollPane);
    lSplitPane.setRightComponent(lExecuteButtonAndConsolePanel);
    lExecuteButtonAndConsolePanel.setLayout(new MigLayout("insets 0",
                                                          "[606px,grow,fill][606px,grow,fill][606px,grow,fill]",
                                                          "[29px][201px,grow,fill]"));

    final JButton lExecuteButton = new JButton("execute");
    lExecuteButtonAndConsolePanel.add(lExecuteButton,
                                      "cell 0 0,alignx left,aligny center");

    final JButton lCancelButton = new JButton("cancel");
    lExecuteButtonAndConsolePanel.add(lCancelButton, "cell 1 0");

    final JButton lConsoleClearButton = new JButton("clear");
    lExecuteButtonAndConsolePanel.add(lConsoleClearButton,
                                      "cell 2 0");

    mConsoleTextArea = new JTextArea();
    ((AbstractDocument) mConsoleTextArea.getDocument()).setDocumentFilter(new LineLimitedDocumentFilter(mConsoleTextArea,
                                                                                                        cMaxNumberOfLines));

    final JScrollPane lConsoleTextAreaScrollPane =
                                                 new JScrollPane(mConsoleTextArea);
    lExecuteButtonAndConsolePanel.add(lConsoleTextAreaScrollPane,
                                      "cell 0 1 3 1,alignx center,aligny center");

    if (mScriptingEngine != null)
    {
      final OutputStreamToJTextArea lOutputStreamToJTextArea =
                                                             new OutputStreamToJTextArea(mConsoleTextArea);
      mScriptingEngine.setOutputStream(lOutputStreamToJTextArea);

      lExecuteButton.addActionListener((event) -> {
        try
        {
          saveToFile(mRSyntaxTextArea.getText(),
                     mCurrentFileTextField.getText());

          if (mScriptingEngine.getScript() == null
              || !mScriptingEngine.getScript()
                                  .equals(mRSyntaxTextArea.getText()))
            mScriptingEngine.setScript(mRSyntaxTextArea.getText());

          mScriptingEngine.executeScriptAsynchronously();
        }
        catch (final Throwable e)
        {
          e.printStackTrace();
        }
      });

      lConsoleClearButton.addActionListener((e) -> {
        try
        {
          mConsoleTextArea.getDocument()
                          .remove(0,
                                  mConsoleTextArea.getDocument()
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

  private void saveToFile(String pScriptText,
                          String pFileName) throws IOException
  {
    try
    {
      if (mFileEventNotifier != null)
        mFileEventNotifier.setIgnore(true);
      if (pFileName == null || pFileName.isEmpty())
        return;
      final File lFile = new File(pFileName);
      final FileOutputStream lFileOutputStream =
                                               new FileOutputStream(lFile);
      IOUtils.write(pScriptText, lFileOutputStream);
      lFileOutputStream.close();

      if (mFileEventNotifier != null)
        mFileEventNotifier.setIgnore(false);
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
  }

  public void loadLastLoadedScriptFile()
  {
    try
    {
      if (mLastLoadedScriptFile.exists())
      {
        Scanner lScanner = new Scanner(mLastLoadedScriptFile);

        // If there is empty line, return
        if (!lScanner.hasNext())
          return;

        String lFileName = lScanner.nextLine();
        SwingUtilities.invokeAndWait(() -> mCurrentFileTextField.setText(lFileName));
        loadFromFile(lFileName);
        SwingUtilities.invokeAndWait(() -> mRSyntaxTextArea.requestFocusInWindow());
        lScanner.close();
      }
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
  }

  public void loadFromFile(String pFileName) throws IOException,
                                             InvocationTargetException
  {
    if (pFileName == null || pFileName.isEmpty())
      return;
    final File lFile = new File(pFileName);
    loadFromFile(lFile);
  }

  public void loadFromFile(File pFile) throws IOException,
                                       InvocationTargetException
  {
    if (pFile == null || !pFile.exists())
      return;
    final FileInputStream lFileInputStream =
                                           new FileInputStream(pFile);
    final String lString = IOUtils.toString(lFileInputStream);
    lFileInputStream.close();
    loadText(lString);

    try
    {
      if (mFileEventNotifier != null)
        mFileEventNotifier.close();
      mFileEventNotifier = new FileEventNotifier(pFile);
      mFileEventNotifier.addFileEventListener(this);
      mFileEventNotifier.startMonitoring();

      writeLastLoadedScriptFile(pFile);
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
    }
  }

  private void writeLastLoadedScriptFile(File pFile)
  {
    try
    {
      Formatter lFormatter = new Formatter(mLastLoadedScriptFile);
      lFormatter.format("%s\n", pFile.getAbsolutePath());
      lFormatter.close();
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }

  }

  public void loadText(String pText) throws InvocationTargetException
  {

    SwingUtilities.invokeLater(() -> {
      if (mRSyntaxTextArea.getText() == null
          || !mRSyntaxTextArea.getText().equals(pText))
      {
        setUpdateCaret(false);
        mRSyntaxTextArea.setText(pText);
        setUpdateCaret(true);
      }
    });
  }

  public void setUpdateCaret(boolean pUpdate)
  {
    DefaultCaret caret = (DefaultCaret) mRSyntaxTextArea.getCaret();
    caret.setUpdatePolicy(pUpdate ? DefaultCaret.ALWAYS_UPDATE
                                  : DefaultCaret.NEVER_UPDATE);
  }

  @Override
  public void dragEnter(DropTargetDragEvent pDtde)
  {
  }

  @Override
  public void dragOver(DropTargetDragEvent pDtde)
  {
  }

  @Override
  public void dropActionChanged(DropTargetDragEvent pDtde)
  {
  }

  @Override
  public void dragExit(DropTargetEvent pDte)
  {
  }

  @Override
  public void drop(DropTargetDropEvent evt)
  {

    try
    {

      final Transferable lTransferable = evt.getTransferable();

      System.out.println("transferable=" + lTransferable);

      if (lTransferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
      {

        evt.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

        final List<File> lFileList =
                                   (List<File>) lTransferable.getTransferData(DataFlavor.javaFileListFlavor);

        evt.getDropTargetContext().dropComplete(true);

        System.out.println("lFileList=" + lFileList);

        if (lFileList.size() == 1)
        {
          SwingUtilities.invokeLater(() -> {
            final File lFile = lFileList.get(0);

            if (lFile.exists())
            {
              try
              {
                mCurrentFileTextField.setText(lFile.getAbsolutePath());
                loadFromFile(lFile);
              }
              catch (final Throwable e)
              {
                e.printStackTrace();
              }
            }
          });
        }

      }
      else
      {
        evt.rejectDrop();
      }

    }
    catch (final IOException e)
    {
      evt.rejectDrop();
    }
    catch (final UnsupportedFlavorException e)
    {
      evt.rejectDrop();
    }

  }

  @Override
  public void fileEvent(FileEventNotifier pThis,
                        File pFile,
                        FileEventKind pEventKind)
  {
    try
    {
      if (pEventKind == FileEventKind.Created
          || pEventKind == FileEventKind.Modified)
      {
        loadFromFile(pFile);
      }
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
    }

  }

  @Override
  public void insertUpdate(DocumentEvent pE)
  {
    try
    {
      if (!SystemUtils.IS_OS_WINDOWS)
      {
        saveToFile(mRSyntaxTextArea.getText(),
                   mCurrentFileTextField.getText());
      }
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
    }

  }

  @Override
  public void removeUpdate(DocumentEvent pE)
  {
    insertUpdate(pE);
  }

  @Override
  public void changedUpdate(DocumentEvent pE)
  {
    insertUpdate(pE);
  }

  @Override
  public void keyTyped(KeyEvent pE)
  {
    // TODO Auto-generated method stub
  }

  @Override
  public void keyPressed(KeyEvent pE)
  {
    try
    {
      if (pE.isControlDown() && pE.getKeyCode() == KeyEvent.VK_S)
        saveToFile(mRSyntaxTextArea.getText(),
                   mCurrentFileTextField.getText());
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }

  }

  @Override
  public void keyReleased(KeyEvent pE)
  {
    // TODO Auto-generated method stub
  }

}
