package regressionforest.context;

import java.util.*;


public class DataCollection {
	private List<DataItem> dc;
	private int dimCount;
	private int classCount;
	private int indexB;

	public DataCollection(int classCount, int dimCount) {
		if (dimCount != 6 && dimCount != 3)
			throw new IllegalArgumentException("dimCount should be 6 or 3 without specified indexB.");
		dc = new ArrayList<DataItem>();
		this.classCount = classCount;
		this.dimCount = dimCount;
		indexB = -1;
	}

	public DataCollection(int classCount, int dimCount, int indexB) {
		if (indexB < 0 || indexB > 5) 
			throw new IllegalArgumentException("indexB should be an integer from 0 to 5.");
		if (dimCount != 1)
			throw new IllegalArgumentException("dimCount should be 1 with specified indexB.");

		dc = new ArrayList<DataItem>();
		this.classCount = classCount;
		this.dimCount = dimCount;
		this.indexB = indexB;
	}

	public void AddDataItem(int[][][] matrix, float[][] box, float[] voxRes) throws Exception {
		DataItem di = new DataItem();
		di.Load(matrix, box, voxRes, dimCount, classCount);
		dc.add(di);
	}

	public void AddDataItem(DataItem item) {
		dc.add(item);
	}

	public void LoadTestData(int[][][] matrix, float[] voxRes) {
		dc.clear();
		DataItem di = new DataItem();
		di.LoadTestData(matrix, voxRes);
		dc.add(di);
	}

	public void Clear() {
		dc.clear();
	}

	public DataItem GetDataItem(int index) {
		return dc.get(index);
	}

	public int GetDataItemCount() {
		return dc.size();
	}

	public int GetDataCount() {
		int sum = 0;
		for (DataItem item : dc) {
			sum += item.GetDataCount();
		}
		return sum;
	}

	public int GetClassCount() {
		return classCount;
	}

	public int GetDimCount() {
		return dimCount;
	}

	public float GetDataPoint(int i) {
		DataItem di = new DataItem();
		for (DataItem item : dc) {
			if (i < item.GetDataCount()) {
				di = item;
				break;
			}
			else
				i -= item.GetDataCount();
		}
		return di.GetDataPoint(i);
	}

	public int GetIndexOffset(int i) {
		int indexOffset = 0;
		for (DataItem item : dc) {
			if (i >= item.GetDataCount()){
				indexOffset += item.GetDataCount();
				i -= item.GetDataCount();
			}
		}		
		return indexOffset;
	}

	public float GetxRange() {
		float range =  0;
		for (DataItem item : dc){
			float tmp = item.GetxDim() * item.GetxRes();
			if (tmp > range)
				range = tmp;		
		}
		return range;
	}

	public float GetyRange() {
		float range =  0;
		for (DataItem item : dc){
			float tmp = item.GetyDim() * item.GetyRes();
			if (tmp > range)
				range = tmp;		
		}
		return range;
	}

	public float GetzRange() {
		float range =  0;
		for (DataItem item : dc){
			float tmp = item.GetzDim() * item.GetzRes();
			if (tmp > range)
				range = tmp;		
		}
		return range;
	}

	public int GetxDim(int i) {
		DataItem di = new DataItem();
		for (DataItem item : dc) {
			if (i < item.GetDataCount()) {
				di = item;
				break;
			}
			else
				i -= item.GetDataCount();
		}
		return di.GetxDim();
	}

	public int GetyDim(int i) {
		DataItem di = new DataItem();
		for (DataItem item : dc) {
			if (i < item.GetDataCount()) {
				di = item;
				break;
			}
			else	
				i -= item.GetDataCount();
		}
		return di.GetyDim();
	}


	public int GetzDim(int i) {
		DataItem di = new DataItem();
		for (DataItem item : dc) {	
			if (i < item.GetDataCount()) {
				di = item;
				break;
			}
			else
				i -= item.GetDataCount();
		}
		return di.GetzDim();
	}

	public float GetxRes(int i) {
		DataItem di = new DataItem();
		for (DataItem item : dc) {
			if (i < item.GetDataCount()) {
				di = item;
				break;
			}
			else
				i -= item.GetDataCount();
		}
		return di.GetxRes();
	}

	public float GetyRes(int i) {
		DataItem di = new DataItem();
		for (DataItem item : dc) {
			if (i < item.GetDataCount()) {
				di = item;
				break;
			}
			else
				i -= item.GetDataCount();
		}
		return di.GetyRes();
	}

	public float GetzRes(int i) {
		DataItem di = new DataItem();
		for (DataItem item : dc) {
			if (i < item.GetDataCount()) {
				di = item;
				break;
			}
			else
				i -= item.GetDataCount();
		}
		return di.GetzRes();
	}

	public int GetXGrid(int i) {
		DataItem di = new DataItem();
		for (DataItem item : dc) {
			if (i < item.GetDataCount()) {
				di = item;
				break;
			}
			else
				i -= item.GetDataCount();
		}
		return di.GetXGrid(i);
	}			

	public int GetYGrid(int i) {
		DataItem di = new DataItem();
		for (DataItem item : dc) {
			if (i < item.GetDataCount()) {
				di = item;
				break;
			}
			else
				i -= item.GetDataCount();
		}
		return di.GetYGrid(i);
	}

	public int GetZGrid(int i) {
		DataItem di = new DataItem();
		for (DataItem item : dc) {
			if (i < item.GetDataCount()) {
				di = item;
				break;
			}
			else
				i -= item.GetDataCount();
		}
		return di.GetZGrid(i);
	}

	public float GetXPos(int i) {
		DataItem di = new DataItem();
		for (DataItem item : dc) {
			if (i < item.GetDataCount()) {
				di = item;
				break;
			}
			else
				i -= item.GetDataCount();
		}
		return di.GetXGrid(i) * di.GetxRes();
	}

	public float GetYPos(int i) {
		DataItem di = new DataItem();
		for (DataItem item : dc) {
			if (i < item.GetDataCount()) {
				di = item;
				break;
			}
			else
				i -= item.GetDataCount();
		}
		return di.GetYGrid(i) * di.GetyRes();
	}

	public float GetZPos(int i) {
		DataItem di = new DataItem();
		for (DataItem item : dc) {
			if (i < item.GetDataCount()) {
				di = item;
				break;
			}
			else
				i -= item.GetDataCount();
		}
		return di.GetZGrid(i) * di.GetzRes();
	}

	public double[] GetVector(int index) {
		if (dimCount == 6) {
			double[] result = new double[6];
			result[0] = GetXPos(index);
			result[1] = result[0];
			result[2] = GetYPos(index);
			result[3] = result[2];
			result[4] = GetZPos(index);
			result[5] = result[4];
			return result;
		}
		else if (dimCount == 3){
			double[] result = new double[3];
			result[0] = GetXPos(index);
			result[1] = GetYPos(index);
			result[2] = GetZPos(index);
			return result;
		}
		else {
			double[] result = new double[1];
			if (indexB < 2) result[0] = GetXPos(index);
			else if (indexB < 4) result[0] =  GetYPos(index);
			else result[0] = GetZPos(index);
			return result;
		}
	}

	public double[] GetOffset(int dataIndex, int classIndex){
		float[] target = GetTarget(dataIndex, classIndex);
		double xPos = GetXPos(dataIndex);
		double yPos = GetYPos(dataIndex);
		double zPos = GetZPos(dataIndex);

		if (dimCount == 6) {
			double[] offset = new double[6];
			offset[0] = xPos - (double)(target[0]);
			offset[1] = xPos - (double)(target[1]);
			offset[2] = yPos - (double)(target[2]);
			offset[3] = yPos - (double)(target[3]);
			offset[4] = zPos - (double)(target[4]);
			offset[5] = zPos - (double)(target[5]);		
			return offset;
		}
		else if (dimCount ==3) {
			double[] offset = new double[3];
			offset[0] = xPos - (double)(target[0]);
			offset[1] = yPos - (double)(target[1]);
			offset[2] = zPos - (double)(target[2]);		
			return offset;
		}
		else
		{
			double[] offset = new double[1];
			switch (indexB) {
			case 0: offset[0] = xPos - (double)(target[0]);break;
			case 1: offset[0] = xPos - (double)(target[0]);break;			
			case 2: offset[0] = yPos - (double)(target[0]);break;
			case 3: offset[0] = yPos - (double)(target[0]);break;
			case 4: offset[0] = zPos - (double)(target[0]);break;
			case 5: offset[0] = zPos - (double)(target[0]);break;
			default: offset[0] = xPos - (double)(target[0]);break;
			}
			return offset;
		}
	}

	private float[] GetTarget(int dataIndex, int classIndex) {
		DataItem di = new DataItem();
		for (DataItem item : dc) {
			if (dataIndex < item.GetDataCount()) {
				di = item;
				break;
			}
			else
				dataIndex -= item.GetDataCount();
		}
		return di.GetBox(classIndex);
	}
}
