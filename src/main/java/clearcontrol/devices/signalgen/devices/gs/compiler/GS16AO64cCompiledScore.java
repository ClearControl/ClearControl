package clearcontrol.devices.signalgen.devices.gs.compiler;

import GS.GSBuffer;

import java.util.ArrayDeque;

public class GS16AO64cCompiledScore
{
    private volatile long mNumberOfMeasures;
    private ArrayDeque<GSBuffer> mArrayData;


    public GS16AO64cCompiledScore()
    {
        if (mArrayData == null)
        {
            mArrayData = new ArrayDeque<GSBuffer>();
            addNewBufferToArrayData();
        }
    }

    // Getters, Setters and Attr Helpers

    public void setNumberOfMeasures(long pNumberOfMeasures)
    {
        mNumberOfMeasures = pNumberOfMeasures;
    }

    public long getNumberOfMeasures()
    {
        return mNumberOfMeasures;
    }

    public ArrayDeque<GSBuffer> getArrayData()
    {
        return mArrayData;
    }

    public void addNewBufferToArrayData()
    {
        try {
            GSBuffer newBuffer = new GSBuffer(2048, 64);
            mArrayData.addLast(newBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addValueToArrayData(double pValue, int pChannelIndex)
    {
        try {
            mArrayData.peekLast().appendValue(pValue,pChannelIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mArrayData.peekLast().getNumTP() == 2048) {
            this.addNewBufferToArrayData();
        }
    }

}
