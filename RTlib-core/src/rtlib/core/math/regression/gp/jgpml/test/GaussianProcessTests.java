package rtlib.core.math.regression.gp.jgpml.test;

import jama.Matrix;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;

import org.junit.Test;
import org.math.plot.Plot2DPanel;

import rtlib.core.math.regression.gp.jgpml.GaussianProcess;
import rtlib.core.math.regression.gp.jgpml.covariancefunctions.CovLINone;
import rtlib.core.math.regression.gp.jgpml.covariancefunctions.CovNoise;
import rtlib.core.math.regression.gp.jgpml.covariancefunctions.CovSum;
import rtlib.core.math.regression.gp.jgpml.covariancefunctions.CovarianceFunction;
import rtlib.core.math.regression.gp.jgpml.util.CSVtoMatrix;

public class GaussianProcessTests
{
	@Test
	public void basicTest()	throws InvocationTargetException,
													InterruptedException
	{

		CovarianceFunction covFunc = new CovSum(1,
																						new CovLINone(),
																						new CovNoise());
		GaussianProcess gp = new GaussianProcess(covFunc);

		double[][] logtheta0 = new double[][]
		{
		{ 1 },
		{ Math.log(2) } };

		Matrix params0 = new Matrix(logtheta0);

		Matrix X = new Matrix(6, 1);
		X.set(0, 0, 0);
		X.set(1, 0, 1);
		X.set(2, 0, 2);
		X.set(3, 0, 3);
		X.set(4, 0, 4);
		X.set(5, 0, 5);

		Matrix Y = new Matrix(6, 1);
		Y.set(0, 0, 0);
		Y.set(1, 0, 1);
		Y.set(2, 0, 1.1);
		Y.set(3, 0, 2);
		Y.set(4, 0, 1.5);
		Y.set(5, 0, 0.5);

		gp.train(X, Y, params0, 200);

		Matrix Xstar = new Matrix(6, 1);
		Xstar.set(0, 0, 0.5);
		Xstar.set(1, 0, 1.5);
		Xstar.set(2, 0, 2.5);
		Xstar.set(3, 0, 3.5);
		Xstar.set(4, 0, 4.5);
		Xstar.set(5, 0, 5.5);
		Matrix Ystar = new Matrix(6, 1);

		Matrix[] YPredCov = gp.predict(Xstar);

		plot(X, Y, Xstar, YPredCov[0]);

		Thread.sleep(10000000);


	}

	@Test
	public void testWithFile() throws InvocationTargetException,
														InterruptedException
	{

		CovarianceFunction covFunc = new CovSum(6,
																						new CovLINone(),
																						new CovNoise());
		GaussianProcess gp = new GaussianProcess(covFunc);

		double[][] logtheta0 = new double[][]
		{
		{ 0.01 },
		{ Math.log(0.01) } };

		Matrix params0 = new Matrix(logtheta0);

		Matrix[] data = CSVtoMatrix.load(	GaussianProcessTests.class,
																			"./data/armdata.csv",
																			6,
																			1);
		Matrix X = data[0];
		Matrix Y = data[1];

		gp.train(X, Y, params0, -20);

		Matrix[] datastar = CSVtoMatrix.load(	GaussianProcessTests.class,
																					"./data/armdatastar.csv",
																					6,
																					1);
		Matrix Xstar = datastar[0];
		Matrix Ystar = datastar[1];

		Matrix[] res = gp.predict(Xstar);

		plot(X, Y, Xstar, Ystar);

		Thread.sleep(10000000);

		res[0].print(res[0].getColumnDimension(), 16);
		res[1].print(res[1].getColumnDimension(), 16);

	}

	private void plot(Matrix X, Matrix Y, Matrix Xstar, Matrix Ystar)	throws InterruptedException,
																																		InvocationTargetException
	{
		EventQueue.invokeAndWait(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					JFrame lJFrame = new JFrame("Test");
					lJFrame.setSize(512, 320);
					Plot2DPanel lPlot2DPanel = new Plot2DPanel();
					lJFrame.getContentPane().setLayout(new BorderLayout(0, 0));
					// lJFrame.getContentPane().add(lPlot2DPanel);
					lJFrame.getContentPane().add(	lPlot2DPanel,
																				BorderLayout.CENTER);
					lPlot2DPanel.addLinePlot(	"train",
																			X.getColumnVector(0),
																			Y.getColumnVector(0));
					lPlot2DPanel.addLinePlot(	"learn",
																			Xstar.getColumnVector(0),
																			Ystar.getColumnVector(0));
					lPlot2DPanel.setVisible(true);
					lPlot2DPanel.revalidate();
					lJFrame.setVisible(true);

				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	// int size = 100;
	// Matrix Xtrain = new Matrix(size, 1);
	// Matrix Ytrain = new Matrix(size, 1);
	//
	// Matrix Xtest = new Matrix(size, 1);
	// Matrix Ytest = new Matrix(size, 1);

	// half of the sinusoid uses points very close to each other and the other
	// half uses
	// more sparse data

	// double inc = 2 * Math.PI / 1000;
	//
	// double[][] data = new double[1000][2];
	//
	// Random random = new Random();
	//
	// for(int i=0; i<1000; i++){
	// data[i][0] = i*inc;
	// data[i][1] = Math.sin(i*+inc);
	// }
	//
	//
	// // NEED TO FILL Xtrain, Ytrain and Xtest, Ytest
	//
	//
	// gp.train(Xtrain,Ytest,params0);
	//
	// // SimpleRealTimePlotter plot = new
	// SimpleRealTimePlotter("","","",false,false,true,200);
	//
	// final Matrix[] out = gp.predict(Xtest);
	//
	// for(int i=0; i<Xtest.getRowDimension(); i++){
	//
	// final double mean = out[0].get(i,0);
	// final double var = 3 * Math.sqrt(out[1].get(i,0));
	//
	// plot.addPoint(i, mean, 0, true);
	// plot.addPoint(i, mean-var, 1, true);
	// plot.addPoint(i, mean+var, 2, true);
	// plot.addPoint(i, Ytest.get(i,0), 3, true);
	//
	// plot.fillPlot();
	// }

}
