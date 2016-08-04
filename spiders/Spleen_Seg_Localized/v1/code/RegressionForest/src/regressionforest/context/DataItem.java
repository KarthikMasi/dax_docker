package regressionforest.context;

import java.util.*;

/*
 * DataItem for localization 
 * a 3-D data associated with several boxes indicating ROIs
 */

public class DataItem {
	private int xDim;
	private int yDim;
	private int zDim;
	
	private float xRes;
	private float yRes;
	private float zRes;
	
	private List<Float> data;
	private List<float[]> box;
	
	public DataItem Load(int[][][] matrix,float[][] box, float[] voxRes, int dimCount, int classCount) throws IllegalArgumentException {
		if (box[0].length != dimCount)
			throw new IllegalArgumentException("the 2nd dimension of the input box should match with the dimension count.");
		if (box.length != classCount)
			throw new IllegalArgumentException("the 1st dimension of the input box should match with the class count.");
		
		data = new ArrayList<Float>();
		this.box = new ArrayList<float[]>();

		xDim = matrix.length;
		yDim = matrix[0].length;
		zDim = matrix[0][0].length;
		
		xRes = voxRes[0];
		yRes = voxRes[1];
		zRes = voxRes[2];
		
		for (int i = 0; i < zDim; ++i)
			for (int j = 0; j < yDim; ++j) 
				for (int k = 0; k < xDim; ++k)
					data.add((float) matrix[k][j][i]);
		
		for (int i = 0; i< classCount; ++i)		
			this.box.add(box[i]);
		
		return this;
	}
	
	public DataItem LoadTestData(int[][][] matrix, float[] voxRes) {
		data = new ArrayList<Float>();
		xDim = matrix.length;
		yDim = matrix[0].length;
		zDim = matrix[0][0].length;
		
		xRes = voxRes[0];
		yRes = voxRes[1];
		zRes = voxRes[2];
		
		for (int i = 0; i < zDim; ++i)
			for (int j = 0; j < yDim; ++j) 
				for (int k = 0; k < xDim; ++k)
					data.add((float) matrix[k][j][i]);
		
		return this;
	}
	
	public int GetDataCount() {
		return data.size();
	}
	
	public int GetBoxCount() {
		return box.size();
	}
	
	public float GetDataPoint(int i) {
		return data.get(i);
	}
	
	public float[] GetBox(int i) {
		return box.get(i);
	}

	public float GetxRes() {
		return xRes;
	}

	public float GetyRes() {
		return yRes;
	}

	public float GetzRes() {
		return zRes;
	}
	
	public int GetxDim() {
		return xDim;
	}

	public int GetyDim() {
		return yDim;
	}

	public int GetzDim() {
		return zDim;
	}
	
	public int GetXGrid(int index) {
		return (index % (xDim * yDim)) % xDim;
	}
	
	public int GetYGrid(int index) {
		return (index % (xDim * yDim)) / xDim;
	}
	
	public int GetZGrid(int index) {
		return index / (xDim * yDim);
	}
	
	public float GetXPos(int index) {
		return GetXGrid(index) * xRes;
	}
	
	public float GetYPos(int index) {
		return GetYGrid(index) * yRes;
	}
	
	public float GetZPos(int index) {
		return GetZGrid(index) * zRes;
	}
	
//	public double[] GetVector(int index) {
//		double[] result = new double[6];
//		result[0] = GetXPos(index);
//		result[1] = result[0];
//		result[2] = GetYPos(index);
//		result[3] = result[2];
//		result[4] = GetZPos(index);
//		result[5] = result[4];
//		return result;
//	}
	
}
