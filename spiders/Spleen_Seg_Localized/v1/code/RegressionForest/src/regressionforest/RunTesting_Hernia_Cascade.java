package regressionforest;

import regressionforest.context.DataCollection;
import regressionforest.matrix.LoadPrintData;
import regressionforest.structure.Forest;
import regressionforest.testing.ForestTesterHistPlus;
import regressionforest.utility.FilesSearchBySuffix;
import regressionforest.utility.XmlIO;
import regressionforest.utility.niftiIO;

public class RunTesting_Hernia_Cascade {
	public static void main(String[] args) throws Exception {
		if (args.length != 9) {
			System.out.println("9 input arguments are required.");
			System.out.println("1. global xml directory");
			System.out.println("2. organ xml directory");
			System.out.println("3. img path");
			System.out.println("4. box path to save");
			System.out.println("5. classCount");
			System.out.println("6. dimCount");
			System.out.println("7. pad margin");
			System.out.println("8. confidentDataPercentage_global");
			System.out.println("9. confidentDataPercentage_organ");
			return;
		}
		
		int classCount = Integer.parseInt(args[4]);
		int classCountOrgan = 1;
		int dimCount = Integer.parseInt(args[5]);
		double pad = Double.parseDouble(args[6]); 
		double[][] box = new double [classCount][dimCount];
		int[] value = {11,13,15,17,22};
		
		// global
		long tBegin = System.currentTimeMillis();
		System.out.println("global ...");
		String xml_dir = args[0];
		String[] xmlFiles = FilesSearchBySuffix.FilesSearch(xml_dir, "xml");
		Forest forest = new Forest();
		XmlIO xIO = new XmlIO(); 	
		for (int x = 0; x < xmlFiles.length; x++){
			Forest tmp = xIO.LoadForestFromDocument(xml_dir + xmlFiles[x]);
			for (int t = 0; t < tmp.GetTreeCount(); t++)
				forest.AddTree(tmp.GetTree(t));
		}
		String imgfn = args[2];
		int[][][] img = niftiIO.niftiImgLoad(imgfn);
		float[] voxres = niftiIO.niftiPixdimLoad(imgfn);
		DataCollection dc = new DataCollection(0, dimCount);
		dc.LoadTestData(img, voxres);
		System.out.println("testing ...");
		double confidentDataPercentage_global = Double.parseDouble(args[7]);
		double[][] box_global = ForestTesterHistPlus.TestForest(dc, forest, classCount, dimCount,confidentDataPercentage_global);
		System.out.println("Training cost: " + Double.toString((double)(System.currentTimeMillis() -  tBegin) / 1000) + " seconds");		
		int xx = img.length; int yy = img[0].length; int zz = img[0][0].length;
		
		// organ
		long tBeginOrgan = System.currentTimeMillis();
		System.out.println("organ ...");
		String xml_organ_dir = args[1];
		for (int c = 0; c < classCount; c++){
			System.out.println(value[c]);
			String organid = String.format("L%02d", value[c]);
			String xml_organ_specific_dir = xml_organ_dir + organid + "/";
			String[] xmlOrganFiles = FilesSearchBySuffix.FilesSearch(xml_organ_specific_dir, "xml");
			Forest organForest = new Forest();
			XmlIO organXIO = new XmlIO(); 	
			for (int x = 0; x < xmlOrganFiles.length; x++){
				Forest tmp = organXIO.LoadForestFromDocument(xml_organ_specific_dir + xmlOrganFiles[x]);
				for (int t = 0; t < tmp.GetTreeCount(); t++)
					organForest.AddTree(tmp.GetTree(t));
			}
			// crop organ with pad
			int rightVox = (int) ((float)(box_global[c][0] - pad) / voxres[0]);
			if (rightVox < 0) rightVox = 0;
			int leftVox = (int) ((float)(box_global[c][0] + pad) / voxres[0]);
			if (leftVox >= xx) leftVox = xx - 1;
			int backVox = (int) ((float)(box_global[c][1] - pad) / voxres[1]);
			if (backVox < 0) backVox = 0;
			int frontVox = (int) ((float)(box_global[c][1] + pad) / voxres[1]);
			if (frontVox >= yy) frontVox = yy - 1;
			int buttomVox = (int) ((float)(box_global[c][2] - pad) / voxres[2]);
			if (buttomVox < 0) buttomVox = 0;
			int topVox = (int) ((float)(box_global[c][2] + pad) / voxres[2]);
			if (topVox >= zz) topVox = zz - 1;
			int[][][] organImg = new int[leftVox - rightVox + 1][frontVox - backVox + 1][topVox - buttomVox + 1];
			for (int k = 0; k < topVox - buttomVox + 1; k++)
				for (int j = 0; j < frontVox - backVox + 1; j++)
					for (int i = 0; i < leftVox - rightVox + 1; i++)
						organImg[i][j][k] = img[rightVox + i][backVox + j][buttomVox + k];
							
			DataCollection organDC = new DataCollection(0, dimCount);
			organDC.LoadTestData(organImg, voxres);
			double confidentDataPercentage_organ = Double.parseDouble(args[8]);
			double[][] box_organ = ForestTesterHistPlus.TestForest(organDC, organForest, classCountOrgan, dimCount,confidentDataPercentage_organ);
			box[c][0] = box_organ[0][0] + (double)((float)rightVox * voxres[0]);
			box[c][1] = box_organ[0][1] + (double)((float)backVox * voxres[1]);
			box[c][2] = box_organ[0][2] + (double)((float)buttomVox * voxres[2]);
		}
		System.out.println("Training cost: " + Double.toString((double)(System.currentTimeMillis() -  tBeginOrgan) / 1000) + " seconds");
			
		System.out.println("saving result ...");
		String txtfile = args[3];
		LoadPrintData.SaveBoxes(box, txtfile);
	}

}
