package clearcontrol.microscope.lightsheet.stacks;

import clearcontrol.stack.metadata.MetaDataEntryInterface;

/**
 * Basic stack meta data entries
 *
 * @author royer
 */
@SuppressWarnings("javadoc")
public enum MetaDataViewFlags implements
                              MetaDataEntryInterface<Boolean>
{
 Camera0(Boolean.class),
 Camera1(Boolean.class),
 Camera2(Boolean.class),
 Camera3(Boolean.class),
 Camera4(Boolean.class),
 Camera5(Boolean.class),
 Camera6(Boolean.class),
 Camera7(Boolean.class),

 LightSheet0(Boolean.class),
 LightSheet1(Boolean.class),
 LightSheet2(Boolean.class),
 LightSheet3(Boolean.class),
 LightSheet4(Boolean.class),
 LightSheet5(Boolean.class),
 LightSheet6(Boolean.class),
 LightSheet7(Boolean.class);

  private final Class<Boolean> mClass;

  private MetaDataViewFlags(Class<Boolean> pClass)
  {
    mClass = pClass;
  }

  @Override
  public Class<Boolean> getMetaDataClass()
  {
    return mClass;
  }

  public static MetaDataViewFlags getLightSheet(int pLightSheetIndex)
  {
    switch (pLightSheetIndex)
    {
    case 0:
      return MetaDataViewFlags.LightSheet0;
    case 1:
      return MetaDataViewFlags.LightSheet1;
    case 2:
      return MetaDataViewFlags.LightSheet2;
    case 3:
      return MetaDataViewFlags.LightSheet3;
    case 4:
      return MetaDataViewFlags.LightSheet4;
    case 5:
      return MetaDataViewFlags.LightSheet5;
    case 6:
      return MetaDataViewFlags.LightSheet6;
    case 7:
      return MetaDataViewFlags.LightSheet7;
    default:
      throw new IllegalArgumentException("Lightsheet index must be within [0,7]");
    }
  }

  public static MetaDataViewFlags getCamera(int pCameraIndex)
  {
    switch (pCameraIndex)
    {
    case 0:
      return MetaDataViewFlags.Camera0;
    case 1:
      return MetaDataViewFlags.Camera1;
    case 2:
      return MetaDataViewFlags.Camera2;
    case 3:
      return MetaDataViewFlags.Camera3;
    case 4:
      return MetaDataViewFlags.Camera4;
    case 5:
      return MetaDataViewFlags.Camera5;
    case 6:
      return MetaDataViewFlags.Camera6;
    case 7:
      return MetaDataViewFlags.Camera7;
    default:
      throw new IllegalArgumentException("Camera index must be within [0,7]");
    }
  }

}
