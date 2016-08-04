package regressionforest.utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class niftiIO {
	public static int[][][] niftiImgLoad(String filename) throws FileNotFoundException, IOException{
		Nifti1Dataset nds = new Nifti1Dataset(filename);
		nds.readHeader();
		double[][][] tmp = nds.readDoubleVol((short) 0);
		int[][][] img = new int[tmp[0][0].length][tmp[0].length][tmp.length];
		for (int i = 0; i < tmp.length; i++)
			for (int j = 0; j < tmp[0].length; j++)
				for (int k = 0; k < tmp[0][0].length; k++)
					img[k][j][i] = (int)(tmp[i][j][k]);
		return img;
	}
	
	
	public static float[] niftiPixdimLoad(String filename) throws FileNotFoundException, IOException{
		Nifti1Dataset nds = new Nifti1Dataset(filename);
		nds.readHeader();
		float[] pixdim = new float[3];
		pixdim[0] = nds.pixdim[1];
		pixdim[1] = nds.pixdim[2];
		pixdim[2] = nds.pixdim[3];
		return pixdim;
	}
	
	public static void niftiImgSave(int[][][] img, String filename)throws FileNotFoundException, IOException{
		Nifti1Dataset nds = new Nifti1Dataset();
		short xx = (short) img.length, yy = (short) img[0].length, zz = (short) img[0][0].length, tt = (short) 0;
		nds.setHeaderFilename(filename);
		nds.setDataFilename(filename);
		nds.setDatatype((short)4);
		nds.setDims((short)3,xx,yy,zz,tt,(short)0,(short)0,(short)0);
		nds.descrip = new StringBuffer("Created by TestNifti1Api");
		nds.writeHeader();
		double[][][] data = new double[zz][yy][xx];

		for (int k=0; k<zz; k++)  
			for (int j=0; j<yy; j++) 
				for (int i=0; i<xx; i++)
					data[k][j][i] = (double) (img[i][j][k]);
		
		nds.writeVol(data, (short)0);
	}	
}
