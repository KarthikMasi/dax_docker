package regressionforest.training;
import regressionforest.context.DataCollection;
import regressionforest.matrix.*;

public class StatisticsAggregator {
	
	private int classCount;
	private int dimCount;
	private int sampleCount;
	private double[][][] XX;
	private double[][] X;
	public StatisticsAggregator(int c,int d) {
		sampleCount = 0;
		classCount = c;
		dimCount = d;
		XX = new double[classCount][dimCount][dimCount];
		X = new double[classCount][dimCount];
	}
	
	public StatisticsAggregator(int classCount_, int dimCount_, int sampleCount_){
		sampleCount = sampleCount_;
		classCount = classCount_;
		dimCount = dimCount_;
		XX = new double[classCount][dimCount][dimCount];
		X = new double[classCount][dimCount];
	}
	
	
	public StatisticsAggregator(int classCount_, int dimCount_, int sampleCount_,
			double[][][] XX_, double[][] X_){
		sampleCount = sampleCount_;
		classCount = classCount_;
		dimCount = dimCount_;
		XX = MatrixComputor.Copy(XX_);
		X = MatrixComputor.Copy(X_);	
	}
	
	public void Clear() {
		sampleCount = 0;
		XX = new double[classCount][dimCount][dimCount];
		X = new double[classCount][dimCount];
	}

	public StatisticsAggregator DeepClone() {
		StatisticsAggregator result = new StatisticsAggregator(classCount,dimCount);
		result.sampleCount = sampleCount;
		result.X = MatrixComputor.Copy(X);
		result.XX = MatrixComputor.Copy(XX);		
		return result;
	}

	public void Aggregate(DataCollection data, int index) {
		double[] datum = new double[dimCount];
		sampleCount++;			
		for (int c = 0; c < classCount; ++c){		
			datum = data.GetOffset(index, c);
			X[c] = MatrixComputor.Add(X[c], datum);			
			for (int i = 0; i < dimCount; ++i){
				for (int j = 0; j < dimCount; ++j){
					if (i <= j)
						XX[c][i][j] += datum[i] * datum[j];
					else
						XX[c][i][j] = XX[c][j][i];					
				}
			}		
		}
	}

	public void Aggregate(StatisticsAggregator aggregator) {
		X = MatrixComputor.Add(X, aggregator.X);
		XX = MatrixComputor.Add(XX, aggregator.XX);
		sampleCount += aggregator.sampleCount;
	}

	public double Entropy() {
		double[][][] Covariance = GetCovariance();
		double ClassDeterminant = 0;
		double result = 0;
		for (int c = 0; c < classCount; ++c){
			ClassDeterminant = Determinant.det((Covariance[c]));
			if (ClassDeterminant == 0.0) return Double.POSITIVE_INFINITY;			
			result += 0.5 * Math.log(Math.pow(2.0 * Math.PI * Math.E, (double)(dimCount)) * ClassDeterminant);			
		}
		return result;
	}

	public double[][] GetMean() {
		return MatrixComputor.Scale(X, (1.0 / (double)(sampleCount > 0 ? sampleCount : 1)));
	}

	public double[][][] GetCovariance(){
		double[][][] result = new double [classCount][dimCount][dimCount];
		double[][] mean = GetMean();
		for (int c = 0; c < classCount; ++c){					
			for (int i = 0; i < dimCount; ++i){
				for (int j = 0; j < dimCount; ++j){
					if (i <= j)
						result[c][i][j] = XX[c][i][j] * XX[c][j][i] / (double)(sampleCount > 0 ? sampleCount : 1) - mean[c][i] * mean[c][j];
					else
						result[c][i][j] = result[c][j][i];					
				}
			}		
		} 		
		return result;
	}

	public double[] GetUncertainty(){
		double[] result = new double[classCount];
		if (sampleCount < 5){
			for (int c = 0; c < classCount; ++c)
				result[c] = Double.MAX_VALUE;
			return result;
		}
		
		double[][][] Covariance = GetCovariance();
		for (int c = 0; c < classCount; ++c)
			for (int i = 0; i < dimCount; ++i)
				for (int j = 0; j < dimCount; ++j)
					result[c] += Math.abs(Covariance[c][i][j]);		
		return result;	
	}

	public double GetClassProbability(double[] datum, int classIndex) throws Exception {
		double[] mean = GetMean()[classIndex];
		double[][] covariance = GetCovariance()[classIndex];
		double det = Determinant.det(covariance);
		double[][] invcov = Inverse.invert(covariance);
		
		double s =  1.0 / (Math.pow(2.0 * Math.PI, 0.5 * (double)dimCount) * det);
		
		double[] datumDeMean1D = MatrixComputor.Substract(datum, mean);
		double[][] datumDeMean2D = new double[dimCount][1];
		for (int i = 0; i < dimCount; ++i)
			datumDeMean2D[i][1] = datumDeMean1D[i];
				
		double t = MatrixComputor.Multiply(MatrixComputor.Multiply(MatrixComputor.Transpose(datumDeMean2D),invcov),datumDeMean2D)[0][0];

		return s * Math.exp(-0.5 * t);
	}

	public int GetSampleCount() {
		return sampleCount;
	}
	
	public int GetClassCount() {
		return classCount;
	}
	
	public int GetDimCount() {
		return dimCount;
	}
	
	public double[][][] GetXX() {
		return XX;
	}
	
	public double[][] GetX() {
		return X;
	}	
}
