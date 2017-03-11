package clearcontrol.simulation;

import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackProvider;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import net.imglib2.img.Img;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.img.planar.OffHeapPlanarImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import org.math.plot.utils.Array;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by dibrov on 10/03/17.
 */
public class BasicSampleSimulator implements SampleSimulatorInterface {

    private long[] mCurrentPosition = {0, 0, 0};
    private long[] mCurrentDimensions = {0, 0, 0};
    private long mCurrentTimeStep = 0;

    private long mNumberOfTimeSteps;
    private long[] mDimensions = {0,0,0};


    private LinkedList<OffHeapPlanarStack> mTimeLapse;

    public BasicSampleSimulator(LinkedList<OffHeapPlanarStack> pTimeLapse) {
        if (pTimeLapse == null & pTimeLapse.isEmpty()) {
            throw new IllegalArgumentException("Can't create a Simulator with an empty timelapse!");
        }
        mTimeLapse = pTimeLapse;
        mNumberOfTimeSteps = pTimeLapse.size();
        mDimensions[0] = mTimeLapse.get(0).getWidth();
        mDimensions[1] = mTimeLapse.get(0).getHeight();
        mDimensions[2] = mTimeLapse.get(0).getDepth();
    }

    @Override
    public long[] getDimensions() {
        return Arrays.copyOf(mDimensions, mDimensions.length);
    }

    private StackProvider mStackProvider = new StackProvider() {
        @Override
        public StackInterface getStack() {
            return getCurrentStack();
        }
    };

    private StackInterface getCurrentStack() {
        return getSubstack(mCurrentPosition, mCurrentDimensions, mCurrentTimeStep);
    }

    private StackInterface getSubstack(long[] pPosition, long[] pDimensions, long pTimeStep) {
        if (pTimeStep > mNumberOfTimeSteps) {
            throw  new ArrayIndexOutOfBoundsException("The simulated timelapse is " + mNumberOfTimeSteps + " " +
                    "stacks long. You requested a stack on time step " + pTimeStep);
        }
        if (pPosition.length != 3) {
            throw  new IllegalArgumentException("Position vector is supposed to be 3D. You provided: " + pPosition.length);
        }
        if (pPosition[0] < 0 || pPosition[0] >= mDimensions[0]) {
            throw new IllegalArgumentException("Wrong X coordinate for substack. Should be within the range of: [" +
                    0 + ", " + mDimensions[0] + "]." );
        }
        if (pPosition[1] < 0 || pPosition[1] >= mDimensions[1]) {
            throw new IllegalArgumentException("Wrong Y coordinate for substack. Should be within the range of: [" +
                    0 + ", " + mDimensions[1] + "]." );
        }
        if (pPosition[2] < 0 || pPosition[2] >= mDimensions[2]) {
            throw new IllegalArgumentException("Wrong Z coordinate for substack. Should be within the range of: [" +
                    0 + ", " + mDimensions[2] + "]." );
        }
        if (pDimensions.length != 3) {
            throw  new IllegalArgumentException("Dimensions vector is supposed to be 3D. You provided: " + pDimensions
                    .length);
        }


//        return new OffHeapPlanarStack(0, 0, );
        return null;
    }

    public BasicSampleSimulator getBasicSampleSimulatorWithASingleStackFromATIFFFile(String pPathToFile){
        try {

//            OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> img1 = new OffHeapPlanarImgFactory().cre
//            OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> img = (Img<UnsignedShortType>) (new ImgOpener().openImgs
//                    (pPathToFile).get
//                    (0));
//            return new SampleSpace(0, img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public StackProvider getStackProvider(long pIndex) {
        return mStackProvider;
    }


}
