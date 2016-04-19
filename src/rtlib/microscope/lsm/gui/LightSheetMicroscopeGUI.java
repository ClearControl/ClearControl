package rtlib.microscope.lsm.gui;

import rtlib.microscope.gui.MicroscopeGUI;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.scripting.engine.ScriptingEngine;
import rtlib.scripting.lang.groovy.GroovyScripting;

public class LightSheetMicroscopeGUI extends MicroscopeGUI
{

	public LightSheetMicroscopeGUI(	LightSheetMicroscope pLightSheetMicroscope,
																	boolean p3DView)
	{
		super(pLightSheetMicroscope,p3DView);
		
		pLightSheetMicroscope.getDeviceLists()
	}

}
