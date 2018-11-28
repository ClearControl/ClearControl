package clearcontrol.devices.signalgen.devices.gs.compiler;

import GS.GSBuffer;

import java.util.ArrayDeque;

public class GS16AO64cCompiledScore
{
    private volatile long mNumberOfMovements;
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

    public void setNumberOfMovements(long pNumberOfMovements)
    {
        mNumberOfMovements = pNumberOfMovements;
    }

    public long getNumberOfMovements()
    {
        return mNumberOfMovements;
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
