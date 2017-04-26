package clearcontrol.microscope.timelapse;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import clearcl.util.ElapsedTime;
import clearcontrol.core.device.task.LoopTaskDevice;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.gui.jfx.var.combo.enums.TimeUnitEnum;
import clearcontrol.microscope.MicroscopeInterface;
import clearcontrol.microscope.lightsheet.acquisition.AcquisitionType;
import clearcontrol.microscope.lightsheet.processor.MetaDataFusion;
import clearcontrol.microscope.stacks.metadata.MetaDataAcquisitionType;
import clearcontrol.microscope.stacks.metadata.MetaDataView;
import clearcontrol.microscope.timelapse.timer.TimelapseTimerInterface;
import clearcontrol.microscope.timelapse.timer.fixed.FixedIntervalTimelapseTimer;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.metadata.MetaDataChannel;
import clearcontrol.stack.sourcesink.StackSinkSourceInterface;
import clearcontrol.stack.sourcesink.sink.FileStackSinkInterface;

/**
 * Base implementation providing common fields and methods for all Timelapse
 * implementations ? extends FileStackSinkInterface
 * 
 * @author royer
 */
public abstract class TimelapseBase extends LoopTaskDevice
                                    implements TimelapseInterface
{
  private static final DateTimeFormatter sDateTimeFormatter =
                                                            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SS");

  private final MicroscopeInterface<?> mMicroscope;

  private final Variable<TimelapseTimerInterface> mTimelapseTimerVariable =
                                                                          new Variable<>("TimelapseTimer",
                                                                                         null);

  private final Variable<Boolean> mEnforceMaxNumberOfTimePointsVariable =
                                                                        new Variable<>("LimitNumberOfTimePoints",
                                                                                       true);

  private final Variable<Boolean> mEnforceMaxDurationVariable =
                                                              new Variable<>("LimitTimelapseDuration",
                                                                             false);

  private final Variable<Boolean> mEnforceMaxDateTimeVariable =
                                                              new Variable<>("LimitTimelapseDateTime",
                                                                             false);

  private final Variable<Long> mMaxNumberOfTimePointsVariable =
                                                              new Variable<Long>("MaxNumberOfTimePoints",
                                                                                 1000L);

  private final Variable<Long> mMaxDurationVariable =
                                                    new Variable<Long>("MaxDuration",
                                                                       24L);

  private final Variable<TimeUnitEnum> mMaxDurationUnitVariable =
                                                                new Variable<TimeUnitEnum>("MaxDurationUnit",
                                                                                           TimeUnitEnum.Hours);

  private final Variable<LocalDateTime> mMaxDateTimeVariable =
                                                             new Variable<LocalDateTime>("MaxDateTime",
                                                                                         LocalDateTime.now());

  private final Variable<LocalDateTime> mStartDateTimeVariable =
                                                               new Variable<LocalDateTime>("StartDateTime",
                                                                                           LocalDateTime.now());

  private final Variable<Long> mTimePointCounterVariable =
                                                         new Variable<Long>("TimePointCounter",
                                                                            1L);

  private final ArrayList<Class<? extends FileStackSinkInterface>> mFileStackSinkTypesList =
                                                                                           new ArrayList<>();

  private final Variable<Class<? extends FileStackSinkInterface>> mCurrentFileStackSinkTypeVariable =
                                                                                                    new Variable<>("CurrentFileStackSinkTypeVariable",
                                                                                                                   null);

  private final Variable<FileStackSinkInterface> mCurrentFileStackSinkVariable =
                                                                               new Variable<>("CurrentFileStackSink",
                                                                                              null);

  private final Variable<File> mRootFolderVariable =
                                                   new Variable<>("RootFolder",
                                                                  null);

  private final Variable<String> mDataSetNamePostfixVariable =
                                                             new Variable<>("DataSetNamePrefix",
                                                                            null);

  private final VariableSetListener<StackInterface> mStackListener;

  /**
   * Instantiates a timelapse with a given timelapse timer
   * 
   * @param pMicroscope
   *          microscope
   * 
   * @param pTimelapseTimer
   *          timelapse timer
   */
  public TimelapseBase(MicroscopeInterface<?> pMicroscope,
                       TimelapseTimerInterface pTimelapseTimer)
  {
    super("Timelapse");
    mMicroscope = pMicroscope;
    getTimelapseTimerVariable().set(pTimelapseTimer);

    getDataSetNamePostfixVariable().addSetListener((o, n) -> info(
                                                                  "New dataset name: %s \n",
                                                                  n));

    mStackListener = (o, n) -> {
      Variable<FileStackSinkInterface> lStackSinkVariable =
                                                          getCurrentFileStackSinkVariable();
      if (lStackSinkVariable != null && n != null
          && n.getMetaData()
              .getValue(MetaDataAcquisitionType.AcquisitionType) == AcquisitionType.TimeLapse)
      {
        info("Appending new stack %s to the file sink %s",
             n,
             lStackSinkVariable);

        String lChannelInMetaData =
                                  n.getMetaData()
                                   .getValue(MetaDataChannel.Channel);

        final String lChannel =
                              lChannelInMetaData != null ? lChannelInMetaData
                                                         : StackSinkSourceInterface.cDefaultChannel;

        ElapsedTime.measureForceOutput("TimeLapse stack saving",
                                       () -> lStackSinkVariable.get()
                                                               .appendStack(lChannel,
                                                                            n));

      }
    };
  }

  /**
   * Instantiates a timelapse with a fixed interval timer
   * 
   * @param pMicroscope
   *          microscope
   */
  public TimelapseBase(MicroscopeInterface<?> pMicroscope)
  {
    this(pMicroscope, new FixedIntervalTimelapseTimer());
  }

  /**
   * Returns the stack sink type list
   * 
   * @return stack sink type list
   */
  @Override
  public ArrayList<Class<? extends FileStackSinkInterface>> getFileStackSinkTypeList()
  {
    return mFileStackSinkTypesList;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void addFileStackSinkType(Class<?> pFileStackSinkType)
  {
    /*if (!pFileStackSinkType.toGenericString()
                           .contains(FileStackSinkInterface.class.getSimpleName()))
    {
      severe("Cannot add files stack sink type: %s must be of type Class<%s>. ",
             pFileStackSinkType.toGenericString(),
             FileStackSinkInterface.class.getSimpleName());
      return;
    }/**/

    mFileStackSinkTypesList.add((Class<? extends FileStackSinkInterface>) pFileStackSinkType);
  }

  @Override
  public void run()
  {

    if (!getIsRunningVariable().get())
    {
      getTimePointCounterVariable().set(0L);
      getStartDateTimeVariable().set(LocalDateTime.now());
    }

    if (getCurrentFileStackSinkTypeVariable().get() == null)
    {
      warning("No stack sink type defined!");

      if (getFileStackSinkTypeList().isEmpty())
      {
        severe("No stack sink types available! aborting timelapse acquisition!");
        return;
      }
      Class<? extends FileStackSinkInterface> lDefaultStackSink =
                                                                getFileStackSinkTypeList().get(0);
      warning("Using the first stack sink available: %s !",
              lDefaultStackSink);

      getCurrentFileStackSinkTypeVariable().set(lDefaultStackSink);
    }

    try
    {
      FileStackSinkInterface lStackSink =
                                        getCurrentFileStackSinkTypeVariable().get()
                                                                             .newInstance();

      if (mRootFolderVariable.get() == null)
      {
        severe("Root folder not defined.");
        return;
      }

      if (getDataSetNamePostfixVariable().get() == null)
        getDataSetNamePostfixVariable().set("");

      String lNowDateTimeString =
                                sDateTimeFormatter.format(LocalDateTime.now());

      lStackSink.setLocation(mRootFolderVariable.get(),
                             lNowDateTimeString + "-"
                                                        + getDataSetNamePostfixVariable().get());

      if (getCurrentFileStackSinkVariable().get() != null)
        try
        {
          getCurrentFileStackSinkVariable().get().close();
        }
        catch (Exception e)
        {
          severe("Error occured while closing stack sink: %s", e);
          e.printStackTrace();
        }

      getCurrentFileStackSinkVariable().set(lStackSink);

      getTimePointCounterVariable().set(0L);
      getTimelapseTimerVariable().get().reset();

      // This is where we actually start the loop, and we make sure to listen to
      // changes

      Variable<StackInterface> lPipelineStackVariable = null;
      if (mMicroscope != null)
      {
        lPipelineStackVariable =
                               mMicroscope.getPipelineStackVariable();
        lPipelineStackVariable.addSetListener(mStackListener);
      }
      super.run();
      if (mMicroscope != null)
        lPipelineStackVariable.removeSetListener(mStackListener);
    }
    catch (InstantiationException e)
    {
      severe("Cannot instanciate class %s (%s)",
             mCurrentFileStackSinkTypeVariable.get(),
             e.getMessage());
      return;
    }
    catch (IllegalAccessException e)
    {
      severe("Cannot access class %s (%s)",
             mCurrentFileStackSinkTypeVariable.get(),
             e.getMessage());
      return;
    }

  }

  @Override
  public boolean startTask()
  {

    return super.startTask();
  }

  @Override
  public void stopTask()
  {
    super.stopTask();
  }

  @Override
  public boolean loop()
  {
    if (getTimelapseTimerVariable() == null)
      return false;

    TimelapseTimerInterface lTimelapseTimer =
                                            getTimelapseTimerVariable().get();

    lTimelapseTimer.waitToAcquire(1, TimeUnit.DAYS);
    lTimelapseTimer.notifyAcquisition();
    acquire();

    getTimePointCounterVariable().increment();

    if (getEnforceMaxNumberOfTimePointsVariable().get())
      if (getTimePointCounterVariable().get() >= getMaxNumberOfTimePointsVariable().get())
        return false;

    if (getEnforceMaxDurationVariable().get()
        && getMaxDurationVariable().get() != null)
      if (checkMaxDuration())
        return false;

    if (getEnforceMaxDateTimeVariable().get()
        && getMaxDateTimeVariable().get() != null)
      if (checkMaxDateTime())
        return false;

    return true;
  }

  private boolean checkMaxDuration()
  {
    LocalDateTime lStartDateTime = getStartDateTimeVariable().get();

    Duration lDuration = Duration.between(lStartDateTime,
                                          LocalDateTime.now());

    long lCurrentlMeasuredDurationInNanos = lDuration.toNanos();

    long lMaxDurationInNanos =
                             TimeUnit.NANOSECONDS.convert(getMaxDurationVariable().get(),
                                                          getMaxDurationUnitVariable().get()
                                                                                      .getTimeUnit());

    long lTimeLeft = lMaxDurationInNanos
                     - lCurrentlMeasuredDurationInNanos;

    boolean lTimeIsOut = lTimeLeft < 0;

    return lTimeIsOut;
  }

  private boolean checkMaxDateTime()
  {
    LocalDateTime lMaxDateTime = getMaxDateTimeVariable().get();
    LocalDateTime lNowDateTime = LocalDateTime.now();

    return lNowDateTime.isAfter(lMaxDateTime);
  }

  @Override
  public abstract void acquire();

  @Override
  public Variable<TimelapseTimerInterface> getTimelapseTimerVariable()
  {
    return mTimelapseTimerVariable;
  }

  @Override
  public Variable<Boolean> getEnforceMaxNumberOfTimePointsVariable()
  {
    return mEnforceMaxNumberOfTimePointsVariable;
  }

  @Override
  public Variable<Boolean> getEnforceMaxDurationVariable()
  {
    return mEnforceMaxDurationVariable;
  }

  @Override
  public Variable<Boolean> getEnforceMaxDateTimeVariable()
  {
    return mEnforceMaxDateTimeVariable;
  }

  @Override
  public Variable<Long> getMaxNumberOfTimePointsVariable()
  {
    return mMaxNumberOfTimePointsVariable;
  }

  @Override
  public Variable<Long> getMaxDurationVariable()
  {
    return mMaxDurationVariable;
  }

  @Override
  public Variable<TimeUnitEnum> getMaxDurationUnitVariable()
  {
    return mMaxDurationUnitVariable;
  }

  @Override
  public Variable<LocalDateTime> getMaxDateTimeVariable()
  {
    return mMaxDateTimeVariable;
  }

  @Override
  public Variable<LocalDateTime> getStartDateTimeVariable()
  {
    return mStartDateTimeVariable;
  }

  @Override
  public Variable<Long> getTimePointCounterVariable()
  {
    return mTimePointCounterVariable;
  }

  @Override
  public Variable<Class<? extends FileStackSinkInterface>> getCurrentFileStackSinkTypeVariable()
  {
    return mCurrentFileStackSinkTypeVariable;
  }

  @Override
  public Variable<FileStackSinkInterface> getCurrentFileStackSinkVariable()
  {
    return mCurrentFileStackSinkVariable;
  }

  @Override
  public Variable<File> getRootFolderVariable()
  {
    return mRootFolderVariable;
  }

  @Override
  public Variable<String> getDataSetNamePostfixVariable()
  {
    return mDataSetNamePostfixVariable;
  }

}
