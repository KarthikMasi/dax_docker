package regressionforest.context;


public class Feature {
	
	// two offset boxes
	// six element vector indicating the offsets to
	// six boundaries of a feature  box
	// e.g., right, left, back, front, down, up,
	// with respect to the current position to get feature response 
	private float[] offsets ;

	public Feature() {
		offsets = new float[12];
	}

	public Feature(float[] offsets_){
		offsets = new float[12];
		
		int len = offsets_.length;
		if (len != 12) 
			throw new IllegalArgumentException("input offsets array length should be 12.");
		if ((offsets_[0] >= offsets_[1]) || (offsets_[2] >= offsets_[3]) || (offsets_[4] >= offsets_[5]) 
				|| (offsets_[6] >= offsets_[7]) || (offsets_[8] >= offsets_[9]) || (offsets_[10] >= offsets_[11]))
			throw new IllegalArgumentException("input offsets should follow the rules that" +
					"right < left, back < front, down < up");
		
		for (int i = 0; i < 12; ++i)
			offsets[i] = offsets_[i];		
	}
	
	// create features given the ranges of three dimensions
	public Feature(float xRange, float yRange, float zRange){
		offsets = new float[12];
		
		offsets[0] = (float) (2 * Math.random() - 1) * xRange / 2;
		offsets[2] = (float) (2 * Math.random() - 1) * yRange / 2;
		offsets[4] = (float) (2 * Math.random() - 1) * zRange / 32;
		offsets[6] = (float) (2 * Math.random() - 1) * xRange / 2;
		offsets[8] = (float) (2 * Math.random() - 1) * yRange / 2;
		offsets[10] = (float) (2 * Math.random() - 1) * zRange / 32;
		

		float xLen1 = (float) (Math.random() * xRange * 7 / 64 + xRange / 64);
		float yLen1 = (float) (Math.random() * yRange * 7 / 64 + yRange / 64);
		float zLen1 = (float) (Math.random() * zRange * 7 / 64 + zRange / 64);
		float xLen2 = (float) (Math.random() * xRange * 7 / 64 + xRange / 64);
		float yLen2 = (float) (Math.random() * yRange * 7 / 64 + yRange / 64);
		float zLen2 = (float) (Math.random() * zRange * 7 / 64 + zRange / 64);
		
		offsets[1] = offsets[0] + xLen1;
		offsets[3] = offsets[2] + yLen1;
		offsets[5] = offsets[4] + zLen1;
		offsets[7] = offsets[6] + xLen2;
		offsets[9] = offsets[8] + yLen2;
		offsets[11] = offsets[10] + zLen2;
	}
	
	public Feature(float xOffset, float yOffset, float zOffset,float xSize, float ySize, float zSize){
		
		offsets = new float[12];

		offsets[0] = (float) (Math.random() * xOffset);
		offsets[2] = (float) (Math.random() * yOffset);
		offsets[4] = (float) (Math.random() * zOffset);
		offsets[6] = (float) (Math.random() * xOffset);
		offsets[8] = (float) (Math.random() * yOffset);
		offsets[10] = (float) (Math.random() * zOffset);

		float xLen1 = (float) (Math.random() * xSize);
		float yLen1 = (float) (Math.random() * ySize);
		float zLen1 = (float) (Math.random() * zSize);
		float xLen2 = (float) (Math.random() * xSize);
		float yLen2 = (float) (Math.random() * ySize);
		float zLen2 = (float) (Math.random() * zSize);
		
		offsets[1] = offsets[0] + xLen1;
		offsets[3] = offsets[2] + yLen1;
		offsets[5] = offsets[4] + zLen1;
		offsets[7] = offsets[6] + xLen2;
		offsets[9] = offsets[8] + yLen2;
		offsets[11] = offsets[10] + zLen2;
	}
	
	
	
	public float[] GetOffsets(){
		return offsets;
	}

	public float GetResponse(DataCollection dc, int index){		
		int indexOffset = dc.GetIndexOffset(index);
		
		// DataItem-wise dimensions and resolutions
		int xDim = dc.GetxDim(index);
		int yDim = dc.GetyDim(index);
		int zDim = dc.GetzDim(index);
		float xRes = dc.GetxRes(index);
		float yRes = dc.GetyRes(index);
		float zRes = dc.GetzRes(index);
		
		// Voxel-based boundaries of the box region
		int rightVox = (int) ((dc.GetXPos(index) + offsets[0]) / xRes);
		if (rightVox < 0) rightVox = 0;
		int leftVox = (int) ((dc.GetXPos(index) + offsets[1]) / xRes);
		if (leftVox >= xDim) leftVox = xDim - 1;
		int backVox = (int) ((dc.GetYPos(index) + offsets[2]) / yRes);
		if (backVox < 0) backVox = 0;
		int frontVox = (int) ((dc.GetYPos(index) + offsets[3]) / yRes);
		if (frontVox >= yDim) frontVox = yDim - 1;
		int buttomVox = (int) ((dc.GetZPos(index) + offsets[4]) / zRes);
		if (buttomVox < 0) buttomVox = 0;
		int topVox = (int) ((dc.GetZPos(index) + offsets[5]) / zRes);
		if (topVox >= zDim) topVox = zDim - 1;
		
		float sum1 = 0;
		int volum1 = 0;
		// response could be zero if the offset box region was beyond the dimensions of the DataItem
		for (int i = rightVox; i < leftVox + 1; ++i)	
			for (int j = backVox; j < frontVox + 1; ++j)
				for (int k = buttomVox; k < topVox + 1; ++k){
					int l = indexOffset + i + j * xDim + k * xDim * yDim;
					sum1 += dc.GetDataPoint(l);
					++volum1;
				}

		if (volum1 == 0) ++volum1;
		
		
		// Voxel-based boundaries of the box region
		rightVox = (int) ((dc.GetXPos(index) + offsets[6]) / xRes);
		if (rightVox < 0) rightVox = 0;
		leftVox = (int) ((dc.GetXPos(index) + offsets[7]) / xRes);
		if (leftVox >= xDim) leftVox = xDim - 1;
		backVox = (int) ((dc.GetYPos(index) + offsets[8]) / yRes);
		if (backVox < 0) backVox = 0;
		frontVox = (int) ((dc.GetYPos(index) + offsets[9]) / yRes);
		if (frontVox >= yDim) frontVox = yDim - 1;
		buttomVox = (int) ((dc.GetZPos(index) + offsets[10]) / zRes);
		if (buttomVox < 0) buttomVox = 0;
		topVox = (int) ((dc.GetZPos(index) + offsets[11]) / zRes);
		if (topVox >= zDim) topVox = zDim - 1;
		float sum2 = 0;
		int volum2 = 0;
		for (int i = rightVox; i < leftVox + 1; ++i)	
			for (int j = backVox; j < frontVox + 1; ++j)
				for (int k = buttomVox; k < topVox + 1; ++k){
					int l = indexOffset + i + j * xDim + k * xDim * yDim;
					sum2 += dc.GetDataPoint(l);
					++volum2;
				}

		if (volum2 == 0) ++volum2;
		
		
		
		return sum1 / volum1 - sum2 / volum2;
	}
	
}
