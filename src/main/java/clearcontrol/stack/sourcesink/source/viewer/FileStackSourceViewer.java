package clearcontrol.stack.sourcesink.source.viewer;

import java.io.File;
import java.util.ArrayList;

import clearcontrol.core.variable.Variable;
import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.source.FileStackSource;
import coremem.recycling.BasicRecycler;

/**
 * File stack source viewer
 *
 * @author royer
 */
public class FileStackSourceViewer extends StackSourceViewer
{

  private final Variable<File> mRootFolderVariable =
                                                   new Variable<>("RootFolder",
                                                                  null);

  private final Variable<String> mDatasetNameVariable =
                                                      new Variable<>("DatasetName",
                                                                     null);

  /**
   * Instantiates a stack source
   * 
   * @param pRootFolder
   *          root folder
   * 
   */
  public FileStackSourceViewer(File pRootFolder)
  {
    super();

    final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory =
                                                                         new ContiguousOffHeapPlanarStackFactory();

    final BasicRecycler<StackInterface, StackRequest> lStackRecycler =
                                                                     new BasicRecycler<StackInterface, StackRequest>(lOffHeapPlanarStackFactory,
                                                                                                                     10);

    getRootFolderVariable().set(pRootFolder);

    ArrayList<String> lDataSetNamesList =
                                        getDataSetNamesList(pRootFolder);

    if (!lDataSetNamesList.isEmpty())
    {
      getDatasetNameVariable().set(lDataSetNamesList.get(0));

      updateFileStackSource(lStackRecycler,
                            getRootFolderVariable().get(),
                            getDatasetNameVariable().get());
    }

    getDatasetNameVariable().addSetListener((o, n) -> {
      if (n != o)
      {
        updateFileStackSource(lStackRecycler,
                              getRootFolderVariable().get(),
                              n);
      }
    });
  }

  /**
   * Returns dataset names list in a given root folder
   * 
   * @param pRootFolder
   *          root folder
   * @return list of dataset names
   */
  public ArrayList<String> getDataSetNamesList(File pRootFolder)
  {
    ArrayList<String> lListOfDatasets = new ArrayList<String>();

    for (File lFile : pRootFolder.listFiles())
    {
      lListOfDatasets.add(lFile.getName());
    }

    return lListOfDatasets;
  }

  protected void updateFileStackSource(final BasicRecycler<StackInterface, StackRequest> lStackRecycler,
                                       File pRootFolder,
                                       String pName)
  {
    FileStackSource lFileStackSource =
                                     new FileStackSource(lStackRecycler);
    lFileStackSource.setLocation(pRootFolder, pName);
    lFileStackSource.update();

    getStackSourceVariable().set(lFileStackSource);
  }

  /**
   * Returns the root folder variable
   * 
   * @return root folder variable
   */
  public Variable<File> getRootFolderVariable()
  {
    return mRootFolderVariable;
  }

  /**
   * Returns the dataset name variable
   * 
   * @return dataset name variable
   */
  public Variable<String> getDatasetNameVariable()
  {
    return mDatasetNameVariable;
  }

  @Override
  public void close()
  {
    super.close();
  }

}
