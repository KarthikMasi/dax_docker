package regressionforest.utility;

import java.util.ArrayList;

public class Histogram {
	private int numBins;
	private double binWidth;
	private double[] binLowBound;
	private double[] binHighBound;
	private double[] binCenter;
	private int[] binNumCount;
	private ArrayList<Double> data;

	public Histogram(ArrayList<Double> data_, int nBins, double lowValue, double highValue){
		data = data_;
		numBins = nBins;
		binWidth = (highValue - lowValue) / (double)(numBins);
		binLowBound = new double[numBins];
		binHighBound = new double[numBins];
		binCenter = new double[numBins];
		binNumCount = new int[numBins];
		
		for (int n = 0; n < numBins; n++){
			binLowBound[n] = lowValue + n * binWidth;
			binHighBound[n] = lowValue + (n + 1) * binWidth;
			binCenter[n] = (binLowBound[n] + binHighBound[n]) /2 ;
		}
		
		for (int i = 0; i < data.size(); i++){
			double datum = data.get(i);
			for  (int n = 0; n < numBins; n++){
				if ((datum >= binLowBound[n]) && (datum < binHighBound[n]))
					binNumCount[n]++;
			}
		}
	}
	
	public double getBinWidth(){
		return binWidth;
	}
	
	public int getMaxBinNumCount(){
		int count = binNumCount[0];
		for  (int n = 1; n < numBins; n++)
			count = (count < binNumCount[n]) ? binNumCount[n] : count; 
		return count;
	}
	
	private int getMaxCountBinID(){
		int id = 0;
		int count = binNumCount[id];
		for  (int n = 1; n < numBins; n++){
			if (count < binNumCount[n]){
				count = binNumCount[n];
				id = n;
			}
		}
		return id;
	}
	
	public double getMaxCountBinCenter(){
		return binCenter[getMaxCountBinID()];
	}
	
	public double getMaxCountBinMean(){
		int id = getMaxCountBinID();
		int count = 0;
		double result = 0;
		for (int i = 0; i < data.size(); i++){
			double datum = data.get(i);
			if ((datum >= binLowBound[id]) && (datum < binHighBound[id])){
				result += datum;
				count++;
			}
		}
		return result / (double)(count);
	}
	
}
