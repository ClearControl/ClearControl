package clearcontrol.devices.signalgen.devices.gs.compiler;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorFeature;
import clearcontrol.devices.signalgen.movement.MovementInterface;
import clearcontrol.devices.signalgen.score.ScoreInterface;
import clearcontrol.devices.signalgen.staves.StaveInterface;

import java.util.ArrayList;

public class GS16AO64cScoreCompiler implements AsynchronousExecutorFeature
{

    public static void compile(GS16AO64cCompiledScore pGS16AO64cCompiledScore,
                               ScoreInterface pScore)
    {
        final ArrayList<MovementInterface> lMovements = pScore.getMovements();

        for (final MovementInterface lMovement: lMovements)
            compileMovement(pGS16AO64cCompiledScore, lMovement);

    }

    private static void compileMovement(GS16AO64cCompiledScore pGS16AO64cCompiledScore,
                                        MovementInterface lMovement)
    {
        for (int i = 0; i < lMovement.getNumberOfStaves(); i++) {
            StaveInterface lStave = lMovement.getStave(i);
            pGS16AO64cCompiledScore.addValueToArrayData(lStave.getValue(i),i);
        }
    }

}
