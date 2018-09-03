package clearcontrol.gui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import clearcontrol.core.variable.Variable;

public class JOpenFileChooserButton extends JButton
{
  private static final File cDefaultFolder =
                                           new File(System.getProperty("user.home"));
  private final Variable<File> mFileVariable =
                                             new Variable<File>("FileVariable");
  private final boolean mOnlyFolders;

  public JOpenFileChooserButton(final String pLabel,
                                final boolean pOnlyFolders)
  {
    this(cDefaultFolder, pLabel, pOnlyFolders);
  }

  public JOpenFileChooserButton(final File pCurrentFolder,
                                final String pLabel,
                                final boolean pOnlyFolders)
  {
    super(pLabel);
    mFileVariable.set(pCurrentFolder);
    mOnlyFolders = pOnlyFolders;

    addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent pE)
      {
        final File lSelectedFile = openFileChooser();
        mFileVariable.set(lSelectedFile);
      }
    });

  }

  private File openFileChooser()
  {
    File lCurrentFolder = mFileVariable.get();
    if (lCurrentFolder == null)
    {
      lCurrentFolder = cDefaultFolder;
    }
    final JFileChooser lJFileChooser =
                                     new JFileChooser(lCurrentFolder);
    if (mOnlyFolders)
    {
      lJFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }
    final Integer lOption = lJFileChooser.showSaveDialog(this);

    if (lOption == JFileChooser.APPROVE_OPTION)
    {
      return lJFileChooser.getSelectedFile();
    }
    else
    {
      return null;
    }
  }

  public Variable<File> getSelectedFileVariable()
  {
    return mFileVariable;
  }

}
