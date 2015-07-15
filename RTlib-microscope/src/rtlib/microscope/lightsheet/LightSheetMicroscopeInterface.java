package rtlib.microscope.lightsheet;

public interface LightSheetMicroscopeInterface
{

	public void setWidthHeight(int pWidth, int pHeight);

	public void setExposure(double pValue);

	public void setD(int pIndex, double pValue);

	public void setI(int pIndex, double pValue);

	public void setY(int pIndex, double pValue);

	public void setA(int pIndex, double pValue);

	public void setB(int pIndex, double pValue);

	public void setR(int pIndex, double pValue);

	public void setL(int pIndex, double pValue);

}
