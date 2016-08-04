package regressionforest.training;
import regressionforest.context.DataCollection;
import regressionforest.matrix.*;

public class StatisticsRecord {
	
	private int classCount;
	private int dimCount;
	private double [][] mean;
	private double [] uncertainty;

	public StatisticsRecord(int c,int d) {
		classCount = c;
		dimCount = d;
		mean = new double [classCount][dimCount];
		uncertainty = new double [classCount];
	}
	
	public StatisticsRecord(int classCount_, int dimCount_, double[][] mean_, double[] uncerntainty_){
		classCount = classCount_;
		dimCount = dimCount_;
		mean = MatrixComputor.Copy(mean_);
		uncertainty = MatrixComputor.Copy(uncerntainty_);	
	}
	

	public StatisticsRecord DeepClone() {
		StatisticsRecord result = new StatisticsRecord(classCount,dimCount);
		result.mean = MatrixComputor.Copy(mean);
		result.uncertainty = MatrixComputor.Copy(uncertainty);		
		return result;
	}


	public double[][] GetMean() {
		return mean;
	}

	public double[] GetUncertainty(){	
		return uncertainty;	
	}
	
	public int GetClassCount() {
		return classCount;
	}
	
	public int GetDimCount() {
		return dimCount;
	}
	
}
