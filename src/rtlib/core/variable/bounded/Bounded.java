package rtlib.core.variable.bounded;

public interface Bounded<T extends Number>
{

	T getMin();
	T getMax();
	
	void setMin(T pMin);
	void setMax(T pMax);
	
}
