package clearcontrol.devices.signalgen.staves;

/**
 * Binary pattern stepping stave
 *
 * @author royer
 */
public class BinaryPatternSteppingStave extends PatternSteppingStave
                                        implements StaveInterface
{

  private volatile int mPatternPeriod = 9;
  private volatile int mPatternPhaseIndex = 0;
  private volatile int mPatternOnLength = 1;
  private volatile int mPatternPhaseIncrement = 1;

  /**
   * Instantiates a binary pattern stepping stave given a name
   * 
   * @param pName
   *          stave name
   */
  public BinaryPatternSteppingStave(final String pName)
  {
    super(pName);
  }

  /**
   * Instantiates a binary stepping stave for a iven name, sync start, sync
   * stop, number of steps, period, phase index, on-length and phase increment.
   * 
   * @param pName
   *          name
   * @param pSyncStart
   *          sync start
   * @param pSyncStop
   *          sync stop
   * @param pNumberOfSteps
   *          number of steps
   * @param pPeriod
   *          period
   * @param pPhaseIndex
   *          phase index
   * @param pOnLength
   *          on-length
   * @param pPhaseIncrement
   *          phase increment
   */
  public BinaryPatternSteppingStave(final String pName,
                                    float pSyncStart,
                                    float pSyncStop,
                                    int pNumberOfSteps,
                                    int pPeriod,
                                    int pPhaseIndex,
                                    int pOnLength,
                                    int pPhaseIncrement)
  {
    super(pName);
    setNumberOfSteps(pNumberOfSteps);
    setSyncStart(pSyncStart);
    setSyncStop(pSyncStop);
    setPatternPeriod(pPeriod);
    setPatternPhaseIndex(pPhaseIndex);
    setPatternOnLength(pOnLength);
    setPatternPhaseIncrement(pPhaseIncrement);
  }

  @Override
  public StaveInterface duplicate()
  {
    return new BinaryPatternSteppingStave(getName(),
                                          getSyncStart(),
                                          getSyncStop(),
                                          getNumberOfSteps(),
                                          getPatternPeriod(),
                                          getPatternPhaseIndex(),
                                          getPatternOnLength(),
                                          getPatternPhaseIncrement());
  }

  @Override
  public float function(int pIndex)
  {
    final int modulo = (pIndex + getPatternPhaseIndex())
                       % getPatternPeriod();
    return modulo < getPatternOnLength() ? 1 : 0;
  }

  /**
   * Returns pattern period
   * 
   * @return pattern period
   */
  public int getPatternPeriod()
  {
    return mPatternPeriod;
  }

  /**
   * Sets pattern period
   * 
   * @param pPatternPeriod
   *          pattern period
   */
  public void setPatternPeriod(int pPatternPeriod)
  {
    mPatternPeriod = pPatternPeriod;
  }

  /**
   * Returns pattern phase index
   * 
   * @return pattern phase index
   */
  public int getPatternPhaseIndex()
  {
    return mPatternPhaseIndex;
  }

  /**
   * Returns pattern phase index
   * 
   * @param pPatternPhaseIndex
   *          pattern phase index
   */
  public void setPatternPhaseIndex(int pPatternPhaseIndex)
  {
    mPatternPhaseIndex = pPatternPhaseIndex;
  }

  /**
   * Returns pattern on-length
   * 
   * @return pattern on-length
   */
  public int getPatternOnLength()
  {
    return mPatternOnLength;
  }

  /**
   * Set pattern on-length
   * 
   * @param pPatternOnLength
   *          pattern on-length
   */
  public void setPatternOnLength(int pPatternOnLength)
  {
    mPatternOnLength = pPatternOnLength;
  }

  /**
   * Pattern phase increment
   * 
   * @return pattern phase increment
   */
  public int getPatternPhaseIncrement()
  {
    return mPatternPhaseIncrement;
  }

  /**
   * Pattern phase increment
   * 
   * @param pPatternPhaseIncrement
   *          pattern phase increment
   */
  public void setPatternPhaseIncrement(int pPatternPhaseIncrement)
  {
    mPatternPhaseIncrement = pPatternPhaseIncrement;
  }



}
