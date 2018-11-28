package clearcontrol.devices.signalgen.devices.gs;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import clearcontrol.devices.signalgen.SignalGeneratorBase;
import clearcontrol.devices.signalgen.SignalGeneratorInterface;
import clearcontrol.devices.signalgen.SignalGeneratorQueue;
import clearcontrol.devices.signalgen.devices.gs.compiler.GS16AO64cCompiledScore;
import clearcontrol.devices.signalgen.devices.gs.compiler.GS16AO64cScoreCompiler;
import clearcontrol.devices.signalgen.score.ScoreInterface;

import GS.GSSequencer;
import GSConstants.GSConstants;

/**
 *
 *
 * @author AhmetCanSolak
 */
public class GS16AO64cSignalGenerator extends SignalGeneratorBase
                                      implements  SignalGeneratorInterface
{
    private final GSConstants constants = new GSConstants();
    private final GS16AO64cCompiledScore mGS16AO64cCompiledScore = new GS16AO64cCompiledScore();

    private GSSequencer sequencer;
    double mWaitTimeInMilliseconds = 0;


    public GS16AO64cSignalGenerator()
    {
        super("GS16AO64cSignalGenerator");
        try {
            sequencer = new GSSequencer(65536*3, 40000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public double getTemporalGranularityInMicroseconds()
    {
        double samplingRate = GSSequencer.currentSampleRate;
        return 1 / samplingRate;
    }

    @Override
    public Future<Boolean> playQueue(SignalGeneratorQueue pSignalGeneratorQueue)
    {
        return super.playQueue(pSignalGeneratorQueue);
    }

    @Override
    public boolean playScore(ScoreInterface pScore)
    {
        final Thread lCurrentThread = Thread.currentThread();
        final int lCurrentThreadPriority = lCurrentThread.getPriority();
        lCurrentThread.setPriority(Thread.MAX_PRIORITY);
        mTriggerVariable.set(true);

        prependTransitionMovement(pScore,
                getTransitionDurationInNanosecondsVariable().get(),
                TimeUnit.NANOSECONDS);

        GS16AO64cScoreCompiler.compile(mGS16AO64cCompiledScore, pScore);

        boolean lPlayed = sequencer.play(mGS16AO64cCompiledScore.getArrayData(), 1);

        lCurrentThread.setPriority(lCurrentThreadPriority);
        mTriggerVariable.set(false);

        return lPlayed && super.playScore(pScore);
    }

    @Override
    public boolean open()
    {
        return true;
    }

    @Override
    public boolean close()
    {
        return true;
    }
}
