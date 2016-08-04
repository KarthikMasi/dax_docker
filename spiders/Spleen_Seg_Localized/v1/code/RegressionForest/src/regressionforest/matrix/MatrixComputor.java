package regressionforest.matrix;

public class MatrixComputor {
	
	public static double[][][] Copy(double[][][] matrix) {
		int x = matrix.length;
		int y = matrix[0].length;
		int z = matrix[0][0].length;
		double[][][] result = new double[x][y][z];		
		
		for (int i = 0; i < x; ++i)
			for (int j = 0; j < y; ++j)
				for (int k = 0; k < z; ++k) 
				result[i][j][k] = matrix[i][j][k];
		
		return result;
	}
	
	public static double[][] Copy(double[][] matrix) {
		int x = matrix.length;
		int y = matrix[0].length;
		double[][] result = new double[x][y];
		
		for (int i = 0; i < x; ++i) {
			for (int j = 0; j < y; ++j) {
				result[i][j] = matrix[i][j];
			}
		}
		
		return result;
	}
	
	public static double[] Copy(double[] matrix) {
		int x = matrix.length;
		double[] result = new double[x];
		
		for (int i = 0; i < x; ++i) {
				result[i] = matrix[i];
		}
		
		return result;
	}
	
	public static int[] Copy(int[] matrix) {
		int x = matrix.length;
		int[] result = new int[x];
		
		for (int i = 0; i < x; ++i) {
				result[i] = matrix[i];
		}
		
		return result;
	}
	
	public static double[][][] Add(double[][][] d1, double[][][] d2) {
		int x1 = d1.length, x2 = d2.length;
		int y1 = d1[0].length, y2 = d2[0].length;
		int z1 = d1[0][0].length, z2 = d2[0][0].length;
		
		if (x1 != x2 || y1 != y2 || z1 != z2) 
			return null;
		
		double[][][] result = new double[x1][y1][z1];
		for (int i = 0; i < x1; ++i) 
			for (int j = 0; j < y1; ++j) 
				for (int k = 0; k < z1; ++k) 
				result[i][j][k] = d1[i][j][k] + d2[i][j][k];
	
		return result;
	}


	public static double[][] Add(double[][] d1, double[][] d2) {
		int x1 = d1.length, x2 = d2.length;
		int y1 = d1[0].length, y2 = d2[0].length;
		
		if (x1 != x2 || y1 != y2) 
			return null;
		
		double[][] result = new double[x1][y1];
		for (int i = 0; i < x1; ++i) 
			for (int j = 0; j < y1; ++j) 
				result[i][j] = d1[i][j] + d2[i][j];
			
		return result;
	}


	public static double[] Add(double[] d1, double[] d2) {
		if (d1.length != d2.length) 
			return null;		
		double[] result = new double[d1.length];
		for (int i = 0; i < d1.length; ++i) 
			result[i]= d1[i] + d2[i];

		return result;
	}
	
	public static double[][][] Substract(double[][][] d1, double[][][] d2) {
		int x1 = d1.length, x2 = d2.length;
		int y1 = d1[0].length, y2 = d2[0].length;
		int z1 = d1[0][0].length, z2 = d2[0][0].length;
		
		if (x1 != x2 || y1 != y2 || z1 != z2) 
			return null;
		
		double[][][] result = new double[x1][y1][z1];
		for (int i = 0; i < x1; ++i) 
			for (int j = 0; j < y1; ++j) 
				for (int k = 0; k < z1; ++k) 
				result[i][j][k] = d1[i][j][k] - d2[i][j][k];
		
		return result;
	}


	public static double[][] Substract(double[][] d1, double[][] d2) {
		int x1 = d1.length, x2 = d2.length;
		int y1 = d1[0].length, y2 = d2[0].length;
		
		if (x1 != x2 || y1 != y2) 
			return null;
		
		double[][] result = new double[x1][y1];
		for (int i = 0; i < x1; ++i) 
			for (int j = 0; j < y1; ++j) 
				result[i][j] = d1[i][j] - d2[i][j];

		return result;
	}


	public static double[] Substract(double[] d1, double[] d2) {
		if (d1.length != d2.length) 
			return null;		
		double[] result = new double[d1.length];
		for (int i = 0; i < d1.length; ++i) 
			result[i]= d1[i] - d2[i];
	
		return result;
	}
	
	public static double[][][] Scale(double[][][] d, double s) {
		int x = d.length;
		int y = d[0].length;
		int z = d[0][0].length;
		double[][][] result = new double[x][y][z];
		
		for (int i =0; i < x; ++i)
			for (int j =0; j < y; ++j)
				for (int k =0; k < z; ++k)
					result[i][j][k] = d[i][j][k] * s;
		
		return result;
	}
	
	public static double[][] Scale(double[][] d, double s) {
		int x = d.length;
		int y = d[0].length;
		double[][] result = new double[x][y];
		for (int i = 0; i < x; ++i)
			for (int j = 0; j < y; ++j)
				result[i][j] = d[i][j] * s;
		return result;
	}
	
	public static double[] Scale(double[] d, double s) {
		int x = d.length;
		double[] result = new double[x];
		for (int i =0; i < x; ++i)
			result[i] = d[i] * s;
		return result;
	}
	
	public static double[][] Multiply(double[][] a, double[][] b) throws Exception{
		int aRows = a.length;
		int aCols = a[0].length;
		int bRows = b.length;
		int bCols = b[0].length;
		
		if (aCols != bRows){
			throw new Exception("A Columns " + aCols + " does not match with B Rows " + bRows + ".");
		}
		
		double[][] c = new double[aRows][bCols];
		for (int i = 0; i < aRows; ++i)
			for (int j = 0; j < bCols; ++j)
				for (int k = 0; k < aCols; ++k)
					c[i][j] += a[i][k] * b[k][j];
		
		return c; 
	}
	
	public static double[] DotMultiply(double[] d1, double[] d2){
		if (d1.length != d2.length) return null;
		double[] result = new double[d1.length];
		
		for (int i = 0; i < d1.length; ++i) 
			result[i]= d1[i] * d2[i];
		
		return result;				
	}
	
	public static double DotProduct(double[] d1, double[] d2) {
		if (d1.length != d2.length) 
			return -1;
		
		double sum = 0.0;
		for (int i = 0; i < d1.length; ++i)
			sum += d1[i] * d2[i];
		
		return sum;
	}

	public static double[][] Transpose(double[][] m){
		int x = m.length; int y = m[0].length;
		double[][] result = new double[y][x];
		for (int i =0; i < x; ++i)
			for (int j =0; j < y; ++j)
				result[j][i] = m[i][j];
		return result;
	}
}
