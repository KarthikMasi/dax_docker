package regressionforest;

import java.io.FileNotFoundException;
import java.io.IOException;

import regressionforest.context.DataCollection;
import regressionforest.structure.Forest;
import regressionforest.utility.FilesSearchBySuffix;
import regressionforest.utility.XmlIO;
import regressionforest.utility.niftiIO;

public class LeafNodeClassifier {
	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException{
		if (args.length != 3) {
			System.out.println("Three input arguments are required exactly.");
			System.out.println("1. xml directory");
			System.out.println("2. img path");
			System.out.println("3. result path prefix");
			return;
		}
		
		// load forest
		System.out.println("loading forest ...");
		String xml_dir = args[0];
		String[] xmlFiles = FilesSearchBySuffix.FilesSearch(xml_dir, "xml");
		Forest forest = new Forest();
		XmlIO xIO = new XmlIO(); 	
		for (int x = 0; x < xmlFiles.length; x++){
			Forest tmp = xIO.LoadForestFromDocument(xml_dir + xmlFiles[x]);
			for (int t = 0; t < tmp.GetTreeCount(); t++)
				forest.AddTree(tmp.GetTree(t));
		}
		// load image
		System.out.println("loading image ...");
		String imgfn = args[1];
		int[][][] img = niftiIO.niftiImgLoad(imgfn);
		float[] voxres = niftiIO.niftiPixdimLoad(imgfn);
		// make data
		int dimCount = 3;
		DataCollection data = new DataCollection(0, dimCount);
		data.LoadTestData(img, voxres);
		// apply 
		int[][] leafnodeindices = forest.Apply(data);
		// organize and save
		int xx = img.length, yy = img[0].length, zz = img[0][0].length;
		String result_prefix = args[2];
		for (int t = 0; t < forest.GetTreeCount(); t++){
			int[] treeleafnodeindices = leafnodeindices[t];
			int[][][] tmp = new int[xx][yy][zz];
			for (int k = 0; k < zz; k++)
				for (int j = 0; j < yy; j++)
					for (int i = 0; i < xx; i++)
						tmp[i][j][k] = treeleafnodeindices[i + j * xx + k * xx * yy];
			
			niftiIO.niftiImgSave(tmp, result_prefix + Integer.toString(t));			
		}
	}
}
