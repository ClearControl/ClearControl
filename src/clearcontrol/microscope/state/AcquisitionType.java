package clearcontrol.microscope.state;

/**
 * Acquisition type
 *
 * @author royer
 */
public enum AcquisitionType
{
 /**
  * Interactive acquisition stack
  */
 Interactive,

 /**
  * Tiemlapse stack
  */
 TimeLapse,

 TimelapseSequential,

 TimeLapseInterleaved,

 TimeLapseOpticallyCameraFused,

 TimeLapseHybridInterleavedOpticsPrefused
}
