package net.imglib2.img.utils;

import coremem.ContiguousMemoryInterface;
import coremem.offheap.OffHeapMemory;
import coremem.offheap.OffHeapMemoryAccess;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.img.planar.OffHeapPlanarImgFactory;
import net.imglib2.img.planar.PlanarRandomAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.Views;

import java.util.Random;

/**
 * Created by dibrov on 10/03/17.
 */
public class Copy {
    public static OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> copy(OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> pSourceImg,
                                                                               long[] pInitLocation,
                                                                               long[] pDimensions, double pFactor,
                                                                               int pNoiseAmpl) {

        long lSourceDimX = pSourceImg.dimension(0);
        long lSourceDimY = pSourceImg.dimension(1);
        long lSourceDimZ = pSourceImg.dimension(2);

        if (pInitLocation.length != 3) {
            throw new IllegalArgumentException("Location vector should be 3D.");
        }
        if (pDimensions.length != 3) {
            throw new IllegalArgumentException("Dimensions vector should be 3D.");
        }
        if (pDimensions[0] == 0 & pDimensions[1] == 0
                & pDimensions[2] == 0) {
            throw new IllegalArgumentException("Can't copy a substack with all dimenstions set to zero.");
        }
        if (pInitLocation[0] + pDimensions[0] >= lSourceDimX
                || pInitLocation[1] + pDimensions[1] >= lSourceDimY
                || pInitLocation[2] + pDimensions[2] >= lSourceDimZ) {
            throw new IllegalArgumentException("Substack sims to be bigger than the initial image.");
        }

        UnsignedShortType usti = new UnsignedShortType(pSourceImg);
        pSourceImg.setLinkedType(usti);

        // OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> imgOut =
        // (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) new
        // OffHeapPlanarImgFactory<>()
        // .createShortInstance(pDimensions, new Fraction());
        OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> imgOut =
                null;
        ContiguousMemoryInterface contOut =
                new OffHeapMemory("memmm",
                        imgOut,
                        OffHeapMemoryAccess.allocateMemory(2
                                * pDimensions[0]
                                * pDimensions[1]
                                * pDimensions[2]),
                        2 * pDimensions[0]
                                * pDimensions[1]
                                * pDimensions[2]);

        imgOut =
                (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) new OffHeapPlanarImgFactory().createShortInstance(contOut,
                        pDimensions,
                        new UnsignedShortType());

        UnsignedShortType usto = new UnsignedShortType(imgOut);
        imgOut.setLinkedType(usto);

        System.out.println("imgOut dims: " + imgOut.dimension(0)
                + " "
                + imgOut.dimension(1)
                + " "
                + imgOut.dimension(2));
        IterableInterval img = Views.offsetInterval(pSourceImg,
                pInitLocation,
                pDimensions);
        Cursor<UnsignedShortType> lCursor = img.localizingCursor();
        PlanarRandomAccess<UnsignedShortType> lRa = imgOut.randomAccess();

        if (pNoiseAmpl == 0.0) {
            while (lCursor.hasNext()) {
                lCursor.fwd();
                lRa.setPosition(lCursor);
                // System.out.println("init img: " + lCursor.get() + "pos: " +
                // lCursor.getIntPosition(0) + " " + lCursor
                // .getIntPosition(1) + " " + lCursor.getIntPosition(2));
                lRa.get().set((short)(pFactor*lCursor.get().get()));
                // System.out.println("just put: " + lRa.get().getInteger());
            }
        }
        else{
            Random rand = new Random();
            while (lCursor.hasNext()) {
                lCursor.fwd();
                lRa.setPosition(lCursor);
                // System.out.println("init img: " + lCursor.get() + "pos: " +
                // lCursor.getIntPosition(0) + " " + lCursor
                // .getIntPosition(1) + " " + lCursor.getIntPosition(2));
                lRa.get().set((short)(pFactor*lCursor.get().get() + pNoiseAmpl*rand.nextDouble()));
                // System.out.println("just put: " + lRa.get().getInteger());
            }
        }
        System.out.println("finish copy with factor:" + pFactor);
        return imgOut;
    }
}
