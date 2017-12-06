package clearcontrol.gui.video.video2d;

import clearcl.ClearCLImage;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import coremem.ContiguousMemoryInterface;
import fastfuse.stackgen.StackGenerator;

import java.util.HashMap;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * December 2017
 */
public class Stack2DSumDisplay extends Stack2DDisplay
{
  public Stack2DSumDisplay(String pTitle, int pWidth, int pHeight) {
    super(pTitle, pWidth, pHeight, false);
  }

  HashMap<Integer, Variable<StackInterface>> mStackVariables = new HashMap<>();

  StackInterface mSumStack = null;

  public Variable<StackInterface> getInputStackVariable(int pDetectionArmIndex)
  {
    System.out.println("Getting input stack variable for " + pDetectionArmIndex);
    if (!mStackVariables.keySet().contains(pDetectionArmIndex)) {

      System.out.println("Setting up input stack variable for " + pDetectionArmIndex);
      Variable<StackInterface> lStackInterfaceVariable = new Variable<StackInterface>("detectionarm" + pDetectionArmIndex);
      lStackInterfaceVariable.addSetListener(new VariableSetListener<StackInterface>()
      {
        @Override public void setEvent(StackInterface pCurrentValue,
                                       StackInterface pNewValue)
        {
          viewChanged();
        }
      });
      mStackVariables.put(pDetectionArmIndex, lStackInterfaceVariable);
    }
    return mStackVariables.get(pDetectionArmIndex);
  }

  private void viewChanged() {

    System.out.println("View changed am I visible?");

    if (!isVisible()) {
      return;
    }

    System.out.println("View changed");

    StackInterface lFirstStack = getFirstStack();

    if (mSumStack == null || mSumStack.getWidth() != lFirstStack.getWidth() || mSumStack.getHeight() != lFirstStack.getHeight() || mSumStack
        .getDepth() != lFirstStack.getDepth()) {
      if (lFirstStack == null) {
        return;
      }
      if (mSumStack != null) {
        mSumStack.free();
      }
      mSumStack = lFirstStack.allocateSameSize();
    }

    int lBytesPerPixel = 2;

    ContiguousMemoryInterface lSumStackMemory = mSumStack.getContiguousMemory();

    boolean firstRound = true;
    for (Integer key : mStackVariables.keySet())
    {
      StackInterface lAnyStack = mStackVariables.get(key).get();

      ContiguousMemoryInterface lAnyMemory = lAnyStack.getContiguousMemory();

      for (int i = 0; i < lAnyMemory.getSizeInBytes() / lBytesPerPixel; i ++) {
        if (firstRound) {
          lSumStackMemory.setShort(i, lAnyMemory.getShort(i));
        } else {
          lSumStackMemory.setShort(i, (short)(lSumStackMemory.getShort(i) + lAnyMemory.getShort(i)));
        }
      }
      firstRound = false;
    }

    super.getInputStackVariable().set(mSumStack);
  }

  private StackInterface getFirstStack() {
    for (Integer key : mStackVariables.keySet()) {
      StackInterface lStackInterface = mStackVariables.get(key).get();
      if (lStackInterface != null) {
        return lStackInterface;
      }
    }
    return null;
  }
}
