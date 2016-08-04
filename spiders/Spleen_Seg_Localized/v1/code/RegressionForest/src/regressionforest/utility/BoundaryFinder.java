package regressionforest.utility;

import java.util.*;

public class BoundaryFinder {
	public static ArrayList<ArrayList<Integer>> ind2sub(int[][][] data, int value){
		ArrayList<Integer> ix = new ArrayList<Integer>();
		ArrayList<Integer> iy = new ArrayList<Integer>();
		ArrayList<Integer> iz = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> xyz = new ArrayList<ArrayList<Integer>>();

		ix.clear();iy.clear();iz.clear();
		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[0].length; j++)
				for (int k = 0; k < data[0][0].length; k++)
					if (data[i][j][k] == value) 
					{ix.add(i);iy.add(j);iz.add(k);}
		xyz.add(ix);xyz.add(iy);xyz.add(iz);
		return xyz;
	}

	public static float[][] GetCentroid(int[][][] data, int[] value, float[] voxres){
		if (voxres.length != 3) throw new IllegalArgumentException("voxres should have 3 elements");
		float[][] centriod = new float[value.length][3];
		for (int v = 0; v < value.length; v++){
			ArrayList<ArrayList<Integer>> xyz = ind2sub(data,value[v]);
			if (xyz.get(0).size() > 0){
				centriod[v][0] = (ArrayListMean(xyz.get(0)) + 1) * voxres[0];
				centriod[v][1] = (ArrayListMean(xyz.get(1)) + 1) * voxres[1];
				centriod[v][2] = (ArrayListMean(xyz.get(2)) + 1) * voxres[2];
			}
			else {
				centriod[v][0] = Float.NaN;
				centriod[v][1] = Float.NaN;
				centriod[v][2] = Float.NaN;
			}
		}
		return centriod;
	}

	public static float[][] GetCentroid(int[][][] data, int[] value){
		float[][] centriod = new float[value.length][3];
		for (int v = 0; v < value.length; v++){
			ArrayList<ArrayList<Integer>> xyz = ind2sub(data,value[v]);
			if (xyz.get(0).size() > 0){
				centriod[v][0] = ArrayListMean(xyz.get(0)) + 1;
				centriod[v][1] = ArrayListMean(xyz.get(1)) + 1;
				centriod[v][2] = ArrayListMean(xyz.get(2)) + 1;
			}
			else {
				centriod[v][0] = Float.NaN;
				centriod[v][1] = Float.NaN;
				centriod[v][2] = Float.NaN;
			}
		}
		return centriod;
	}

	public static float ArrayListMean(ArrayList<Integer> arraylist){
		float result = 0;
		for (int i = 0; i < arraylist.size(); i++)
			result += (float)(arraylist.get(i));
		return result / arraylist.size();	
	}

	public static float[][] GetBoundry(int[][][] data, int[] value, float[] voxres){
		if (voxres.length != 3) throw new IllegalArgumentException("voxres should have 3 elements");
		float[][] box = new float[value.length][6];
		for (int v = 0; v < value.length; v++){
			ArrayList<ArrayList<Integer>> xyz = ind2sub(data,value[v]);
			if (xyz.get(0).size() > 0){
				box[v][0] = (Collections.min(xyz.get(0)) + 1) * voxres[0];
				box[v][1] = (Collections.max(xyz.get(0)) + 1) * voxres[0];
				box[v][2] = (Collections.min(xyz.get(1)) + 1) * voxres[1];
				box[v][3] = (Collections.max(xyz.get(1)) + 1) * voxres[1];
				box[v][4] = (Collections.min(xyz.get(2)) + 1) * voxres[2];
				box[v][5] = (Collections.max(xyz.get(2)) + 1) * voxres[2];
			}
			else {
				box[v][0] = Float.NaN;
				box[v][1] = Float.NaN;
				box[v][2] = Float.NaN;
				box[v][3] = Float.NaN;
				box[v][4] = Float.NaN;
				box[v][5] = Float.NaN;
			}
		}
		return box;
	}

	public static float[][] GetBoundry(int[][][] data, int[] value){
		float[][] box = new float[value.length][6];
		for (int v = 0; v < value.length; v++){
			ArrayList<ArrayList<Integer>> xyz = ind2sub(data,value[v]);
			if (xyz.get(0).size() > 0){
				box[v][0] = Collections.min(xyz.get(0)) + 1;
				box[v][1] = Collections.max(xyz.get(0)) + 1;
				box[v][2] = Collections.min(xyz.get(1)) + 1;
				box[v][3] = Collections.max(xyz.get(1)) + 1;
				box[v][4] = Collections.min(xyz.get(2)) + 1;
				box[v][5] = Collections.max(xyz.get(2)) + 1;
			}
			else {
				box[v][0] = Float.NaN;
				box[v][1] = Float.NaN;
				box[v][2] = Float.NaN;
				box[v][3] = Float.NaN;
				box[v][4] = Float.NaN;
				box[v][5] = Float.NaN;
			}

		}
		return box;
	}
}
