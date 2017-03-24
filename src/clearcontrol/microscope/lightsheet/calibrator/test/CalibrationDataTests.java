package clearcontrol.microscope.lightsheet.calibrator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import clearcontrol.microscope.lightsheet.calibrator.CalibrationData;
import clearcontrol.microscope.lightsheet.calibrator.LightSheetPositioner;

public class CalibrationDataTests
{

  @Test
  public void saveload() throws JsonGenerationException,
                         JsonMappingException,
                         IOException
  {
    CalibrationData lCalibrationData = new CalibrationData();

    SimpleMatrix lMatrix = SimpleMatrix.identity(2);
    LightSheetPositioner lLightSheetPositioner =
                                               new LightSheetPositioner(lMatrix);
    lCalibrationData.mPositionerMap.put("test",
                                        lLightSheetPositioner);

    File lFile =
               File.createTempFile(CalibrationDataTests.class.getSimpleName(),
                                   "saveload");
    System.out.println(lFile);

    lCalibrationData.saveTo(lFile);

    assertTrue(lFile.exists());

    CalibrationData lCalibrationDataRead =
                                         CalibrationData.readFrom(lFile);

    assertNotNull(lCalibrationDataRead);

    assertEquals(1,
                 lCalibrationDataRead.mPositionerMap.get("test").mTransformMatrix.get(0,
                                                                                      0),
                 0.001);

    assertEquals(0,
                 lCalibrationDataRead.mPositionerMap.get("test").mTransformMatrix.get(1,
                                                                                      0),
                 0.001);

  }

}
