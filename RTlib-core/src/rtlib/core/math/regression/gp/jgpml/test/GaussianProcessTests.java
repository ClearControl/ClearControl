package rtlib.core.math.regression.gp.jgpml.test;

import jama.Matrix;

import org.junit.Test;

import rtlib.core.math.regression.gp.jgpml.GaussianProcess;
import rtlib.core.math.regression.gp.jgpml.covariancefunctions.CovLINone;
import rtlib.core.math.regression.gp.jgpml.covariancefunctions.CovNoise;
import rtlib.core.math.regression.gp.jgpml.covariancefunctions.CovSum;
import rtlib.core.math.regression.gp.jgpml.covariancefunctions.CovarianceFunction;
import rtlib.core.math.regression.gp.jgpml.util.CSVtoMatrix;

public class GaussianProcessTests
{

	@Test
	public void test()
	{

		CovarianceFunction covFunc = new CovSum(6,
																						new CovLINone(),
																						new CovNoise());
		GaussianProcess gp = new GaussianProcess(covFunc);

		double[][] logtheta0 = new double[][]
		{
		{ 0.1 },
		{ Math.log(0.1) } };

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

		res[0].print(res[0].getColumnDimension(), 16);
		res[1].print(res[1].getColumnDimension(), 16);

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
