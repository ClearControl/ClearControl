package rtlib.gui.video.video3d;

import java.util.concurrent.TimeUnit;

import rtlib.core.units.Magnitudes;
import rtlib.stack.Stack;
import clearvolume.volume.Volume;
import clearvolume.volume.VolumeManager;
import clearvolume.volume.sink.VolumeSinkInterface;
import clearvolume.volume.sink.relay.RelaySinkAdapter;
import clearvolume.volume.sink.relay.RelaySinkInterface;

public class CopyVolumeSink extends RelaySinkAdapter implements
																										RelaySinkInterface
{

	private Class<?> mType;

	public CopyVolumeSink(VolumeSinkInterface pRelaySink, Class<?> pType)
	{
		super(pRelaySink);
		mType = pType;
	}

	@Override
	public void sendVolume(Volume<?> pVolume)
	{
		if (getRelaySink() != null)
			getRelaySink().sendVolume(pVolume);
	}

	public void sendVolume(Stack<?> pStack)
	{
		Volume<?> lRequestAndWaitForVolume = getManager().requestAndWaitForVolume(1,
																																							TimeUnit.MILLISECONDS,
																																							mType,
																																							pStack.getDimensions());

		lRequestAndWaitForVolume.setVoxelSizeInRealUnits(	"nm",
																											pStack.getVoxelSizeInRealUnits(0),
																											pStack.getVoxelSizeInRealUnits(1),
																											pStack.getVoxelSizeInRealUnits(2));

		lRequestAndWaitForVolume.setColor(1, 1, 1, 1);

		lRequestAndWaitForVolume.setTimeIndex(pStack.getIndex());
		lRequestAndWaitForVolume.setTimeInSeconds(Magnitudes.nano2unit(pStack.getTimeStampInNanoseconds()));
		lRequestAndWaitForVolume.setChannelID(0);
		lRequestAndWaitForVolume.setChannelName("channel 0");

		lRequestAndWaitForVolume.copyDataFrom(pStack.getPointer()
																								.getByteBuffer());

		sendVolume(lRequestAndWaitForVolume);
	}

	@Override
	public VolumeManager getManager()
	{
		return getRelaySink().getManager();
	}

}
