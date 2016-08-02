package clearcontrol.microscope.gui.halcyon;

import halcyon.model.node.HalcyonNodeType;

/**
 * HalcyonNode Type enumeration
 */
public enum MicroscopeNodeType implements HalcyonNodeType
{
	Camera,
	Laser,
	Stage,
	FilterWheel,
	OpticalSwitch,
	SignalGenerator,
	ScalingAmplifier,
	AdaptiveOptics,
	Scripting,
	StackDisplay2D,
	StackDisplay3D,
	Other;

}
