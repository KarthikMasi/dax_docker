package regressionforest;

import regressionforest.context.DataCollection;
import regressionforest.matrix.LoadPrintData;
import regressionforest.structure.Forest;
import regressionforest.testing.ForestTesterHistPlus;
import regressionforest.utility.FilesSearchBySuffix;
import regressionforest.utility.XmlIO;
import regressionforest.utility.niftiIO;

public class RunLocalizationTestingCentroids {
	public static void main(String[] args) throws Exception {
		if (args.length != 5 && args.length != 6) {
			System.out.println("Either 5 or 6 input arguments are required.");
			System.out.println("1. xml directory");
			System.out.println("2. img path");
			System.out.println("3. box path to save");
			System.out.println("4. classCount");
			System.out.println("5. dimCount");
			System.out.println("6. confidentDataPercentage");
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
		long tBegin = System.currentTimeMillis();
		System.out.println("loading image ...");
		String imgfn = args[1];
		int[][][] img = niftiIO.niftiImgLoad(imgfn);
		float[] voxres = niftiIO.niftiPixdimLoad(imgfn);
		// make data
		int classCount = Integer.parseInt(args[3]);
		int dimCount = Integer.parseInt(args[4]);
		DataCollection dc = new DataCollection(0, dimCount);
		dc.LoadTestData(img, voxres);
		// test
		System.out.println("testing ...");
		double confidentDataPercentage = 0.10;
		if (args.length == 6) confidentDataPercentage = Double.parseDouble(args[5]);
		double[][] box = ForestTesterHistPlus.TestForest(dc, forest, classCount, dimCount,confidentDataPercentage);
		System.out.println("Training cost: " + Double.toString((double)(System.currentTimeMillis() -  tBegin) / 1000) + " seconds");
		
		// save box into txt
		System.out.println("saving result ...");
		String txtfile = args[2];
		LoadPrintData.SaveBoxes(box, txtfile);
	}

}
