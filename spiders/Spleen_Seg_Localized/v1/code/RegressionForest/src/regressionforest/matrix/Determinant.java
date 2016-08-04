package regressionforest.matrix;

public class Determinant {
	 
    /**
     * Determinant of a regressionforest.matrix using Laplace's formula with expanding along the
     * 0th row. It is not checked whether the regressionforest.matrix is quadratic!
     *
     * @param m Matrix
     * @return determinant
     */
    public static double det(double[][] m) {
        int n = m.length;
        if (n == 1) {
            return m[0][0];
        } else {
            double det = 0;
            for (int j = 0; j < n; j++) {
                det += Math.pow(-1, j) * m[0][j] * det(minor(m, 0, j));
            }
            return det;
        }
    }
 
    /**
     * Computing the minor of the regressionforest.matrix m without the i-th row and the j-th
     * column
     *
     * @param m input regressionforest.matrix
     * @param i removing the i-th row of m
     * @param j removing the j-th column of m
     * @return minor of m
     */
    private static double[][] minor(final double[][] m, final int i, final int j) {
        int n = m.length;
        double[][] minor = new double[n-1][n-1];
        // index for minor regressionforest.matrix position:
        int r = 0, s = 0;
        for (int k = 0; k < n; k++) {
            double[] row = m[k];
            if (k != i) {
                for (int l = 0; l < row.length; l++) {
                    if (l != j) {
                        minor[r][s++] = row[l];
                    }
                }
                r++;
                s = 0;
            }
        }
        return minor;
    }
 
}