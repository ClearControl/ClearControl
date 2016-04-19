package rtlib.microscope.gui.halcyon;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import halcyon.model.node.HalcyonNodeType;
import javafx.scene.Node;

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
