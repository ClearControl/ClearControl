package clearcontrol.devices.signalgen.devices.gs.compiler;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorFeature;
import clearcontrol.devices.signalgen.measure.MeasureInterface;
import clearcontrol.devices.signalgen.score.ScoreInterface;
import clearcontrol.devices.signalgen.staves.StaveInterface;

import java.util.ArrayList;

public class GS16AO64cScoreCompiler implements AsynchronousExecutorFeature
{

    public static void compile(GS16AO64cCompiledScore pGS16AO64cCompiledScore,
                               ScoreInterface pScore)
    {
        final ArrayList<MeasureInterface> lMeasures = pScore.getMeasures();

        for (final MeasureInterface lMeasure: lMeasures)
            compileMeasure(pGS16AO64cCompiledScore, lMeasure);

    }

    private static void compileMeasure(GS16AO64cCompiledScore pGS16AO64cCompiledScore,
                                        MeasureInterface lMeasure)
    {
        for (int i = 0; i < lMeasure.getNumberOfStaves(); i++) {
            StaveInterface lStave = lMeasure.getStave(i);
            pGS16AO64cCompiledScore.addValueToArrayData(lStave.getValue(i),i);
        }
    }

}
