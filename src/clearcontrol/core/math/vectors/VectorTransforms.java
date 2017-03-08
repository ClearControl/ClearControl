package clearcontrol.core.math.vectors;

/**
 * Created by myersadmin on 08/03/2017.
 */
public class VectorTransforms {

    public static double[] normalize(double[] inputVector) {
        if (inputVector.length == 0) {
            throw new IllegalArgumentException("Cannot normalise an empty vector! Returning null.");
        }
        else {
            double[] outputVector = new double[inputVector.length];
            double norm = 0.0;
            for (int i = 0; i < inputVector.length; i++) {
                norm += inputVector[i]*inputVector[i];
            }
            norm = Math.sqrt(norm);
            for (int i = 0; i < inputVector.length; i++) {
                outputVector[i] = inputVector[i] / norm;
            }
            return outputVector;
        }
    }
}
