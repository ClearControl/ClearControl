package clearcontrol.devices.stages;

/**
 * Stage type
 *
 * @author royer
 */
public enum StageType
{
 /**
  * Single axis stage
  */
 Single,
 /**
  * Multi axis stage
  */
 Multi,
 /**
  * X,Y, and R stage
  */
 XYZR,

 /**
  * Hub device that combines several other stage devices
  */
 Hub
}
