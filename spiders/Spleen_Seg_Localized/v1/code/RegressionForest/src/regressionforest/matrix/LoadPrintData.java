package regressionforest.matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class LoadPrintData {
	public static int[][][] LoadMatrix(String path) throws FileNotFoundException {
		Scanner in = new Scanner(new File(path));
		int x = in.nextInt(), y = in.nextInt(), z = in.nextInt();
		int[][][] matrix = new int[x][y][z];
		for (int i = 0; i < x; ++i) 
			for (int j = 0; j < y; ++j) 
				for (int k = 0; k < z; ++k) {
					matrix[i][j][k] = in.nextInt();
				}
		in.close();
		return  matrix;
	}

	public static float[][] LoadBoxes(String path) throws FileNotFoundException {
		Scanner in = new Scanner(new File(path));
		int boxCount = in.nextInt();
		float[][] boxes = new float[boxCount][6];
		for (int i = 0; i < boxCount; ++i)
			for (int j = 0; j < 6; ++j) 
				boxes[i][j] = in.nextFloat();
		in.close();
		return boxes;
	}

	public static float[] LoadVoxRes(String path) throws FileNotFoundException {
		Scanner in = new Scanner(new File(path));
		float[] voxRes = new float[3];
		voxRes[0] = in.nextFloat();
		voxRes[1] = in.nextFloat();
		voxRes[2] = in.nextFloat();
		in.close();
		return voxRes;
	}
	
	public static void SaveBoxes(double[][] box, String txtfile) throws FileNotFoundException{
		int x = box.length; int y = box[0].length;
		PrintWriter f = new PrintWriter(txtfile);
		f.printf("%d %d\n", x, y);
		for (int i = 0; i < x; ++i)
			for (int j = 0; j < y; ++j) 
					f.println(box[i][j]);
		f.close();
	}

	public static void SaveBoxes(float[][] box, String txtfile) throws FileNotFoundException{
		int x = box.length; int y = box[0].length;
		PrintWriter f = new PrintWriter(txtfile);
		f.printf("%d %d\n", x, y);
		for (int i = 0; i < x; ++i)
			for (int j = 0; j < y; ++j) 
					f.println(box[i][j]);
		f.close();
	}
	
	
	public static void PrintBoxes(double[][] box){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < box.length; i++) {
			double[] row = box[i];
			sb.append("[");
			for (int j = 0; j < row.length; j++) {
				sb.append(" ");
				sb.append(row[j]);
			}
			sb.append(" ]\n");
		}
		sb.deleteCharAt(sb.length() - 1);	
		System.out.println(sb.toString());
	}
	
	public static void PrintBoxes(float[][] box){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < box.length; i++) {
			float[] row = box[i];
			sb.append("[");
			for (int j = 0; j < row.length; j++) {
				sb.append(" ");
				sb.append(row[j]);
			}
			sb.append(" ]\n");
		}
		sb.deleteCharAt(sb.length() - 1);	
		System.out.println(sb.toString());
	}
	
}
