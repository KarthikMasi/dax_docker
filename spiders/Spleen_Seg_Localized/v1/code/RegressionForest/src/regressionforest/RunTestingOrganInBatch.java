package regressionforest;

import regressionforest.context.DataCollection;
import regressionforest.matrix.LoadPrintData;
import regressionforest.structure.Forest;
import regressionforest.testing.ForestTesterHistPlus;
import regressionforest.utility.FilesSearchBySuffix;
import regressionforest.utility.XmlIO;
import regressionforest.utility.niftiIO;

public class RunTestingOrganInBatch {
	public static void main(String[] args) throws Exception {
		if (args.length != 5 && args.length != 6) {
			System.out.println("Either 5 or 6 input arguments are required.");
			System.out.println("1. xml directory");
			System.out.println("2. img directory");
			System.out.println("3. box directory");
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
		int classCount = Integer.parseInt(args[3]);
		int dimCount = Integer.parseInt(args[4]);
		double confidentDataPercentage = 0.10;
		if (args.length == 6) confidentDataPercentage = Double.parseDouble(args[5]);
				
		String img_dir = args[1];
		String img_suffix = "nii.gz";
		String box_dir = args[2];
		String[] imgFiles = FilesSearchBySuffix.FilesSearch(img_dir, img_suffix);

		for (int i = 0; i < imgFiles.length; i++){
			long tBegin = System.currentTimeMillis();
			String imgfn = img_dir + imgFiles[i];
			String fn = imgFiles[i].substring(imgFiles[i].indexOf("img")+3, imgFiles[i].indexOf(".nii.gz"));
			int[][][] img = niftiIO.niftiImgLoad(imgfn);
			float[] voxres = niftiIO.niftiPixdimLoad(imgfn);
			DataCollection dc = new DataCollection(0, dimCount);
			dc.LoadTestData(img, voxres);
			double[][] box = ForestTesterHistPlus.TestForest(dc, forest, classCount, dimCount,confidentDataPercentage);
			System.out.println("Training cost: " + Double.toString((double)(System.currentTimeMillis() -  tBegin) / 1000) + " seconds");
			String txtfile = box_dir + "box" + fn + ".txt";
			LoadPrintData.SaveBoxes(box, txtfile);
		}
	}
}
