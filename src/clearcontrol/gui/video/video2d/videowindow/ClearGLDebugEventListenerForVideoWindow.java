package clearcontrol.gui.video.video2d.videowindow;

import java.io.IOException;

import cleargl.ClearGLDefaultEventListener;
import cleargl.ClearGLWindow;
import cleargl.GLAttribute;
import cleargl.GLFloatArray;
import cleargl.GLProgram;
import cleargl.GLTexture;
import cleargl.GLTypeEnum;
import cleargl.GLUniform;
import cleargl.GLVertexArray;
import cleargl.GLVertexAttributeArray;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;

import coremem.ContiguousMemoryInterface;
import coremem.enums.NativeTypeEnum;
import coremem.offheap.OffHeapMemory;
import coremem.util.Size;

final class ClearGLDebugEventListenerForVideoWindow extends
                                                    ClearGLDefaultEventListener
{

  private GLProgram mGLProgramVideoRender;
  private GLAttribute mPositionAttribute;

  private GLAttribute mTexCoordAttribute;
  private GLUniform mTexUnit;

  private GLUniform mMinimumUniform;
  private GLUniform mMaximumUniform;
  private GLUniform mGammaUniform;
  private GLUniform mProjectionMatrix;

  private GLVertexArray mQuadVertexArray;
  private GLVertexAttributeArray mPositionAttributeArray;

  private GLVertexAttributeArray mTexCoordAttributeArray;
  private GLTexture mTexture;

  private GLProgram mGLProgramGuides;
  private GLAttribute mGuidesPositionAttribute;
  private GLVertexAttributeArray mXLinesPositionAttributeArray;
  private GLVertexAttributeArray mGridPositionAttributeArray;
  private GLVertexArray mXLinesGuidesVertexArray;
  private GLVertexArray mGridGuidesVertexArray;

  /**
   * 
   */
  private final VideoWindow mVideoWindow;
  private boolean mFlipX;

  /**
   * Constructs an instance of the ClearGLDebugEventListenerForVideoWindow class
   * 
   * @param pVideoWindow
   */
  ClearGLDebugEventListenerForVideoWindow(VideoWindow pVideoWindow,
                                          final boolean pFlipX)
  {
    mVideoWindow = pVideoWindow;
    mFlipX = pFlipX;
  }

  @Override
  public void init(final GLAutoDrawable pGLAutoDrawable)
  {
    super.init(pGLAutoDrawable);
    try
    {
      final GL lGL = pGLAutoDrawable.getGL();
      lGL.setSwapInterval(1);
      lGL.glDisable(GL.GL_DEPTH_TEST);
      lGL.glDisable(GL.GL_STENCIL_TEST);
      lGL.glEnable(GL.GL_TEXTURE_2D);

      lGL.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
      lGL.glClear(GL.GL_COLOR_BUFFER_BIT);

      final int lWidth = pGLAutoDrawable.getSurfaceWidth();
      final int lHeight = pGLAutoDrawable.getSurfaceHeight();

      setOrthoProjectionMatrixWithAspectRatio(lWidth, lHeight, 1, 1);

      mGLProgramVideoRender =
                            GLProgram.buildProgram(lGL,
                                                   VideoWindow.class,
                                                   "shaders/video.vertex.glsl",
                                                   "shaders/video.fragment.glsl");

      mPositionAttribute =
                         mGLProgramVideoRender.getAttribute("position");
      mTexCoordAttribute =
                         mGLProgramVideoRender.getAttribute("texcoord");
      mTexUnit = mGLProgramVideoRender.getUniform("texUnit");
      mTexUnit.setInt(0);

      mMinimumUniform = mGLProgramVideoRender.getUniform("minimum");
      mMaximumUniform = mGLProgramVideoRender.getUniform("maximum");
      mGammaUniform = mGLProgramVideoRender.getUniform("gamma");
      mProjectionMatrix =
                        mGLProgramVideoRender.getUniform("projection");

      mQuadVertexArray = new GLVertexArray(mGLProgramVideoRender);
      mQuadVertexArray.bind();
      mPositionAttributeArray =
                              new GLVertexAttributeArray(mPositionAttribute,
                                                         4);

      final GLFloatArray lQuadVerticesFloatArray =
                                                 new GLFloatArray(6,
                                                                  4);
      lQuadVerticesFloatArray.add(-1, -1, 0, 1);
      lQuadVerticesFloatArray.add(1, -1, 0, 1);
      lQuadVerticesFloatArray.add(1, 1, 0, 1);
      lQuadVerticesFloatArray.add(-1, -1, 0, 1);
      lQuadVerticesFloatArray.add(1, 1, 0, 1);
      lQuadVerticesFloatArray.add(-1, 1, 0, 1);

      mQuadVertexArray.addVertexAttributeArray(mPositionAttributeArray,
                                               lQuadVerticesFloatArray.getFloatBuffer());

      mTexCoordAttributeArray =
                              new GLVertexAttributeArray(mTexCoordAttribute,
                                                         2);

      final GLFloatArray lTexCoordFloatArray = new GLFloatArray(6, 2);
      lTexCoordFloatArray.add(mFlipX ? 1 : 0, 0);
      lTexCoordFloatArray.add(mFlipX ? 0 : 1, 0);
      lTexCoordFloatArray.add(mFlipX ? 0 : 1, 1);
      lTexCoordFloatArray.add(mFlipX ? 1 : 0, 0);
      lTexCoordFloatArray.add(mFlipX ? 0 : 1, 1);
      lTexCoordFloatArray.add(mFlipX ? 1 : 0, 1);

      initializeTexture(mVideoWindow.mVideoWidth,
                        mVideoWindow.mVideoHeight);

      mQuadVertexArray.addVertexAttributeArray(mTexCoordAttributeArray,
                                               lTexCoordFloatArray.getFloatBuffer());

      mGLProgramGuides =
                       GLProgram.buildProgram(lGL,
                                              VideoWindow.class,
                                              "shaders/guides.vertex.glsl",
                                              "shaders/guides.fragment.glsl");

      mGuidesPositionAttribute =
                               mGLProgramGuides.getAttribute("position");

      mXLinesPositionAttributeArray =
                                    new GLVertexAttributeArray(mGuidesPositionAttribute,
                                                               4);
      mXLinesGuidesVertexArray = new GLVertexArray(mGLProgramGuides);
      mXLinesGuidesVertexArray.bind();

      final GLFloatArray lXlinesGuidesVerticesFloatArray =
                                                         new GLFloatArray(4,
                                                                          4);
      lXlinesGuidesVerticesFloatArray.add(-1, -1, 0, 1);
      lXlinesGuidesVerticesFloatArray.add(+1, +1, 0, 1);
      lXlinesGuidesVerticesFloatArray.add(-1, +1, 0, 1);
      lXlinesGuidesVerticesFloatArray.add(+1, -1, 0, 1);

      mXLinesGuidesVertexArray.addVertexAttributeArray(mXLinesPositionAttributeArray,
                                                       lXlinesGuidesVerticesFloatArray.getFloatBuffer());

      mGridPositionAttributeArray =
                                  new GLVertexAttributeArray(mGuidesPositionAttribute,
                                                             4);
      mGridGuidesVertexArray = new GLVertexArray(mGLProgramGuides);
      mGridGuidesVertexArray.bind();

      final GLFloatArray lGridGuidesVerticesFloatArray =
                                                       new GLFloatArray(12,
                                                                        4);
      final float lRatio = 0.5f;

      lGridGuidesVerticesFloatArray.add(-1.0f, 0, 0, 1.0f);
      lGridGuidesVerticesFloatArray.add(+1.0f, 0, 0, 1.0f);

      lGridGuidesVerticesFloatArray.add(0, -1.0f, 0, 1.0f);
      lGridGuidesVerticesFloatArray.add(0, +1.0f, 0, 1.0f);

      lGridGuidesVerticesFloatArray.add(-1.0f, -lRatio, 0.0f, 1.0f);
      lGridGuidesVerticesFloatArray.add(+1.0f, -lRatio, 0.0f, 1.0f);
      lGridGuidesVerticesFloatArray.add(-1.0f, +lRatio, 0.0f, 1.0f);
      lGridGuidesVerticesFloatArray.add(+1.0f, +lRatio, 0.0f, 1.0f);
      lGridGuidesVerticesFloatArray.add(-lRatio, -1.0f, 0.0f, 1.0f);
      lGridGuidesVerticesFloatArray.add(-lRatio, +1.0f, 0.0f, 1.0f);
      lGridGuidesVerticesFloatArray.add(+lRatio, -1.0f, 0.0f, 1.0f);
      lGridGuidesVerticesFloatArray.add(+lRatio, +1.0f, 0.0f, 1.0f);

      mGridGuidesVertexArray.addVertexAttributeArray(mGridPositionAttributeArray,
                                                     lGridGuidesVerticesFloatArray.getFloatBuffer());

    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }

  }

  private void initializeTexture(int pTextureWidth,
                                 int pTextureHeight)
  {
    if (mTexture != null)
      mTexture.close();

    GLTypeEnum lGLType = null;

    if (mVideoWindow.mType == NativeTypeEnum.Byte)
      lGLType = GLTypeEnum.Byte;
    else if (mVideoWindow.mType == NativeTypeEnum.UnsignedByte)
      lGLType = GLTypeEnum.UnsignedByte;
    else if (mVideoWindow.mType == NativeTypeEnum.Short)
      lGLType = GLTypeEnum.Short;
    else if (mVideoWindow.mType == NativeTypeEnum.UnsignedShort)
      lGLType = GLTypeEnum.UnsignedShort;
    else if (mVideoWindow.mType == NativeTypeEnum.Int)
      lGLType = GLTypeEnum.Int;
    else if (mVideoWindow.mType == NativeTypeEnum.UnsignedInt)
      lGLType = GLTypeEnum.UnsignedInt;
    else if (mVideoWindow.mType == NativeTypeEnum.Float)
      lGLType = GLTypeEnum.Float;
    else if (mVideoWindow.mType == NativeTypeEnum.Double)
      lGLType = GLTypeEnum.Float;

    mTexture = new GLTexture(mGLProgramVideoRender,
                             lGLType,
                             1,
                             pTextureWidth,
                             pTextureHeight,
                             1,
                             true,
                             VideoWindow.cMipMapLevel);

    mTexture.clear();
  }

  @Override
  public void reshape(final GLAutoDrawable pGLAutoDrawable,
                      final int x,
                      final int y,
                      final int pWindowWidth,
                      final int pWindowHeight)
  {
    super.reshape(pGLAutoDrawable, x, y, pWindowWidth, pWindowHeight);
    mVideoWindow.mEffectiveWindowWidth = pWindowWidth;
    mVideoWindow.mEffectiveWindowHeight = pWindowHeight;

    final int lDisplayWidth = pWindowWidth;
    final int lDisplayHeight = pWindowHeight;

    final int lImageWidth = mVideoWindow.mSourceBufferWidth;
    final int lImageHeight = mVideoWindow.mSourceBufferHeight;

    setOrthoProjectionMatrixWithAspectRatio(lDisplayWidth,
                                            lDisplayHeight,
                                            lImageWidth,
                                            lImageHeight);

  }

  private void setOrthoProjectionMatrixWithAspectRatio(final int pDisplayWidth,
                                                       final int pDisplayHeight,
                                                       int pImageWidth,
                                                       int pImageHeight)
  {
    if (pImageWidth == 0)
      pImageWidth = 1;
    if (pImageHeight == 0)
      pImageHeight = 1;

    final float lAspectRatio = (1.0f * pDisplayWidth * pImageHeight)
                               / (pDisplayHeight * pImageWidth);

    if (lAspectRatio >= 1)
      getClearGLWindow().setOrthoProjectionMatrix(-lAspectRatio,
                                                  lAspectRatio,
                                                  -1,
                                                  1,
                                                  0,
                                                  1);
    else
      getClearGLWindow().setOrthoProjectionMatrix(-1,
                                                  1,
                                                  -(1 / lAspectRatio),
                                                  1 / lAspectRatio,
                                                  0,
                                                  1);/**/
  }

  @Override
  public void display(final GLAutoDrawable pGLAutoDrawable)
  {
    try
    {
      super.display(pGLAutoDrawable);
      final GL lGL = pGLAutoDrawable.getGL().getGL();

      // System.out.println("DISPLAY");
      if (!mVideoWindow.mDisplayOn)
        return;

      if (mVideoWindow.mSourceBuffer != null)
      {
        mVideoWindow.mSendBufferLock.lock();
        {
          final int lBufferWidth = mVideoWindow.mSourceBufferWidth;
          final int lBufferHeight = mVideoWindow.mSourceBufferHeight;
          final ContiguousMemoryInterface lSourceBuffer =
                                                        mVideoWindow.mSourceBuffer;
          mVideoWindow.mSourceBuffer = null;

          if (mVideoWindow.mVideoWidth != lBufferWidth
              || mVideoWindow.mVideoHeight != lBufferHeight
              || mTexture.getWidth() != lBufferWidth
              || mTexture.getHeight() != lBufferHeight)
          {
            mVideoWindow.mVideoWidth = lBufferWidth;
            mVideoWindow.mVideoHeight = lBufferHeight;
            initializeTexture(mVideoWindow.mVideoWidth,
                              mVideoWindow.mVideoHeight);
          }

          if (!mVideoWindow.mMinMaxFixed)
            mVideoWindow.fastMinMaxSampling(lSourceBuffer);
          final ContiguousMemoryInterface lConvertedBuffer =
                                                           convertBuffer(lSourceBuffer,
                                                                         lBufferWidth,
                                                                         lBufferHeight);

          mTexture.copyFrom(lConvertedBuffer.getByteBuffer());
          mVideoWindow.mNotifyBufferCopy.countDown();

        }
        mVideoWindow.mSendBufferLock.unlock();
      }

      {
        if (mVideoWindow.mManualMinMax)
        {
          mMinimumUniform.setFloat((float) mVideoWindow.mMinIntensity);
          mMaximumUniform.setFloat((float) mVideoWindow.mMaxIntensity);
        }
        else
        {
          mMinimumUniform.setFloat((float) mVideoWindow.mSampledMinIntensity);
          mMaximumUniform.setFloat((float) mVideoWindow.mSampledMaxIntensity);
        }
        mGammaUniform.setFloat((float) mVideoWindow.mGamma);

        setOrthoProjectionMatrixWithAspectRatio(mVideoWindow.mEffectiveWindowWidth,
                                                mVideoWindow.mEffectiveWindowHeight,
                                                mVideoWindow.mVideoWidth,
                                                mVideoWindow.mVideoHeight);
        mProjectionMatrix.setFloatMatrix(getClearGLWindow().getProjectionMatrix()
                                                           .getFloatArray(),
                                         false);

        mGLProgramVideoRender.use(lGL);
        mTexture.bind(mGLProgramVideoRender);
        // System.out.println("DRAW");
        lGL.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        lGL.glClear(GL.GL_COLOR_BUFFER_BIT);

        mQuadVertexArray.draw(GL.GL_TRIANGLES);

        if (mVideoWindow.isDisplayLines())
        {
          mGLProgramGuides.bind();
          // mXLinesGuidesVertexArray.draw(GL.GL_LINES);

          mGridGuidesVertexArray.draw(GL.GL_LINES);
        }

      }
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }

  }

  private ContiguousMemoryInterface convertBuffer(ContiguousMemoryInterface pSourceBuffer,
                                                  int pBufferWidth,
                                                  int pBufferHeight)
  {

    if (mVideoWindow.mType == NativeTypeEnum.Double)
    {
      final int lLengthInFloats = pBufferWidth * pBufferHeight;
      if (mVideoWindow.mConversionBuffer == null
          || mVideoWindow.mConversionBuffer.getSizeInBytes() != lLengthInFloats
                                                                * Size.FLOAT)
      {
        if (mVideoWindow.mConversionBuffer != null)
          mVideoWindow.mConversionBuffer.free();

        mVideoWindow.mConversionBuffer =
                                       OffHeapMemory.allocateFloats(lLengthInFloats);
      }

      for (int i = 0; i < lLengthInFloats; i++)
      {
        final double lValue = pSourceBuffer.getDoubleAligned(i);
        mVideoWindow.mConversionBuffer.setFloatAligned(i,
                                                       (float) lValue);
      }

      return mVideoWindow.mConversionBuffer;
    }

    return pSourceBuffer;
  }

  @Override
  public void dispose(final GLAutoDrawable pGLAutoDrawable)
  {
    super.dispose(pGLAutoDrawable);
  }

  @Override
  public void setClearGLWindow(ClearGLWindow pClearGLWindow)
  {
    mVideoWindow.mClearGLWindow = pClearGLWindow;
  }

  @Override
  public ClearGLWindow getClearGLWindow()
  {
    return mVideoWindow.mClearGLWindow;
  }
}