package gui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import variable.objectv.ObjectVariable;

public class JOpenFileChooserButton extends JButton
{
	private static final File cDefaultFolder = new File(System.getProperty("user.home"));
	private ObjectVariable<File> mFileVariable = new ObjectVariable<File>();
	private boolean mOnlyFolders;

	public JOpenFileChooserButton(String pLabel, boolean pOnlyFolders)
	{
		this(cDefaultFolder, pLabel, pOnlyFolders);
	}

	public JOpenFileChooserButton(File pCurrentFolder,
																String pLabel,
																boolean pOnlyFolders)
	{
		super(pLabel);
		mFileVariable.setReference(pCurrentFolder);
		mOnlyFolders = pOnlyFolders;

		addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pE)
			{
				final File lSelectedFile = openFileChooser();
				mFileVariable.setReference(lSelectedFile);
			}
		});

	}

	private File openFileChooser()
	{
		File lCurrentFolder = mFileVariable.getReference();
		if (lCurrentFolder == null)
			lCurrentFolder = cDefaultFolder;
		JFileChooser lJFileChooser = new JFileChooser();
		if (mOnlyFolders)
			lJFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		Integer lOption = lJFileChooser.showSaveDialog(this);

		if (lOption == JFileChooser.APPROVE_OPTION)
			return lJFileChooser.getSelectedFile();
		else
			return null;
	}

	public ObjectVariable<File> getSelectedFileVariable()
	{
		return mFileVariable;
	}

}
